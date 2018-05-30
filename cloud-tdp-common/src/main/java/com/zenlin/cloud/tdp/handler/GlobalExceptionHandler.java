package com.zenlin.cloud.tdp.handler;


import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.exception.BusinessException;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/22  9:24.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Autowired
    private ResultUtil resultUtil;

    /**
     * 自定义业务异常
     * 拦截处理控制器里对应的异常。
     * 返回给页面200状态码
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public RestMessage handlerGuideException(Exception ex) {
        LOGGER.error("error", ex);
        return resultUtil.error(ex.getMessage());
    }

    /**
     * 服务器参数异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestMessage handlerServerException(Exception ex) {
        LOGGER.error("error", ex);
        return resultUtil.error(ex.getMessage());
    }

    /**
     * 功能描述：
     * 创建时间：2017年4月5日 下午1:31:34
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public RestMessage handlerException(Exception ex) {
        LOGGER.error("error", ex);
        return resultUtil.error("系统异常,请联系管理员 " + ex.getMessage());
    }
}
