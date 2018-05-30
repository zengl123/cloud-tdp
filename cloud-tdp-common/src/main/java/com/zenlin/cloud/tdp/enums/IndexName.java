package com.zenlin.cloud.tdp.enums;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  13:44.
 */
public enum IndexName {

    DEVICE_SPECIFICATION_INDEX_NAME("devicespecification"),
    KPI_CMCC_INDEX_NAME("kpicmcc"),
    PARK_INDEX_NAME("park");

    private String value;

    IndexName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
