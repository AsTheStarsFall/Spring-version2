package com.tianhy.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Desc: 声明是Controller
 * @Author: thy
 * @CreateTime: 2019/3/27
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
