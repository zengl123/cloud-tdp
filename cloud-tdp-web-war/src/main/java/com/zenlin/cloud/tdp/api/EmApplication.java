package com.zenlin.cloud.tdp.api;

import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.zenlin.cloud.tdp.schedule.AnalysisSchedule;
import com.zenlin.cloud.tdp.service.EmAnalysisImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

/**
 * 描述:
 * 项目名:cloud-tdp-moudles
 *
 * @Author:ZENLIN
 * @Created 2018/1/3  21:04.
 */
@Controller
@Component
@RequestMapping(value = "/tdp/v2/")
public class EmApplication {
    @Autowired
    private EmAnalysisImpl impl;
    @Autowired
    private AnalysisSchedule analysisSchedule;

    @RequestMapping(value = "day")
    @ResponseBody
    public RestMessage analysisByDay(@RequestBody JSONObject jsonObject) {
        String codaSite = jsonObject.getString("codaSite");
        String date = jsonObject.getString("date");
        if (StringUtils.isEmpty(date)) date = LocalDate.now().toString();
        String beginTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        try {
            return impl.day(beginTime, endTime, codaSite);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("统计异常");
        }
    }

    @RequestMapping(value = "week")
    @ResponseBody
    public RestMessage analysisByWeek(@RequestBody JSONObject jsonObject) {
        String codaSite = jsonObject.getString("codaSite");
        String beginTime = jsonObject.getString("beginTime");
        String endTime = jsonObject.getString("endTime");
        if (StringUtils.isEmpty(beginTime)) beginTime = LocalDate.now().minusDays(7).toString() + " 00:00:00";
        if (StringUtils.isEmpty(endTime)) endTime = LocalDate.now().minusDays(1).toString() + " 23:59:59";
        try {
            return impl.week(beginTime, endTime, codaSite);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("统计异常");
        }
    }

    @RequestMapping(value = "year")
    @ResponseBody
    public RestMessage analysisByYear(@RequestBody JSONObject jsonObject) {
        String codaSite = jsonObject.getString("codaSite");
        String beginTime = jsonObject.getString("beginTime");
        String endTime = jsonObject.getString("endTime");
        if (StringUtils.isEmpty(beginTime)) beginTime = LocalDate.now().getYear() + "-01-01";
        if (StringUtils.isEmpty(endTime)) endTime = LocalDate.now().minusDays(1).toString();
        try {
            return impl.year(beginTime, endTime, codaSite);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("统计异常");
        }
    }

    @RequestMapping(value = "clear")
    @ResponseBody
    public RestMessage clear() {
        try {
            analysisSchedule.clearAnalysis();
            return ResultUtils.success("", "SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error("ERROR");
        }
    }
}
