package com.zenlin.cloud.tdp.entity.kpi;

import com.zenlin.cloud.tdp.entity.BaseEntity;
import io.searchbox.annotations.JestId;
import lombok.Data;

import java.util.UUID;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/10  9:18.
 */
@Data
public class KpiTouristRetentionTimeDay extends BaseEntity {
    @JestId
    private String id;

    private Integer kpiValue;
    private Integer kpiHour;
    private String kpiTime;
    private String kpiSpotAreaName;
    private String kpiSpotAreaNo;
}
