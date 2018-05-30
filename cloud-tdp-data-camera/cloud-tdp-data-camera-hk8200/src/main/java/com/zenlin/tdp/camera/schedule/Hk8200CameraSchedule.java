package com.zenlin.tdp.camera.schedule;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.tdp.camera.service.Hk8200CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/10  11:23.
 */
@PropertySource(value = "classpath:HK8200Camera.properties")
@EnableScheduling
@Component
public class Hk8200CameraSchedule {
    @Autowired
    private Hk8200CameraService service;

    @Scheduled(cron = "${HK8200_CAMERA_SCHEDULE}")
    public void schedule() {
        JSONObject params = new JSONObject();
        params.put("factoryModelName", "HK8200_CAMERA");
        service.syncCamera(params);
    }
}
