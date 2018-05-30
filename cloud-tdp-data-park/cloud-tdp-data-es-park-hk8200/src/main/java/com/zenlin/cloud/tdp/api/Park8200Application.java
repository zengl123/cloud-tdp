package com.zenlin.cloud.tdp.api;

import com.zenlin.cloud.tdp.webservice.client.WsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述:
 * 项目名:cloud-tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/4/3  23:01.
 */
@RestController
@RequestMapping("tdp/ws/")
public class Park8200Application {
    @Autowired
    private WsClient wsClient;

    @RequestMapping(value = "login")
    public String login() {
        String login = wsClient.login();
        return login;
    }
    @RequestMapping(value = "listParkData")
    public void listParkData() {
        wsClient.listParkData();
    }
    @RequestMapping(value = "listParkPoint")
    public void listParkPoint() {
        wsClient.listParkPoint();
    }
}
