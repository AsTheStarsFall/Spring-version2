package com.tianhy.spring.framework.webmvc.Servlet;

import lombok.Data;

import java.util.Map;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
@Data
public class MyModelAndView {

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }
    //页面名字
    private String viewName;

    private Map<String,?> model;


    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
