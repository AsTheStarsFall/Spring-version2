package com.tianhy.spring.demo.aspect;

import com.tianhy.spring.framework.aop.aspect.JoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * {@link}
 *
 * @Desc: 日志监听切面定义
 * @Author: thy
 * @CreateTime: 2019/4/16
 **/
@Slf4j
public class Logaspect {

    //切面前置通知
    public void before(JoinPoint joinPoint) {
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
        //这个方法中的逻辑，是自己定义的
        log.debug("Invoker Before Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //切面后置通知
    public void after(JoinPoint joinPoint) {
        log.debug("Invoker After Method!!!" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = System.currentTimeMillis();
        System.out.println("use time :" + (endTime - startTime));
    }

    //切面异常通知
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.debug("出现异常" +
                "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage());
    }
}
