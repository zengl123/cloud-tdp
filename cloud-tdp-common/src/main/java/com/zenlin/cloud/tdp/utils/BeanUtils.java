package com.zenlin.cloud.tdp.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/12  11:58.
 */
@Component
public class BeanUtils {
    /**
     * 任务线程池
     *
     * @return
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor getPoolTaskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        poolTaskExecutor.setCorePoolSize(5);
        //最大线程数
        poolTaskExecutor.setMaxPoolSize(1000);
        //空闲线程的存活时间
        poolTaskExecutor.setKeepAliveSeconds(30000);
        //队列最大长度
        poolTaskExecutor.setQueueCapacity(200);
        return poolTaskExecutor;
    }
}
