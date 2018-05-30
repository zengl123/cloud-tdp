package com.zenlin.cloud.tdp.api;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.DeviceSpecification;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.servicce.DeviceSpecificationService;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:59.
 */
@RestControllerAdvice
@RestController
@RequestMapping(value = "/tdp/deviceSpecification/")
public class DeviceSpecificationApplication {
    @Autowired
    private DeviceSpecificationService service;
    @Autowired
    private ResultUtil resultUtil;

    @RequestMapping(value = "save")
    @ResponseBody
    public RestMessage save(@RequestBody JSONObject params) {
        return service.saveDeviceSpecification(params);
    }

    @GetMapping(value = "queryByFactoryModelName/{factoryModelName}")
    @ResponseBody
    public RestMessage query(@PathVariable(value = "factoryModelName") String factoryModelName) {
        return service.queryByFactoryModelName(factoryModelName);
    }

    @GetMapping(value = "delete/{factoryModelName}")
    @ResponseBody
    public RestMessage deleted(@PathVariable(value = "factoryModelName") String factoryModelName) {
        return service.deletedByFactoryModelName(factoryModelName);
    }
}
