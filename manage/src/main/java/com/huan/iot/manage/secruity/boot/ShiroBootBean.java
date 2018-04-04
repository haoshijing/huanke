package com.huan.iot.manage.secruity.boot;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author haoshijing
 * @version 2018年04月04日 17:22
 **/
@Configuration
public class ShiroBootBean {

    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setSessionManager( sessionManager());
        defaultWebSecurityManager.setCacheManager(cacheManager());
        return  defaultWebSecurityManager;
    }

    public SessionDAO sessionDAO(){
        return null;
    }
    @Bean
    public CacheManager cacheManager(){
        return  null;
    }

    @Bean
    public SessionManager sessionManager(){
       return null;
    }

    @Bean
    public AuthenticatingRealm real(){
        return  null;
    }
}
