package com.tianhy.spring.framework.webmvc.Servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyView {

    //消息自描述类型 Content-type
    public final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private File templateFile;

    public MyView(File templateFile) {
        this.templateFile = templateFile;
    }

    //读取页面内容，渲染到客户端
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer buff = new StringBuffer();

        RandomAccessFile randomAccess = new RandomAccessFile(this.templateFile, "r");
        String line = null;

        while (null != (line = randomAccess.readLine())) {
            //转成utf-8
            line = new String(line.getBytes("ISO-8859-1"), "utf-8");
            //正则规则  ￥{detail} ^：表示非
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paramName = matcher.group();
                //把 {} 替换掉
                paramName = paramName.replaceAll("￥\\{|\\}", "");
                Object paramValue = model.get(paramName);
                if (paramValue == null) {
                    continue;
                }
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            buff.append(line);
        }
        response.setCharacterEncoding("utf-8");
        //设置消息自描述类型
//        response.setContentType(DEFAULT_CONTENT_TYPE);
        response.getWriter().write(buff.toString());
    }


    //处理特殊字符
    public static String makeStringForRegExp(String str) {
        return str.replace("\\", "\\\\").replace("*", "\\*")
                .replace("+", "\\+").replace("|", "\\|")
                .replace("{", "\\{").replace("}", "\\}")
                .replace("(", "\\(").replace(")", "\\)")
                .replace("^", "\\^").replace("$", "\\$")
                .replace("[", "\\[").replace("]", "\\]")
                .replace("?", "\\?").replace(",", "\\,")
                .replace(".", "\\.").replace("&", "\\&");
    }
}
