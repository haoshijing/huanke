package com.huanke.iot.user.shiro;

import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ShiroConfiguration {

    @Bean
    public UserRealm getUserRealm() {

        return new UserRealm();
    }

    @Bean
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {

        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public RedisCacheManager redisCacheManager() {

        return new RedisCacheManager();
    }

    @Bean
    public SessionManager sessionManager() {

        final DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setCacheManager(redisCacheManager());
        sessionManager.setGlobalSessionTimeout(60 * 60 * 1000);
        sessionManager.setSessionIdCookieEnabled(true);
        final SimpleCookie cookie = new SimpleCookie("osmcs_id");
        cookie.setHttpOnly(true);
        sessionManager.setSessionIdCookie(cookie);
        return sessionManager;
    }

    /**
     * RedisSessionDAO
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {

        final RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        return redisSessionDAO;
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {

        final DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(redisCacheManager());
        securityManager.setRealm(getUserRealm());
        return securityManager;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor() {

        final AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager());
        return new AuthorizationAttributeSourceAdvisor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {

        final DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {

        final ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        return shiroFilterFactoryBean;
    }
}