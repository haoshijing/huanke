package com.huanke.iot.user.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

public class RedisCacheManager implements CacheManager {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <K, V> Cache<K, V> getCache(final String name) throws CacheException {

        return new ShiroCache<K, V>(name, redisTemplate);
    }

    public RedisTemplate<String, Object> getRedisTemplate() {

        return redisTemplate;
    }

    public void setRedisTemplate(final RedisTemplate<String, Object> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

}