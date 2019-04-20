package com.tianhy.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Desc: 请求路径
 * @Author: thy
 * @CreateTime: 2019/3/27
 **/
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";

}

