package com.tianhy.spring.framework.beans;

/**
 * {@link}
 *
 * @Desc: Bean的包装器
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyBeanWrapper {

    private Object wrapperInstance;
    private Class<?> wrapperClass;

    public MyBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Object getWrapperInstance() {
        return this.wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return this.wrapperInstance.getClass();
    }
}
