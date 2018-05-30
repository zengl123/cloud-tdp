package com.zenlin.cloud.tdp.servicce.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.*;
import com.zenlin.cloud.tdp.entity.kpi.KpiSpotArea;
import com.zenlin.cloud.tdp.entity.kpi.KpiTouristRetentionTimeDay;
import com.zenlin.cloud.tdp.enums.IndexName;
import com.zenlin.cloud.tdp.enums.kpi.KPIId;
import com.zenlin.cloud.tdp.enums.TypeName;
import com.zenlin.cloud.tdp.servicce.DeviceSpecificationService;
import com.zenlin.cloud.tdp.servicce.KpiService;
import com.zenlin.cloud.tdp.utils.EsUtils;
import com.zenlin.cloud.tdp.utils.RedisUtils;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/10  9:39.
 */
@Service
public class KpiImpl implements KpiService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EsUtils esUtils;
    @Autowired
    private ResultUtil resultUtil;
    @Autowired
    private DeviceSpecificationService service;


    public KpiImpl() {

    }

    private void init() {

    }

    @Override
    public RestMessage saveKpiSpotArea(JSONObject params) {
        JSONArray data = params.getJSONArray("data");
        List<KpiSpotArea> kpiSpotAreas = data.stream().map(object -> {
            KpiSpotArea kpiSpotArea = JSONObject.parseObject(JSON.toJSONString(object), KpiSpotArea.class);
            return kpiSpotArea;
        }).collect(Collectors.toList());
        EsEntity esBaseEntity = new EsEntity();
        esBaseEntity.setEntityList(kpiSpotAreas);
        esBaseEntity.setIndexName(IndexName.KPI_CMCC_INDEX_NAME.getValue());
        esBaseEntity.setType(TypeName.KPI_CMCC_TYPE_NAME.getValue());
        try {
            return esUtils.saveEntityBatch(esBaseEntity);
        } catch (IOException e) {
            LOGGER.error("kpi区域数据新增异常,error={}", e);
            return resultUtil.error("ERROR");
        } finally {
            redisUtils.setJSONObject(redisTemplate, "spotArea", params, null);
        }
    }


    @Override
    public RestMessage searchAll(JSONObject params) {
        Integer pageNumber;
        Integer pageSize;
        if (null == params) {
            params = new JSONObject();
        }
        try {
            pageNumber = params.getInteger("pageNumber");
            pageSize = params.getInteger("pageSize");
            List<KpiSpotArea> list = esUtils.searchAll(IndexName.KPI_CMCC_INDEX_NAME.getValue(), TypeName.KPI_CMCC_TYPE_NAME.getValue(), pageNumber, pageSize, KpiSpotArea.class);
            return resultUtil.success(list, "SUCCESS");
        } catch (Exception e) {
            LOGGER.error("查询kpi区域异常,error={}", e);
            return resultUtil.error("ERROR");
        }
    }

    @Override
    public RestMessage saveKpiTouristRetentionTimeDay(JSONObject params) {
        return null;
    }


    private List<KpiTouristRetentionTimeDay> kpiTouristRetentionTimeDay() {
        String value = KPIId.KPI_ID_MINUTE.getValue();
        return null;
    }
}
