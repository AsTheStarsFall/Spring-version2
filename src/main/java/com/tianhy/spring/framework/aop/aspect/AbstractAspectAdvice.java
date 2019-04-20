package com.tianhy.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * {@link}
 *
 * @Desc: 切面通知抽象类
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public abstract class AbstractAspectAdvice {

    private Method aspectMethod;
    private Object aspectTarget;

    public AbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    //执行通知方法
    public Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws Throwable {
        //获取切面方法的参数
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        //如果参数为空，直接执行
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        } else {
            //如果参数不为空，给参数赋值
            Object[] args = new Object[parameterTypes.length];
            for (int i = 0; i < args.length; i++) {
                if (parameterTypes[i] == JoinPoint.class) {
                    args[i] = joinPoint;
                } else if (parameterTypes[i] == Throwable.class) {
                    args[i] = tx;
                } else if (parameterTypes[i] == Object.class) {
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);

        }
    }
}
