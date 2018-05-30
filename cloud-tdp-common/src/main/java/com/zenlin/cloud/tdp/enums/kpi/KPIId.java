package com.zenlin.cloud.tdp.enums.kpi;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:26.
 */
public enum KPIId {

    KPI_ID_MINUTE("1001");

    private String value;

    KPIId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
