package com.tianhy.spring.framework.aop.support;

import com.tianhy.spring.framework.aop.aspect.*;
import com.tianhy.spring.framework.aop.config.AopConfig;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class MyAdviseSupport {

    //目标类
    private Class<?> targetClass;

    //目标对象
    private Object target;

    //切面正则
    private Pattern pointCutClassPattern;

    //AOP配置信息
    private AopConfig aopConfig;

    //Cache with Method as key and advisor chain List as value
    //方法缓存,一个方法 对应一个执行链
    private transient Map<Method, List<Object>> methodCache;


    public MyAdviseSupport(AopConfig aopConfig) {
        this.aopConfig = aopConfig;
    }

    //获取目标类
    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    //解析AOP配置文件
    private void parse() {
        //public .* com.tianhy.spring.demo.service..*Service..*(.*)
        System.out.println(aopConfig.getPointCut());
        String pointCut = aopConfig.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //截取从开始到Service结尾
        String pointCutFroClassRegex = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);

        //com.tianhy.spring.demo.service..*Service
        pointCutClassPattern = Pattern.compile("class " + pointCutFroClassRegex.substring(
                pointCutFroClassRegex.lastIndexOf(" ") + 1));

        methodCache = new HashMap<>();
        //AOP配置文件中切面类的表达式 编译成正则
        Pattern pattern = Pattern.compile(pointCut);

        try {

            //获取切面类
            Class<?> aspactClass = Class.forName(this.aopConfig.getAspectClass());

            //保存切面类的方法
            Map<String, Method> aspectMethod = new HashMap<>();
            for (Method method : aspactClass.getMethods()) {
                aspectMethod.put(method.getName(), method);
            }

            //处理原生方法上抛出的异常
            for (Method method : this.targetClass.getMethods()) {
                String methodStr = method.toString();
                if (methodStr.contains("throws")) {
                    //截取throws之前的
                    methodStr = methodStr.substring(0, methodStr.lastIndexOf("throws")).trim();
                }

                //匹配方法名
                Matcher matcher = pattern.matcher(methodStr);
                if (matcher.matches()) {
                    //执行链
                    List<Object> advices = new LinkedList<>();

                    //把每个方法包装成一个interceptor

                    //before
                    if (!(null == aopConfig.getAspectBefore() || "".equals(aopConfig.getAspectBefore()))) {
                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethod.get(aopConfig.getAspectBefore()), aspactClass.newInstance()));
                    }
                    //after
                    if (!(null == aopConfig.getAspectAfter() || "".equals(aopConfig.getAspectAfter()))) {
                        advices.add(new MethodAfterAdviceInterceptor(aspectMethod.get(aopConfig.getAspectAfter()), aspactClass.newInstance()));
                    }
                    //afterThrowing
                    if (!(null == aopConfig.getAspectAfterThrow() || "".equals(aopConfig.getAspectAfterThrow()))) {
//                        advices.add(new MethodBeforeAdviceInterceptor(aspectMethod.get(aopConfig.getAspectAfterThrow()),aspactClass.newInstance()));
                        AfterThrowingAdviceInterceptor afterThrowingAdviceInterceptor =
                                new AfterThrowingAdviceInterceptor(aspectMethod.get(aopConfig.getAspectAfterThrow()), aspactClass.newInstance());
                        afterThrowingAdviceInterceptor.setThrowingName(aopConfig.getAspectAfterThrowingName());
                        advices.add(afterThrowingAdviceInterceptor);
                    }
                    methodCache.put(method, advices);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取拦截器链
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        //根据方法获取到执行链
        List<Object> objects = methodCache.get(method);
        if (objects == null) {
            //根据传入的目标类获取到方法
            Method method1 = targetClass.getMethod(method.getName(), method.getParameterTypes());
            //根据目标类中的方法获取执行链（所有的）
            objects = methodCache.get(method1);

            //保存到执行链中
            this.methodCache.put(method1, objects);
        }
        return objects;
    }

    //匹配正则
    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }

}
