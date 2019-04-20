package com.tianhy.spring.framework.core;

/**
 * {@link}
 *
 * @Desc: The root interface for accessing a Spring bean container.
 *        This is the basic client view of a bean container;
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public interface MyBeanFactory {

    /**
     * 根据BeanName 从 IOC 容器获取一个实例bean
     * @param className
     * @return
     */
    Object getBean(String className);

    Object getBean(Class<?> clazz);
}
