package com.tianhy.spring.framework.context;

/**
 * @Desc: Interface to be implemented by any object that wishes to be notified
 * of the {@link MyApplicationContext} that it runs in.
 * @Author: thy
 * @CreateTime: 2019/4/14
 **/
public interface MyApplicationContextAware {
    /**
     * 只要实现了这个接口，就会自动调用setApplicationContext()，将IOC容器注入到目标类
     */

    void setApplicationContext(MyApplicationContext applicationContext);
}
