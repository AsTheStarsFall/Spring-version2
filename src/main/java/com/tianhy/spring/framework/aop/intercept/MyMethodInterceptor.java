package com.tianhy.spring.framework.aop.intercept;

/**
 * {@link}
 *
 * @Desc: Intercepts calls on an interface on its way to the target. Theseare nested "on top" of the target.
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public interface MyMethodInterceptor {

    Object invoke(MyReflectiveMethodInvocation invocation) throws Throwable;
}
