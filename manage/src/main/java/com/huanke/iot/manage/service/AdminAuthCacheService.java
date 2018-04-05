package com.huanke.iot.manage.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.huanke.iot.manage.vo.AdminAuthInfo;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AdminAuthCacheService {

    Cache<String /*token*/,AdminAuthInfo> cache = CacheBuilder.newBuilder().maximumSize(4096)
    .expireAfterAccess(2, TimeUnit.HOURS).build();

    public AdminAuthInfo getByToken(String token){
        return cache.getIfPresent(token);
    }

    public void deleteToken(String token){
        cache.asMap().remove(token);
    }
    public void setTokenCache(String token, AdminAuthInfo adminAuthInfo){
        cache.put(token,adminAuthInfo);
    }
}
