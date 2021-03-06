package com.huanke.iot.manage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.ControllerAdvice;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//@ServletComponentScan
@EnableSwagger2
@SpringBootApplication
@Configuration
//@EnableRedisHttpSession
@EnableTransactionManagement
@ControllerAdvice
@ImportResource({"classpath:application-context.xml"})
public class ManageStarter {

    @Value("${swaggerUrl}")
    private String swaggerUrl;

    @Value("${env}")
    private String env;

    public static void main(String[] args) {
        SpringApplication.run(ManageStarter.class, args);
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .host(swaggerUrl)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.huanke.iot"))
                .paths("dev".equals(env)?PathSelectors.any():PathSelectors.none())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("manage")
                .description("manage")
                .version("1.0")
                .build();
    }
}
