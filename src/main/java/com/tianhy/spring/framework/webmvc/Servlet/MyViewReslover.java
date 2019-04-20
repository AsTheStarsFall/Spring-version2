package com.tianhy.spring.framework.webmvc.Servlet;


import java.io.File;
import java.util.Locale;

/**
 * {@link}
 *
 * @Desc: 视图解析器
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyViewReslover {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";
    //模板目录
    private File templateRootDir;

    public MyViewReslover(String templateRoot) {
        //每次实例化都执行构造函数
        //获取到模板的路径
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    //处理 并返回一个view
    public MyView resolveViewName(String viewName, Locale locale) throws Exception {
        if(null == viewName || "".equals(viewName.trim())){return null;}
//        if(StringUtils.isBlank(viewName)){return null;}
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX)? viewName : (viewName+DEFAULT_TEMPLATE_SUFFIX);

        //模板路径转换成文件
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        return new MyView(templateFile);
    }
}
