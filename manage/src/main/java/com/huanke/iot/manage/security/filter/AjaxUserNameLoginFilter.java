package com.huanke.iot.manage.security.filter;

import com.huanke.iot.base.api.ApiResponse;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class AjaxUserNameLoginFilter   extends FormAuthenticationFilter {
    protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
        String loginUrl = getLoginUrl();
        WebUtils.issueRedirect(request, response, loginUrl);
    }
}
