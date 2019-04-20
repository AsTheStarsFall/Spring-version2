package com.tianhy.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Desc: 声明自动注入
 * @Author: thy
 * @CreateTime: 2019/3/27
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
