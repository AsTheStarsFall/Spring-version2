package com.tianhy.spring.framework.aop.intercept;

import com.tianhy.spring.framework.aop.aspect.JoinPoint;

import java.lang.reflect.Method;
import java.util.*;

/**
 * {@link}
 *
 * @Desc: 反射方法调用
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class MyReflectiveMethodInvocation implements JoinPoint {

    private Object proxy;
    private Object target;
    private Class<?> targetClass;
    private List<Object> interceptorsAndDynamicMethodMatchers;
    private Object[] arguments;
    private Method method;

    private Map<String, Object> userAttribut;

    //定义一个索引，从-1开始来记录当前拦截器执行的位置
    private int currentInterceptorIndex = -1;

    //拦截器调用过程

    public MyReflectiveMethodInvocation(
            Object proxy, Object target, Method method, Object[] arguments,
            Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {

        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
//        this.method = BridgeMethodResolver.findBridgedMethod(method);
//        this.arguments = AopProxyUtils.adaptArgumentsIfNecessary(method, arguments);
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }


    //执行拦截器链
    public Object proceed() throws Throwable {
        int count =0;
//        System.out.println("proceed : " + count++);
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
            return this.method.invoke(this.target, this.arguments);
        }

        Object o = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (o instanceof MyMethodInterceptor) {
            MyMethodInterceptor mi = (MyMethodInterceptor) o;
            return mi.invoke(this);
        }
        return proceed();
    }


    @Override
    public Object getThis() {
        return this.target;
    }

    @Override
    public Object[] getArguments() {
        return this.arguments;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    @Override
    public void setUserAttribute(String key, Object value) {
        if (value != null) {
            if (this.userAttribut == null) {
                userAttribut = new HashMap<>();
            }
            this.userAttribut.put(key, value);

        } else {
            if (this.userAttribut != null) {
                this.userAttribut.remove(key);
            }
        }
    }

    @Override
    public Object getUserAttribute(String key) {
        return this.userAttribut != null ? this.userAttribut.get(key) : null;
    }
}
