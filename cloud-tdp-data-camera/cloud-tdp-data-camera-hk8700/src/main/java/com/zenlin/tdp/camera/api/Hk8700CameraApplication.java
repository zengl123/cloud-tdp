package com.zenlin.tdp.camera.api;

import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.tdp.camera.service.Hk8700CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/17  11:12.
 */
@RestController
@RestControllerAdvice
@RequestMapping(value = "/tdp/camera/")
public class Hk8700CameraApplication {

    @Autowired
    private Hk8700CameraService service;

    @GetMapping(value = "syncCamera8700")
    public RestMessage syncCamera() {
        return service.syncCamera();
    }
}
