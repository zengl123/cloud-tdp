package com.zenlin.cloud.tdp.utils;

import com.zenlin.cloud.tdp.entity.Pagination;
import com.zenlin.cloud.tdp.entity.RestMessage;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 描述:返回结果工具类
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/17  22:53.
 */
@Component
public class ResultUtil {

    public RestMessage success(Object data, String message) {
        RestMessage restMessage = new RestMessage();
        restMessage.setCode(8200);
        restMessage.setData(data);
        restMessage.setMessage(message);
        return restMessage;
    }

    public RestMessage error(String message) {
        RestMessage restMessage = new RestMessage();
        restMessage.setSuccess(false);
        restMessage.setCode(8500);
        restMessage.setMessage(message);
        return restMessage;
    }

    public static Pagination pagSuccess(List list, String message) {
        Pagination pagination = new Pagination();
        pagination.setSuccess(true);
        pagination.setCode(8200);
        pagination.setData(list);
        pagination.setMessage(message);
        return pagination;
    }

    public static Pagination pagError(String message) {
        Pagination pagination = new Pagination();
        pagination.setSuccess(false);
        pagination.setCode(8500);
        pagination.setMessage(message);
        return pagination;
    }
}
