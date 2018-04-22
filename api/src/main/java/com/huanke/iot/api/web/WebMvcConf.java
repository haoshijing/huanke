package com.huanke.iot.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public  class WebMvcConf extends WebMvcConfigurerAdapter {

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private ProcessInterceptor processInterceptor;

    /**
     * 请求拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册监控拦截器
        registry.addInterceptor(processInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/h5/**")
                .excludePathPatterns(new String[]{"/h5/api/user/auth","/h5/api/getSign"});

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .maxAge(120);
    }
}
