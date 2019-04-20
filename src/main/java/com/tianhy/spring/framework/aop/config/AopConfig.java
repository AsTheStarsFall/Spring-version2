package com.tianhy.spring.framework.aop.config;

import lombok.Data;

/**
 * {@link}
 *
 * @Desc: 切面定义
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
@Data
public class AopConfig {

    private String pointCut;
    private String aspectBefore;
    private String aspectAfter;
    private String aspectClass;
    private String aspectAfterThrow;
    private String aspectAfterThrowingName;

}
