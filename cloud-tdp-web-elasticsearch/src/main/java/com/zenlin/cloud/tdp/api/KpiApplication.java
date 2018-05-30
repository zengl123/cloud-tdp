package com.zenlin.cloud.tdp.api;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.servicce.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/10  10:20.
 */
@RestControllerAdvice
@RestController
@RequestMapping(value = "/tdp/kpi/")
public class KpiApplication {
    @Autowired
    private KpiService kpiService;


    @RequestMapping(value = "pushSpotArea")
    @ResponseBody
    public RestMessage pushSpotArea(@RequestBody JSONObject params) {
        return kpiService.saveKpiSpotArea(params);
    }

    @PostMapping(value = "searchAll")
    @ResponseBody
    public RestMessage searchAll(@RequestBody(required = false) JSONObject params) {
        return kpiService.searchAll(params);
    }
}
