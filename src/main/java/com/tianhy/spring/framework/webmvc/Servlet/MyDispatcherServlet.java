package com.tianhy.spring.framework.webmvc.Servlet;

import com.tianhy.spring.framework.annotation.MyController;
import com.tianhy.spring.framework.annotation.MyRequestMapping;
import com.tianhy.spring.framework.context.MyApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link HttpServlet}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
@Slf4j
public class MyDispatcherServlet extends HttpServlet {

    private final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";


    //应用的上下文
    private MyApplicationContext applicationContext;

    private List<MyHandlerMapping> handlerMappings = new ArrayList<>();

    private Map<MyHandlerMapping, MyHandlerAdapter> handlerMappingAdapters = new ConcurrentHashMap<>();

    private List<MyViewReslover> viewReslovers = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "").replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }


    @Override
    public void init(ServletConfig config) throws ServletException {
        //1、初始化ApplicationContext
        applicationContext = new MyApplicationContext(config.getInitParameter(CONTEXT_CONFIG_LOCATION));

        //2、初始化spring MVC 九大组件（初始化策略）
        initStrategies(applicationContext);

    }

    /**
     * 初始化策略
     */
    private void initStrategies(MyApplicationContext applicationContext) {
        //多文件上传的组件
        initMultipartResolver(applicationContext);
        //初始化本地语言环境
        initLocaleResolver(applicationContext);
        //初始化模板处理器
        initThemeResolver(applicationContext);

        //handlerMapping，必须实现
        initHandlerMappings(applicationContext);
        //初始化参数适配器，必须实现
        initHandlerAdapters(applicationContext);


        //初始化异常拦截器
        initHandlerExceptionResolvers(applicationContext);
        //初始化视图预处理器
        initRequestToViewNameTranslator(applicationContext);


        //初始化视图转换器，必须实现
        initViewResolvers(applicationContext);


        //参数缓存器
        initFlashMapManager(applicationContext);
    }

    //handlerMapping 处理请求路径映射
    private void initHandlerMappings(MyApplicationContext applicationContext) {
        try {
            //遍历所有beanDefinitionNames
            String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();

            for (String beanDefinitionName : beanDefinitionNames) {
                Object bean = applicationContext.getBean(beanDefinitionName);
                Class<?> clazz = bean.getClass();
                if (!clazz.isAnnotationPresent(MyController.class)) { continue; }
                String baseUri = "";
                //Controller的URL
                if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                    MyRequestMapping annotation = clazz.getAnnotation(MyRequestMapping.class);
                    baseUri = annotation.value();
                }
                //method的URL
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(MyRequestMapping.class)) { continue; }
                    MyRequestMapping annotation = method.getAnnotation(MyRequestMapping.class);

                    //优化,避免输入多个 / 或少输入 / 而找不到路径
                    //通过获取到注解的值，与方法上@requestMapping的值拼接，生成一个路径
                    String url = ("/" + baseUri + "/" + annotation.value())
                            .replace("\\*", ".*")
                            .replaceAll("/+", "/");
                    Pattern compile = Pattern.compile(url);
                    this.handlerMappings.add(new MyHandlerMapping(bean, method, compile));
                    log.info("mapped :" + compile + ": " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initHandlerAdapters(MyApplicationContext applicationContext) {
        //将一个request 转变为一个handler

        //一个handlerMapping 对应一个 handlerAdapter
        for (MyHandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerMappingAdapters.put(handlerMapping, new MyHandlerAdapter());
        }
    }

    //初始化视图解析器
    private void initViewResolvers(MyApplicationContext applicationContext) {
        //模板目录
        String templateRoot = applicationContext.getConf().getProperty("template");
        //模板文件的的路径
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File file = new File(templateRootPath);
        String[] list = file.list();
        for (int i = 0; i < list.length; i++) {
            this.viewReslovers.add(new MyViewReslover(templateRoot));
        }
    }

    /**
     * @Description: 处理HttpServletRequest请求路径，并且返回一个handlerMapping
     * @Param: [req]
     * @return: com.tianhy.spring.framework.webmvc.Servlet.MyHandlerMapping
     * @Author: thy
     * @Date: 2019/4/16
     */
    private MyHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextUrl = req.getContextPath();
        //  ‘/+’：多个 ‘/’
        url = url.replaceAll(contextUrl, "").replaceAll("/+", "/");

        for (MyHandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过req的请求的URL匹配handlerMapping
        MyHandlerMapping handler = getHandler(req);

        if (handler == null) {
//            resp.getWriter().write("404 Not Found!");
            processDispatchResult(req, resp, new MyModelAndView("404"));
            return;
        }

        //2、准备调用前的参数
        MyHandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        //3、真正的执行
        MyModelAndView modelAndView = handlerAdapter.handle(req, resp, handler);

        //4、输出
        processDispatchResult(req, resp, modelAndView);

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, MyModelAndView modelAndView) throws Exception {

        //ModelView转换为HTML、OuputStream、json、freemark、veolcity
        if (modelAndView == null) {
            return;
        }
        if (this.viewReslovers.isEmpty()) {
            return;
        }
        for (MyViewReslover viewReslover : this.viewReslovers) {
            MyView myView = viewReslover.resolveViewName(modelAndView.getViewName(), null);
            myView.render(modelAndView.getModel(), req, resp);
            return;
        }
    }

    private MyHandlerAdapter getHandlerAdapter(MyHandlerMapping handler) {
        if (this.handlerMappingAdapters.isEmpty()) {
            return null;
        }
        MyHandlerAdapter handlerAdapter = this.handlerMappingAdapters.get(handler);
        if (handlerAdapter.supports(handler)) {
            return handlerAdapter;
        }
        return null;
    }

    private void initThemeResolver(MyApplicationContext context) {
    }

    private void initLocaleResolver(MyApplicationContext context) {
    }

    private void initMultipartResolver(MyApplicationContext context) {
    }

    private void initHandlerExceptionResolvers(MyApplicationContext applicationContext) {
    }

    private void initRequestToViewNameTranslator(MyApplicationContext applicationContext) {
    }


    private void initFlashMapManager(MyApplicationContext applicationContext) {
    }

}
