package com.zenlin.cloud.tdp.enums;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  13:47.
 */
public enum TypeName {
    //设备信息配置数据
    DEVICE_SPECIFICATION_TYPE_NAME("device_specification"),
    //移动运营商区域数据
    KPI_CMCC_TYPE_NAME("kpi_spot_area"),
    //卡口数据
    PARK_BAYONET_RECORD_TYPE_NAME("park_bayonet_record");

    private String value;

    TypeName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
