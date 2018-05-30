package com.zenlin.cloud.tdp.enums.kpi;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  11:44.
 */
public enum KPIPath {
    /**
     * 游客滞留时间接口
     */
    RETENTION_TIME("/zjydservice/ZLSJ/V1");

    private String value;

    KPIPath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
