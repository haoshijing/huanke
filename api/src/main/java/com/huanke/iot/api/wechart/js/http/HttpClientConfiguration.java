package com.huanke.iot.api.wechart.js.http;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:52
 **/
@Configuration
public class HttpClientConfiguration {

    @Bean
    public HttpClient initHttpClient(){
        HttpClient httpClient = new HttpClient();
        httpClient.setMaxRequestsQueuedPerDestination(1024);
        httpClient.setConnectTimeout(3000);
        try {
            httpClient.start();
        }catch (Exception e){

        }
        return httpClient;
    }
}
