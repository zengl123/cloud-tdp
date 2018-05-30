package com.zenlin.cloud.tdp.entity.camera;

import com.zenlin.cloud.tdp.entity.BaseEntity;
import io.searchbox.annotations.JestId;
import lombok.Data;

/**
 * 描述:监控分组列表实体类
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/17  11:19.
 */
@Data
public class CameraGroup extends BaseEntity {
    @JestId
    private String id;
    /**
     * 列表编号
     */
    private String cameraGroupNo;
    /**
     * 列表名称
     */
    private String cameraGroupName;
    /**
     * 列表上级编号
     */
    private String cameraGroupParentNo;
    /**
     * 列表上级名称
     */
    private String cameraGroupParentName;
    /**
     * 设备厂家
     */
    private String series;
}
