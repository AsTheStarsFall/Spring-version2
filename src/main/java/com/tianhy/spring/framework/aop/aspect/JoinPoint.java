package com.tianhy.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/17
 **/
public interface JoinPoint {

    Object getThis();

    //参数列表
    Object[] getArguments();

    Method getMethod();

    void setUserAttribute(String key, Object value);

    Object getUserAttribute(String key);


}
