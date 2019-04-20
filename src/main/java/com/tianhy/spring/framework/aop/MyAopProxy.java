package com.tianhy.spring.framework.aop;

/**
 * {@link}
 *
 * @Desc: 切面代理接口,有两个实现类 CGlibProxy 和 JDKDynamicProxy
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public interface MyAopProxy {

    Object getProxy();

    //获取代理对象
    Object getProxy(ClassLoader classLoader);
}
