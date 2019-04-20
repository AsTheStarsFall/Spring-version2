package com.tianhy.spring.demo.controller;


import com.tianhy.spring.demo.service.IModifyService;
import com.tianhy.spring.demo.service.IQueryService;
import com.tianhy.spring.framework.annotation.*;
import com.tianhy.spring.framework.webmvc.Servlet.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Desc: 控制层
 * @Author: thy
 * @CreateTime: 2019/3/27
 **/
@MyController
@MyRequestMapping("/tianhy")
public class Controller {

    @MyAutowired
    private IModifyService modifyService;

    @MyAutowired
    private IQueryService queryService;


    @MyRequestMapping("/query")
    public MyModelAndView query(HttpServletRequest req, HttpServletResponse resp, @MyRequestParam("name") String name) {
        String result = name;
        String sname = queryService.query(result);
        return out(resp, sname);
    }

    @MyRequestMapping("/add")
    public MyModelAndView add(HttpServletRequest req, HttpServletResponse resp,
                              @MyRequestParam("name") String name, @MyRequestParam("addr") String addr) {
        String result = null;
        try {
            result = modifyService.add(name, addr);
            return out(resp, result);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            //与500页面对应 ￥{detail}  ￥{stackTrace}
            model.put("detail", e.getCause().getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]",""));
            return new MyModelAndView("500", model);
        }

    }

    @MyRequestMapping("/sub")
    public void add(HttpServletRequest req, HttpServletResponse resp,
                    @MyRequestParam("a") Double a, @MyRequestParam("b") Double b) {
        try {
            resp.getWriter().write(a + "-" + b + "=" + (a - b));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MyModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
