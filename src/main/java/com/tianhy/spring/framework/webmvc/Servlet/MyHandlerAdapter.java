package com.tianhy.spring.framework.webmvc.Servlet;


import com.tianhy.spring.framework.annotation.MyRequestParam;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.*;


/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public class MyHandlerAdapter {

    public boolean supports(Object handler) {
        return (handler instanceof MyHandlerMapping);
    }

    MyModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {

        MyHandlerMapping handlerMapping = (MyHandlerMapping) handler;

        Map<String, Integer> paramIndexMapping = new HashMap<>();
        /**
         * 方法上加了注解的参数
         */
        //拿到方法上的注解
        Annotation[][] anno = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < anno.length; i++) {
            //解析注解@MyRequestParam
            for (Annotation a : anno[i]) {
                if (a instanceof MyRequestParam) {
                    //获取注解的value
                    String value = ((MyRequestParam) a).value();
                    if (StringUtils.isNotBlank(value.trim())) {
                        //参数与位置保存
                        paramIndexMapping.put(value, i);
                    }
                }
            }
        }

        /**
         * req与resp参数
         */
        //拿到方法的形参列表
        Class<?>[] methodParameterTypes = handlerMapping.getMethod().getParameterTypes();

        //遍历方法的形参列表
        for (int i = 0; i < methodParameterTypes.length; i++) {
            //每一个方法的形参
            Class<?> methodParamType = methodParameterTypes[i];
            //判断是否为req/resp类型，拿到参数名称，分别放入paramIndexMapping
            if ((methodParamType == HttpServletRequest.class) || methodParamType == HttpServletResponse.class) {
                paramIndexMapping.put(methodParamType.getName(), i);
            }
        }

        //存放形参的值
        Object[] methodParamtersValues = new Object[methodParameterTypes.length];

        //拿到req请求的参数，一个key对应多个value，所以是个数组
        Map<String, String[]> reqParameterMap = req.getParameterMap();

        //遍历req请求的参数, 给methodParamtersValues赋值
        for (Map.Entry<String, String[]> entry : reqParameterMap.entrySet()) {
            //参数是字符串的形式，因为是个数组所以要把[] 去掉
            //‘\\[|\\]’: '[' 或者']' 都要被替换掉
            String s = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "");
            if (!paramIndexMapping.containsKey(entry.getKey())) {
                continue;
            }
            //entry.getKey() = name
            //参数下标
            int index = paramIndexMapping.get(entry.getKey());
            //参数赋值
            methodParamtersValues[index] = convert(methodParameterTypes[index], s);
        }
        if (paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            methodParamtersValues[reqIndex] = req;
        }
        if (paramIndexMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            methodParamtersValues[respIndex] = resp;
        }

        //执行方法
        Object invoke = handlerMapping.getMethod().invoke(handlerMapping.getController(), methodParamtersValues);
        if (invoke == null || invoke instanceof Void) {
            return null;
        }
        boolean isModelView = handlerMapping.getMethod().getReturnType() == MyModelAndView.class;
        return isModelView ? (MyModelAndView) invoke : null;

    }

    //从url获取到的参数都是string类型,http基于字符串
    //类型转换
    public Object convert(Class<?> type, String value) {
        if (String.class == type) {
            return value;
        }
        if (Integer.class == type) {
            return Integer.valueOf(value);
        } else if (Double.class == type) {
            return Double.valueOf(value);
        }

        return value;
    }

}
