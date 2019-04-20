package com.tianhy.spring.framework.aop.aspect;

import com.tianhy.spring.framework.aop.intercept.MyMethodInterceptor;
import com.tianhy.spring.framework.aop.intercept.MyReflectiveMethodInvocation;

import java.lang.reflect.Method;

/**
 * {@link}
 *
 * @Desc: 前置通知拦截器
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class MethodBeforeAdviceInterceptor extends AbstractAspectAdvice implements Advice, MyMethodInterceptor {


    private JoinPoint joinPoint;

    public MethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    //执行前置通知
    @Override
    public Object invoke(MyReflectiveMethodInvocation invocation) throws Throwable {
        this.joinPoint = invocation;
        before(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return invocation.proceed();
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint, null, null);
    }

}
