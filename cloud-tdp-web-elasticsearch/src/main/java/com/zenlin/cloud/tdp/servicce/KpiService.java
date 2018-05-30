package com.zenlin.cloud.tdp.servicce;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.RestMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/10  9:37.
 */
public interface KpiService {
    /**
     * 保存kpi景点
     *
     * @param params
     * @return
     */
    RestMessage saveKpiSpotArea(JSONObject params);

    /**
     * 查询所有景点
     *
     * @param params
     * @return
     */
    RestMessage searchAll(JSONObject params);

    /**
     * 保存每日滞留人数
     *
     * @param params
     * @return
     */
    RestMessage saveKpiTouristRetentionTimeDay(JSONObject params);
}
