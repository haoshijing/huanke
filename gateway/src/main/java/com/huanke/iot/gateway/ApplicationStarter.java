package com.huanke.iot.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author haoshijing
 * @version 2018年03月26日 16:01
 **/
@SpringBootApplication
@ImportResource({"classpath:application-context.xml"})
public class ApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
