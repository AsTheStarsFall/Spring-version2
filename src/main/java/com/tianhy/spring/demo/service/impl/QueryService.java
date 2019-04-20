package com.tianhy.spring.demo.service.impl;

import com.tianhy.spring.demo.service.IQueryService;
import com.tianhy.spring.framework.annotation.MyService;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 查询业务
 */
@MyService
@Slf4j
public class QueryService implements IQueryService {

    /**
     * 查询
     */
    @Override
    public String query(String name) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
        log.debug("这是在业务方法中打印的：" + json);
        return json;
    }

}
