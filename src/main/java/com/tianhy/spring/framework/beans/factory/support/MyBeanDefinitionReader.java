package com.tianhy.spring.framework.beans.factory.support;

import com.tianhy.spring.framework.beans.factory.config.MyBeanDefinition;
import lombok.Data;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * {@link}
 *
 * @Desc: 读取配置文件, 将扫描到的类转化为beanDefinition对象
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
@Data
public class MyBeanDefinitionReader {

    //扫描到的类名
    private List<String> classNames = new ArrayList<>();

    private final String SCAN_PACKAGE = "scanPackage";

    private Properties properties = new Properties();

    public MyBeanDefinitionReader(String... locations) {
        //找到对应的文件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            //加载到内存
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //扫描类
        doScanner(properties.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        //由包路径com.tianhy.controller 转换为文件路径,就是把 . 替换成 /
        //转义的意义在于，java中的路径为字符串，无法识别是否为路径 \\
        // .getClassLoader()
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        //F:\StudyWorkSpaces\Spring-v1\target\classes
        // 遍历当前路径下的所有文件
        for (File file : classPath.listFiles()) {
            //如果是根目录，递归
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                //将扫描到的类名保存
                classNames.add(className);
            }
        }
    }

    /**
     * Load bean definitions from the specified resource locations.
     */
    //把指定的配置文件中扫描的类转换成beanDefinition对象
    public List<MyBeanDefinition> loadBeanDefinitions(String... locations) {

        List<MyBeanDefinition> beanDefinitions = new ArrayList<>();
        //遍历扫描到的bean名称
        for (String className : classNames) {
            try {
                //根据bean创建一个beanClass
                Class<?> beanClass = Class.forName(className);
                //接口不能实例化，让它的实现区实例化
                if (beanClass.isInterface()) {
                    continue;
                }

                beanDefinitions.add(createDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                //去重
//                beanDefinitions.add(createDefinition(beanClass.getName(), beanClass.getName()));

                //接口的实现
                Class<?>[] impls = beanClass.getInterfaces();
                for (Class<?> impl : impls) {
                    beanDefinitions.add(createDefinition(impl.getName(), beanClass.getName()));
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return beanDefinitions;
    }

    //创建beanDefinition。将扫描到的类转换为beanDefinition
    private MyBeanDefinition createDefinition(String factoryBeanName, String name) {
        MyBeanDefinition beanDefinition = new MyBeanDefinition();
        beanDefinition.setBeanName(name);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        // 之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        // 在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

//    public Properties getProperTies(){
//        return this.properties;
//    }
}
