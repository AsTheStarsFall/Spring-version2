package com.tianhy.spring.framework.webmvc.Servlet;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * {@link}
 *
 * @Desc: Interface to be implemented by objects that define a mapping between requests and handler objects
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
@Data
public class MyHandlerMapping {

    private Object controller;
    private Method method;
    private Pattern pattern;

    public MyHandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }
}
