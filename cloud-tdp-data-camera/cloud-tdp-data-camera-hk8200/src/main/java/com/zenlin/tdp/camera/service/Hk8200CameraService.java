package com.zenlin.tdp.camera.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/10  11:18.
 */
public interface Hk8200CameraService {
    /**
     * 同步监控设备
     *
     * @param params
     * @return
     */
    boolean syncCamera(JSONObject params);
}
