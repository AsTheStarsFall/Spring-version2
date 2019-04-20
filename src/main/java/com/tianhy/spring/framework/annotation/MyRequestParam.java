package com.tianhy.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @Desc: 请求参数
 * @Author: thy
 * @CreateTime: 2019/3/27
 **/
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";

}
