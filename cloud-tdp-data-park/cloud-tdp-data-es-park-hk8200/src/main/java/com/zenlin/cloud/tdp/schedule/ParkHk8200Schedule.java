package com.zenlin.cloud.tdp.schedule;

import com.zenlin.cloud.tdp.webservice.client.WsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/23  10:21.
 */
@EnableScheduling
@Component
public class ParkHk8200Schedule {
    @Autowired
    private WsClient wsClient;

    //@Scheduled(cron = "0 0/1 * * * ?")
    public void saveListParkData() {
        wsClient.syncParkBayonetRecord();
    }
}
