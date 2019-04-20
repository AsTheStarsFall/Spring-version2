package com.tianhy.spring.framework.aop;

import com.tianhy.spring.framework.aop.intercept.MyReflectiveMethodInvocation;
import com.tianhy.spring.framework.aop.support.MyAdviseSupport;

import java.lang.reflect.*;
import java.util.List;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class MyJdkDynamicAopProxy implements MyAopProxy, InvocationHandler {


    private MyAdviseSupport adviseSupport;

    public MyJdkDynamicAopProxy(MyAdviseSupport config) {
        this.adviseSupport = config;
    }

    //获取代理类
    @Override
    public Object getProxy() {
        return getProxy(this.adviseSupport.getTargetClass().getClassLoader());
    }

    /**
     * 传入原生的对象,调用getProxy时，就会执行invoke实现动态代理
     * @param classLoader
     * @return
     */
    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader, this.adviseSupport.getTargetClass().getInterfaces(), this);
    }

    //执行被代理对象的方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

//        System.out.println(method);
//        System.out.println(this.adviseSupport.getTargetClass());
        //每个method对应一个执行链
        List<Object> interceptorsAndDynamicInterceptionAdvice =
                this.adviseSupport.getInterceptorsAndDynamicInterceptionAdvice(method, this.adviseSupport.getTargetClass());

        MyReflectiveMethodInvocation reflectiveMethodInvocation =
                new MyReflectiveMethodInvocation(proxy, this.adviseSupport.getTarget(), method, args, this.adviseSupport.getTargetClass(), interceptorsAndDynamicInterceptionAdvice);

        //执行拦截器链
        return reflectiveMethodInvocation.proceed();
    }
}
