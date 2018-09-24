package com.huanke.iot.user.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class ShiroCache<K, V> implements Cache<K, V> {

    private static final String REDIS_SHIRO_CACHE = "osmcs-shiro-cache:";

    private final String cacheKey;

    private final RedisTemplate<K, V> redisTemplate;

    private static final long globExpire = 30;

    @SuppressWarnings("rawtypes")
    public ShiroCache(final String name, final RedisTemplate client) {
        this.cacheKey = REDIS_SHIRO_CACHE + name + ":";
        this.redisTemplate = client;
    }

    @Override
    public V get(final K key) throws CacheException {

        redisTemplate.boundValueOps(getCacheKey(key)).expire(globExpire, TimeUnit.MINUTES);
        return redisTemplate.boundValueOps(getCacheKey(key)).get();
    }

    @Override
    public V put(final K key, final V value) throws CacheException {

        final V old = get(key);
        redisTemplate.boundValueOps(getCacheKey(key)).set(value);
        return old;
    }

    @Override
    public V remove(final K key) throws CacheException {

        final V old = get(key);
        redisTemplate.delete(getCacheKey(key));
        return old;
    }

    @Override
    public void clear() throws CacheException {

        redisTemplate.delete(keys());
    }

    @Override
    public int size() {

        return keys().size();
    }

    @Override
    public Set<K> keys() {

        return redisTemplate.keys(getCacheKey("*"));
    }

    @Override
    public Collection<V> values() {

        final Set<K> set = keys();
        final List<V> list = new ArrayList<>();
        for (final K s : set) {
            list.add(get(s));
        }
        return list;
    }

    private K getCacheKey(final Object k) {

        return (K) (this.cacheKey + k);
    }

}