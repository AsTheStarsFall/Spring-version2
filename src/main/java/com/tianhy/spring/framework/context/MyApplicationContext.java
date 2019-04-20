package com.tianhy.spring.framework.context;

import com.tianhy.spring.framework.annotation.*;
import com.tianhy.spring.framework.aop.*;
import com.tianhy.spring.framework.aop.config.AopConfig;
import com.tianhy.spring.framework.aop.support.MyAdviseSupport;
import com.tianhy.spring.framework.beans.MyBeanWrapper;
import com.tianhy.spring.framework.beans.factory.config.MyBeanDefinition;
import com.tianhy.spring.framework.beans.factory.config.MyBeanPostProcessor;
import com.tianhy.spring.framework.beans.factory.support.MyBeanDefinitionReader;
import com.tianhy.spring.framework.beans.factory.support.MyDefaultListableBeanFactory;
import com.tianhy.spring.framework.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link MyDefaultListableBeanFactory,MyBeanFactory}
 *
 * @Desc: IOC->DI->MVC->AOP
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {

    private String[] locations;
    private MyBeanDefinitionReader reader;

    public MyApplicationContext(String... locations) {
        this.locations = locations;
        try {

            //入口
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通用的IOC容器
     */
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    /**
     * 单例的IOC容器缓存
     */
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<>();


    @Override
    public void refresh() throws Exception {
        //1、定位配置文件
        reader = new MyBeanDefinitionReader(this.locations);
        //2、加载配置文件，扫描相关的类，封装成beanDefinition
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions(locations);
        //3、放入IOC容器
        registBeanDefinition(beanDefinitions);
        //4、把不是延迟加载的类，提前初始化，就是自动注入
        //DI
        autowired();
    }

    /**
     * 把bean注册到父类 {@link MyDefaultListableBeanFactory}
     *
     * @param beanDefinitions
     * @throws Exception
     */
    private void registBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        for (MyBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("the " + beanDefinition.getFactoryBeanName() + " is exist");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //IOC容器初始化完成，下一步autowired
    }

    /**
     * 只处理非延时加载，默认singleton
     */
    private void autowired() {
        for (Map.Entry<String, MyBeanDefinition> definitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = definitionEntry.getKey();
            definitionEntry.getValue();
            if (!definitionEntry.getValue().isLazyInit()) {
                getBean(beanName);
            }
        }
    }


    /**
     * autosired注入从这里开始，获取beanDefinitionMap的信息，通过反射返回一个实例
     * 在spring中不会返回出去，而是创建一个BeanWrapper对beanDefiniton进行包装
     * 装饰器模式：
     * 1、保留原来的OOP关系
     * 2、我需要对它进行扩展，增强（为了以后AOP打基础）
     */
    @Override
    public Object getBean(String beanName) {
        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        Object instance = null;

        //在spring中：AbstractAutowireCapableBeanFactory
        //mybatis sessionFactory
        MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();

        beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

        //初始化bean
        instance = instantiateBean(beanName, beanDefinition);

        //将实例化对象封装到BeanWrapper中
        MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);

        //将BeanWrapper放入IOC容器
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        beanPostProcessor.postProcessAfterInitialization(instance, beanName);


        //注入    
        populateBean(beanName, new MyBeanDefinition(), beanWrapper);


        return this.factoryBeanInstanceCache.get(beanName).getWrapperInstance();
    }

    private void populateBean(String beanName, MyBeanDefinition beanDefinition, MyBeanWrapper beanWrapper) {
        Object instance = beanWrapper.getWrapperInstance();
        Class<?> clazz = beanWrapper.getWrapperClass();

        //只有加了注解的类注入
        if (!(clazz.isAnnotationPresent(MyController.class) || clazz.isAnnotationPresent(MyService.class))) {
            return;
        }

        //获取字段，给字段赋值
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            //只有加了MyAutowired的属性才自动注入
            if (!field.isAnnotationPresent(MyAutowired.class)) {
                continue;
            }

            //获取注解value，如果没有自定义，就按照它的类型注入
            MyAutowired annotation = field.getAnnotation(MyAutowired.class);

            String autoWiredName = annotation.value().trim();
            if ("".equalsIgnoreCase(autoWiredName)) {
                autoWiredName = field.getType().getName();
            }

            field.setAccessible(true);

            try {
                //bug
                if (this.factoryBeanInstanceCache.get(autoWiredName) == null) {
                    continue;
                }

                //字段赋值
//                System.out.println(field);
//                Object wrapperInstance = this.factoryBeanInstanceCache.get(autoWiredName).getWrapperInstance();
                field.set(instance, this.factoryBeanInstanceCache.get(autoWiredName).getWrapperInstance());
//                System.out.println(field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    //初始化bean的同时，创建切面aop代理
    private Object instantiateBean(String beanName, MyBeanDefinition beanDefinition) {
        //拿到要实例化的类名
        String className = beanDefinition.getBeanName();
        //实例化对象
        Object instance = null;
        try {
            //默认为单例
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //解析AOP配置文件
                MyAdviseSupport adviseSupport = instantionAopConfig(beanDefinition);
                //设置目标对象
                adviseSupport.setTargetClass(clazz);
                adviseSupport.setTarget(instance);

                //如果符合pointCut规则，创建代理对象
                if (adviseSupport.pointCutMatch()) {
                    //调用getProxy() 执行invoke方法
                    instance = createProxy(adviseSupport).getProxy();
                }
//                System.out.println(instance);
                this.singletonObjects.put(className, instance);
                this.singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }


    //创建代理
    private MyAopProxy createProxy(MyAdviseSupport adviseSupport) {

        //获取到代理对象
        Class<?> targetClass = adviseSupport.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new MyJdkDynamicAopProxy(adviseSupport);
        }
        return new MyCglibAopProxy(adviseSupport);
    }


    /**
     * 初始化Aopconfig
     *
     * @param beanDefinition
     * @return
     */
    private MyAdviseSupport instantionAopConfig(MyBeanDefinition beanDefinition) {
        AopConfig aopConfig = new AopConfig();
        //获取AOP配置文件
        aopConfig.setPointCut(this.reader.getProperties().getProperty("pointCut"));
        aopConfig.setAspectClass(this.reader.getProperties().getProperty("aspectClass"));
        aopConfig.setAspectBefore(this.reader.getProperties().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(this.reader.getProperties().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrow(this.reader.getProperties().getProperty("aspectAfterThrow"));
        aopConfig.setAspectAfterThrowingName(this.reader.getProperties().getProperty("aspectAfterThrowingName"));
        //AOP的配置文件保存
        return new MyAdviseSupport(aopConfig);
    }

    @Override
    public Object getBean(Class<?> clazz) {
        return getBean(clazz.getName());
    }

    //获取所有branDefinitionNames
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionMap.size()]);
    }

    public Properties getConf() {
        return this.reader.getProperties();
    }
}
