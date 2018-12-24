package com.huanke.iot.api.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
public  class WebMvcConf extends WebMvcConfigurerAdapter {

    @Autowired
    private UserInterceptor userInterceptor;

    @Autowired
    private ProcessInterceptor processInterceptor;

    @Autowired
    private AppAuthInterceptor appAuthInterceptor;

    @Autowired
    private DeviceHighSetInterceptor deviceHighSetInterceptor;

    @Autowired
    private HostInterceptor hostInterceptor;

    /**
     * 请求拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册监控拦截器
        registry.addInterceptor(processInterceptor)
                .addPathPatterns("/**");
        /*registry.addInterceptor(hostInterceptor).
                addPathPatterns("/**");*/
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/h5/**")
                .excludePathPatterns(new String[]{"/h5/api/user/appid","/h5/api/user/auth","/h5/api/flushCache","/h5/pay/weixinNotify","/h5/pay/queryResult"});
        registry.addInterceptor(appAuthInterceptor).
                addPathPatterns("/app/**").excludePathPatterns("/app/api/base/setApkInfo",
                "/app/api/base/removeIMeiInfo","/app/api/base/getAppPassword","/app/api/base/obtainApk")
                .excludePathPatterns("/app/api/bind");
        registry.addInterceptor(deviceHighSetInterceptor)
                .addPathPatterns("/h5/high/**")
                .excludePathPatterns("/h5/high/getToken").excludePathPatterns("/h5/high/childDeviceList/**");



    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
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
