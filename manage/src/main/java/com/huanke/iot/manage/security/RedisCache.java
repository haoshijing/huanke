package com.huanke.iot.manage.security;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * @author haoshijing
 * @version 2018年04月04日 17:35
 **/
public class RedisCache implements Cache<String,String>{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String get(String key) throws CacheException {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public String put(String key, String value) throws CacheException {
        stringRedisTemplate.opsForValue().set(key,value);
        return value;
    }

    @Override
    public String remove(String key) throws CacheException {
        stringRedisTemplate.delete(key);
        return key;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<String> keys() {
        return null;
    }

    @Override
    public Collection<String> values() {
        return null;
    }
}
