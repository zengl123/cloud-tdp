package com.zenlin.tdp.camera.utils;

import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.security.Md5Util;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/17  15:19.
 */
public class Hk8700SignUtil {
    /**
     * 8700加密算法
     *
     * @param host
     * @param path
     * @param param
     * @param secret
     * @return
     */
    public static final String postBuildToken(String host, String path, JSONObject param, String secret) {
        StringBuilder url = new StringBuilder();
        url.append(host).append(path);
        String jsonString = JSONObject.toJSONString(param);
        StringBuilder builder = new StringBuilder();
        String md5String = builder.append(path).append(jsonString).append(secret).toString();
        String token = Md5Util.getMd5(md5String);
        url.append("?token=").append(token);
        return url.toString();
    }
}
