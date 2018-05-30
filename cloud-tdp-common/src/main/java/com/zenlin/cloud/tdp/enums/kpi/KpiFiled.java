package com.zenlin.cloud.tdp.enums.kpi;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/21  15:45.
 */
public enum KpiFiled {

    FACTORY_MODEL_NAME("factoryModelName.keyword");

    private String value;

    KpiFiled(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
