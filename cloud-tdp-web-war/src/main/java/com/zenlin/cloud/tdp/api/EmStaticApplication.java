package com.zenlin.cloud.tdp.api;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.zenlin.cloud.tdp.service.EmServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 浙江卓锐科技股份有限公司
 *
 * @author
 * @create 2016-09-01 19:37
 * @description 环境监测
 */
@RestController
@RequestMapping(value = "/tdp/v2/")
@Component
public class EmStaticApplication {

    @Autowired
    private EmServiceImpl emService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    @Qualifier(value = "redis10Template")
    private RedisTemplate redisTemplate;

    /**
     * 一个月每周最大值
     *
     * @return
     */
    @RequestMapping(value = "/monthview")
    public RestMessage MonthView(@RequestBody JSONObject reqParam) {
        RestMessage restMessage;
        String coda_site = reqParam.getString("coda_site");
        if (StringUtils.isEmpty(coda_site)) {
            coda_site = reqParam.getString("codaSite");
        }
        try {
            Object object = redisUtils.getObject(redisTemplate, "month" + coda_site);
            if (null != object) {
                restMessage = (RestMessage) object;
            } else {
                restMessage = emService.MonthView(coda_site);
                if (restMessage.isSuccess()) {
                    redisUtils.setObject(redisTemplate, "month" + coda_site, restMessage, null);
                }
            }
            return restMessage;
        } catch (Exception e) {
            return ResultUtils.error("统计异常");
        }
    }
}