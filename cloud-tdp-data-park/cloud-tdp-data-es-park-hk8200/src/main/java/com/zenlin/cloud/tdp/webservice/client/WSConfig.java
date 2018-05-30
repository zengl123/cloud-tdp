package com.zenlin.cloud.tdp.webservice.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * Created by ThinkPad on 2018/1/23.
 */
@Configuration
public class WSConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.zenlin.cloud.tdp.webservice.server");
        return marshaller;
    }
    @Bean
    public WsClient wsClient(Jaxb2Marshaller marshaller) {
        WsClient client = new WsClient();
        client.setDefaultUri("https://52.109.3.192/bms/services/ThirdBayonetService?wsdl");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
