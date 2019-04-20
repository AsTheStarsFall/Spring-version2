package com.tianhy.spring.framework.beans.factory.config;


/**
 * {@link}
 *
 * @Desc: Factory hook that allows for custom modification of new bean instances
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyBeanPostProcessor {

    //为在Bean的初始化前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    //为在Bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
