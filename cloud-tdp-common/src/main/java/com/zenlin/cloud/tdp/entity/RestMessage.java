package com.zenlin.cloud.tdp.entity;

import lombok.Data;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/17  22:54.
 */
@Data
public class RestMessage {
    private boolean success = true;
    private Object data;
    private Integer Code;
    private String message;
}
