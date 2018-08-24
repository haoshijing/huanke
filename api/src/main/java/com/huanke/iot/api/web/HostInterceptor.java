package com.huanke.iot.api.web;

import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.base.cache.CustomerAppCache;
import com.huanke.iot.base.po.customer.CustomerPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HostInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CustomerAppCache customerAppCache;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String host = request.getHeader("host");
        UserRequestContext userRequestContext = UserRequestContextHolder.get();
        String[] hostsArr = host.split("[.]");
        if (hostsArr.length > 0) {
            String webSiteHost = hostsArr[0];
            CustomerPo customerPo =  customerAppCache.getAppId(webSiteHost);
            if(customerPo != null){
                UserRequestContext.CustomerVo customerVo = new UserRequestContext.CustomerVo();
                customerVo.setAppId(customerPo.getAppid());
                customerVo.setCustomerId(customerPo.getId());
                userRequestContext.setCustomerVo(customerVo);
            }else{
                throw new RuntimeException("没有匹配到任何公众号");
            }

        }
        return true;
    }
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserRequestContextHolder.clear();
    }

}
