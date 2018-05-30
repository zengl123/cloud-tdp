package com.zenlin.cloud.tdp.schedule;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.api.EmApplication;
import com.zenlin.cloud.tdp.api.EmStaticApplication;
import com.zenlin.cloud.tdp.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/19  14:28.
 */
@EnableScheduling
@Component
@PropertySource(value = "classpath:config.properties")
public class AnalysisSchedule {
    @Autowired
    private EmApplication emApplication;
    @Autowired
    private EmStaticApplication emStaticApplication;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    @Qualifier(value = "redis10Template")
    private RedisTemplate redisTemplate;
    @Value("${pointDevices}")
    private String pointDevices;

    @Scheduled(cron = "${clearAnalysis}")
    public void clearAnalysis() {
        redisUtils.removeByPattern(redisTemplate, "week*");
        redisUtils.removeByPattern(redisTemplate, "month*");
        redisUtils.removeByPattern(redisTemplate, "year*");
        List<String> list = Arrays.asList(pointDevices.split(","));
        for (int i = 0; i < list.size(); i++) {
            JSONObject params = new JSONObject();
            params.put("codaSite", list.get(i));
            try {
                emApplication.analysisByWeek(params);
                Thread.sleep(5000L);
                emStaticApplication.MonthView(params);
                Thread.sleep(5000L);
                emApplication.analysisByYear(params);
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

