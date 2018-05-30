package com.zenlin.cloud.tdp.entity.camera;

import com.zenlin.cloud.tdp.entity.BaseEntity;
import io.searchbox.annotations.JestId;
import lombok.Data;

/**
 * 描述:监控点实体类
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/17  11:22.
 */
@Data
public class CameraDevice extends BaseEntity {
    @JestId
    private String id;
    /**
     * 设备编号
     */
    private String deviceNo;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备编码
     */
    private String indexCode;
    /**
     * 设备IP地址
     */
    private String deviceIP;
    /**
     * 设备端口号
     */
    private Integer devicePort;
    /**
     * 设备在线状态
     */
    private Integer isOnline;
    /**
     * 设备存在状态
     */
    private Integer isDeleted;
    /**
     * 设备通道号
     */
    private Integer deviceChannelNo;
    /**
     * 设备预览参数
     */
    private String previewParameters;
    /**
     * 设备回放参数
     */
    private String PlaybackParameters;
    /**
     * 设备上级编号
     */
    private String deviceParentNo;
}
