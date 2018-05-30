package com.zenlin.cloud.tdp.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/15  12:30.
 */
@Data
public class BaseEntity {
    private String createTime;
    private String modifyTime = LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private long defaultTime = System.currentTimeMillis();
}
