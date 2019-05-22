package com.huanke.iot.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

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
    @Value("${swaggerUrl}")
    private String swaggerUrl;

    @Value("${env}")
    private String env;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .host(swaggerUrl)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.huanke.iot"))
                .paths("dev".equals(env)? PathSelectors.any():PathSelectors.none())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("api")
                .description("api")
                .version("1.0")
                .build();
    }
}
