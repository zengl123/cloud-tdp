package com.zenlin.cloud.tdp.servicce;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.DeviceSpecification;
import com.zenlin.cloud.tdp.entity.RestMessage;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:53.
 */
public interface DeviceSpecificationService {
    RestMessage saveDeviceSpecification(JSONObject params);

    RestMessage queryByFactoryModelName(String factoryModelName);

    RestMessage deletedByFactoryModelName(String factoryModelName);
}
