package com.zenlin.cloud.tdp.enums;

/**
 * 描述:redisKey名称
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/23  10:56.
 */
public enum RedisKey {

    PARK_BAYONET_RECORD_SYNC_TIME("PARK_BAYONET_RECORD_SYNC_TIME");

    private String value;

    RedisKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
