package com.tianhy.spring.framework.aop;

import com.tianhy.spring.framework.aop.support.MyAdviseSupport;

/**
 * {@link}
 *
 * @Desc:
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
public class MyCglibAopProxy implements MyAopProxy {
    public MyCglibAopProxy(MyAdviseSupport adviseSupport) {

    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
