package com.tianhy.spring.framework.beans.factory.support;


import com.tianhy.spring.framework.beans.factory.config.MyBeanDefinition;
import com.tianhy.spring.framework.context.support.MyAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link MyBeanDefinition,MyAbstractApplicationContext}
 *
 * @Desc: Map of bean definition objects, keyed by bean name
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext {
    /**
     * 存储Bean对象，key值为beanName
     * 存储注册信息的BeanDefinition,伪IOC容器
     */
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
