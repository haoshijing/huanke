package com.huanke.iot.api.web;

import com.huanke.iot.api.requestcontext.UserRequestContext;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.base.innercache.PublicNumberCache;
import com.huanke.iot.base.innercache.PublicNumberCacheVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Repository
public class HostInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private PublicNumberCache publicNumberCache;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String requestUri = request.getRequestURI();
        UserRequestContext userRequestContext = UserRequestContextHolder.get();
        String[] hostsArr = requestUri.split(".");
        if (hostsArr.length > 0) {
            String webSiteHost = hostsArr[0];
            PublicNumberCacheVo publicNumberCacheVo =  publicNumberCache.selectByHost(webSiteHost);
            if(publicNumberCacheVo != null){
                userRequestContext.setCacheVo(publicNumberCacheVo);
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
