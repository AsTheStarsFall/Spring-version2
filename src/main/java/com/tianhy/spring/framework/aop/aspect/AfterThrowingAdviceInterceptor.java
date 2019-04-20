package com.tianhy.spring.framework.aop.aspect;

import com.tianhy.spring.framework.aop.intercept.MyMethodInterceptor;
import com.tianhy.spring.framework.aop.intercept.MyReflectiveMethodInvocation;

import java.lang.reflect.Method;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class AfterThrowingAdviceInterceptor extends AbstractAspectAdvice implements Advice,MyMethodInterceptor {

    String throwingName;

    public AfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyReflectiveMethodInvocation invocation) throws Throwable {
        try {

            return invocation.proceed();
        }catch (Throwable throwable){
            invokeAdviceMethod(invocation,null,throwable.getCause());
            throw throwable;
        }
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }
}
