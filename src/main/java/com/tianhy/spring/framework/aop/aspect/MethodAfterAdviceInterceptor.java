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
public class MethodAfterAdviceInterceptor extends AbstractAspectAdvice implements Advice,MyMethodInterceptor {

    private JoinPoint joinPoint;
    public MethodAfterAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MyReflectiveMethodInvocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        this.joinPoint = invocation;
        this.after(proceed,invocation.getMethod(),invocation.getArguments(),invocation.getThis());
        return proceed;
    }

    private void after(Object value,Method method,Object[] args,Object aThis) throws Throwable{
        super.invokeAdviceMethod(this.joinPoint,value,null);

    }
}
