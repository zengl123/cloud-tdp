package com.zenlin.cloud.tdp.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.servicce.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/15  15:32.
 */
@EnableScheduling
@Component
public class EsSchedule {

    @Autowired
    private KpiService service;

    //@Scheduled(cron = "0/2 * * * * ?")
    public void test() {
        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        data.put("data", jsonArray);
        for (int i = 0; i < 1000; i++) {
            JSONObject object = new JSONObject();
            object.put("kpiSpotAreaName", "方岩天街——胡公殿");
            object.put("kpiSpotAreaNo", "16831");
            jsonArray.add(object);
        }
        service.saveKpiSpotArea(data);
    }
}
