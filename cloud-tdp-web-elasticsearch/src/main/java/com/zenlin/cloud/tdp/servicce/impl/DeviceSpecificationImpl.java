package com.zenlin.cloud.tdp.servicce.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.DeviceSpecification;
import com.zenlin.cloud.tdp.entity.EsEntity;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.enums.kpi.KpiFiled;
import com.zenlin.cloud.tdp.enums.IndexName;
import com.zenlin.cloud.tdp.enums.TypeName;
import com.zenlin.cloud.tdp.servicce.DeviceSpecificationService;
import com.zenlin.cloud.tdp.utils.EsUtils;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:54.
 */
@Service
public class DeviceSpecificationImpl implements DeviceSpecificationService {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private EsUtils esUtils;
    @Autowired
    private ResultUtil resultUtil;

    @Override
    public RestMessage saveDeviceSpecification(JSONObject params) {
        JSONArray array = params.getJSONArray("data");
        List<DeviceSpecification> deviceSpecifications = array.stream().map(object -> {
            DeviceSpecification deviceSpecification = JSONObject.parseObject(JSON.toJSONString(object), DeviceSpecification.class);
            RestMessage restMessage = queryByFactoryModelName(deviceSpecification.getFactoryModelName());
            if (!restMessage.isSuccess()) {
                return null;
            }
            DeviceSpecification queryByFactoryModelName = (DeviceSpecification) restMessage.getData();
            if (null == queryByFactoryModelName) {
                deviceSpecification.setCreateTime(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            } else {
                deviceSpecification.setId(queryByFactoryModelName.getId());
                deviceSpecification.setCreateTime(queryByFactoryModelName.getCreateTime());
            }
            return deviceSpecification;
        }).filter(deviceSpecification -> null != deviceSpecification).collect(Collectors.toList());
        EsEntity esEntity = new EsEntity();
        esEntity.setIndexName(IndexName.DEVICE_SPECIFICATION_INDEX_NAME.getValue());
        esEntity.setType(TypeName.DEVICE_SPECIFICATION_TYPE_NAME.getValue());
        esEntity.setEntityList(deviceSpecifications);
        try {
            return esUtils.saveEntityBatch(esEntity);
        } catch (IOException e) {
            LOGGER.error("设备信息数据新增异常,error={}", e);
            return resultUtil.error("ERROR");
        }
    }

    @Override
    public RestMessage queryByFactoryModelName(String factoryModelName) {
        try {
            List<DeviceSpecification> entity = esUtils.findEntity(KpiFiled.FACTORY_MODEL_NAME.getValue(), factoryModelName, IndexName.DEVICE_SPECIFICATION_INDEX_NAME.getValue(), TypeName.DEVICE_SPECIFICATION_TYPE_NAME.getValue(), DeviceSpecification.class);
            DeviceSpecification deviceSpecification;
            if (CollectionUtils.isNotEmpty(entity)) {
                deviceSpecification = entity.get(0);
            } else {
                deviceSpecification = null;
            }
            return resultUtil.success(deviceSpecification, "SUCCESS");
        } catch (IOException e) {
            LOGGER.error("设备信息查询异常,error={}", e.getMessage());
            return resultUtil.error("ERROR");
        }
    }

    @Override
    public RestMessage deletedByFactoryModelName(String factoryModelName) {
        RestMessage restMessage = queryByFactoryModelName(factoryModelName);
        if (!restMessage.isSuccess()) {
            return resultUtil.error("ERROR");
        }
        DeviceSpecification deviceSpecification = restMessage.getData() == null ? new DeviceSpecification() : (DeviceSpecification) restMessage.getData();
        try {
            String deleteDocumentById = esUtils.deleteDocumentById(IndexName.DEVICE_SPECIFICATION_INDEX_NAME.getValue(), TypeName.DEVICE_SPECIFICATION_TYPE_NAME.getValue(), deviceSpecification.getId());
            return resultUtil.success(deleteDocumentById, "SUCCESS");
        } catch (IOException e) {
            LOGGER.error("设备信息删除异常,error={}", e);
            return resultUtil.error("ERROR");
        }
    }
}
