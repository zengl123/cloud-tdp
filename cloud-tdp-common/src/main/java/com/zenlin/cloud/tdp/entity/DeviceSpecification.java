package com.zenlin.cloud.tdp.entity;

import com.alibaba.fastjson.JSONObject;
import io.searchbox.annotations.JestId;
import lombok.Data;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:50.
 */
@Data
public class DeviceSpecification extends BaseEntity {
    @JestId
    private String id;

    private String factoryModelName;
    private JSONObject config;
}
