package com.tianhy.spring.framework.beans.factory.config;


import lombok.Data;

/**
 * {@link}
 *
 * @Desc: A BeanDefinition describes a bean instance, which has property values
 *        BeanDefinition描述了一个bean实例
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
@Data
public class MyBeanDefinition {

    private String beanName;
    private String factoryBeanName;
    private boolean isLazyInit = false;
    private boolean isSingleton = true;
}
