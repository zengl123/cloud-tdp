package com.zenlin.cloud.tdp.entity.park;

import com.zenlin.cloud.tdp.entity.BaseEntity;
import io.searchbox.annotations.JestId;
import lombok.Data;

/**
 * 描述:卡口实体类
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/23  9:43.
 */
@Data
public class ParkBayonetRecord extends BaseEntity {
    @JestId
    private String id;

    private String crossingIndexCode;
    private String crossingName;
    private String laneId;
    private String laneName;
    private String directionName;
    private String plateInfo;
    private String plateType;
    private String passTime;
    private String vehicleType;
    private String picVehicle;
    private String alarmAction;
    private String alarmActionName;
    private String vehicleLogo;
    private String vehicleSubLogoName;
    private String vehicleModel;
    private String vehicleModelName;
    private String vehicleColor;
}
