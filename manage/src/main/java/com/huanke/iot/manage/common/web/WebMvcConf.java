package com.huanke.iot.manage.common.web;

import com.huanke.iot.manage.common.inteceptor.AuthInterceptor;
import com.huanke.iot.manage.common.inteceptor.ProcessInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author albert
 * @package com.xianduankeji.ktv.portal.web
 * @email cn.lu.duke@gmail.com
 * @date January 14, 2018
 */
@Configuration
@Slf4j
public class WebMvcConf extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private ProcessInterceptor processInterceptor;

    /**
     * 请求拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册监控拦截器
        registry.addInterceptor(processInterceptor)
                .addPathPatterns("/**")
                .addPathPatterns("/api/device/*");
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**").
                excludePathPatterns("/api/device/otaDevice").
                excludePathPatterns("/api/device/updateDeviceId").
                excludePathPatterns("/api/device/resetPid").
                excludePathPatterns("/api/device/upload", "/api/upload");
    }
}
