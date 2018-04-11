package com.huanke.iot.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * @author haoshijing
 * @version 2018年03月29日 16:28
 **/
@SpringBootApplication
@ImportResource({"classpath:application-context.xml"})
public class ApiApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplicationStarter.class, args);
    }

}
