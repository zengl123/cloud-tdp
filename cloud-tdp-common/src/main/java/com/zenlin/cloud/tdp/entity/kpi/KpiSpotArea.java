package com.zenlin.cloud.tdp.entity.kpi;

import com.zenlin.cloud.tdp.entity.BaseEntity;
import io.searchbox.annotations.JestId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @author ZENLIN
 * @Created 2018/5/10  10:02.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiSpotArea extends BaseEntity {
    @JestId
    private String id;

    /**
     * kpi区域名称
     */
    private String kpiSpotAreaName;
    /**
     * kpi区域编号
     */
    private String kpiSpotAreaNo;
    /**
     * kpi区域父编号
     */
    private String kpiParentSpotAreaNo;
    /**
     * kpi区域父名称
     */
    private String kpiParentSpotAreaName;
    /**
     * kpi厂商
     */
    private String kpiSeries;
}
