package com.huanke.iot.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.ControllerAdvice;

@SpringBootApplication
@Configuration
@ControllerAdvice
@ImportResource({"classpath:application-context.xml"})
public class ManageStarter {
    public static void main(String[] args) {
        SpringApplication.run(ManageStarter.class, args);
    }
}
