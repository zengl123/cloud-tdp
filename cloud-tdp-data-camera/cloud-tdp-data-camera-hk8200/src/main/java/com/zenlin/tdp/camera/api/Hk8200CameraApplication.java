package com.zenlin.tdp.camera.api;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import com.zenlin.tdp.camera.service.Hk8200CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/12  10:51.
 */
@RestControllerAdvice
@RestController
@RequestMapping(value = "tdp/data/")
public class Hk8200CameraApplication {
    @Autowired
    private Hk8200CameraService service;
    @Autowired
    private ResultUtil resultUtil;

    @RequestMapping(value = "syncCamera/{factoryModelName}", method = RequestMethod.GET)
    public RestMessage syncCamera(@PathVariable String factoryModelName) {
        JSONObject params = new JSONObject();
        params.put("factoryModelName", factoryModelName);
        boolean syncCamera = service.syncCamera(params);
        return resultUtil.success(syncCamera, "SUCCESS");
    }
}
