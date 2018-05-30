package com.zenlin.cloud.tdp.entity;


import io.searchbox.annotations.JestId;
import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/14  17:01.
 */
@Data
public class EsEntity<T> {
    @JestId
    private String id;

    private String indexName;
    private String type;
    //批量插入的时候应用
    private List<T> entityList;
    //单个对象
    private T entity;
}
