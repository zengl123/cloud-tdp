package com.zenlin.cloud.tdp.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/22  9:24.
 */
@Data
public class Pagination<T> implements Serializable {
    private Boolean success = true;
    private int count = 0;
    private int pageSize = 10;
    private int currentPage = 1;
    private List<T> data;
    private int totalPage;
    private Integer code;
    private String message;
}
