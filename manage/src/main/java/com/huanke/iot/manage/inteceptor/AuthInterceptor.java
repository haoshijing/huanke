package com.huanke.iot.manage.inteceptor;


import com.google.common.collect.Lists;
import com.huanke.iot.manage.service.AdminAuthCacheService;
import com.huanke.iot.manage.vo.AdminAuthInfo;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Repository
public class AuthInterceptor extends HandlerInterceptorAdapter {
    private static  final String TOKEN = "X-TOKEN";

    @Autowired
    private AdminAuthCacheService adminAuthCacheService;

    private List<String> whiteUrlList = Lists.newArrayList("/login","/content/upload");
    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String token = request.getHeader(TOKEN);
        boolean preLogin = true;
        if(StringUtils.isEmpty(token)){
            preLogin = isPassUrl(request);
        }else {
            AdminAuthInfo adminAuthInfo = adminAuthCacheService.getByToken(token);
            if (adminAuthInfo == null) {
                preLogin = false;
            }
        }
        if(!preLogin){
            response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code",-1);
            jsonObject.put("msg","你的登录已失效");
            response.getWriter().print(jsonObject);
        }
        return preLogin;
    }

    private boolean isPassUrl(HttpServletRequest request){
        String requestUri = request.getRequestURI();
        for(String writeUrl : whiteUrlList){
            if(requestUri.contains(writeUrl)){
                return  true;
            }
        }
        return false;
    }

}
