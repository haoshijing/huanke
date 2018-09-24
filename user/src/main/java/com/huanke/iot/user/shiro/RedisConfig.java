/*
 *
 *  * COPYRIGHT China Mobile (SuZhou) Software Technology Co.,Ltd. 2017
 *  *
 *  * The copyright to the computer program(s) herein is the property of
 *  * CMSS Co.,Ltd. The programs may be used and/or copied only with written
 *  * permission from CMSS Co.,Ltd. or in accordance with the terms and conditions
 *  * stipulated in the agreement/contract under which the program(s) have been
 *  * supplied.
 *
 */

package com.huanke.iot.user.shiro;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;

/**
 * on 2017/6/16.
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Override
    @Bean
    public KeyGenerator keyGenerator() {

        return new KeyGenerator() {

            @Override
            public Object generate(final Object target, final Method method, final Object... params) {

                final StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (final Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @SuppressWarnings("rawtypes")
    @Bean
    public CacheManager cacheManager(final RedisTemplate redisTemplate) {

        final RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        // 设置缓存过期时间
        // rcm.setDefaultExpiration(60);//秒
        return rcm;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(final RedisConnectionFactory factory) {

        final StringRedisTemplate redisTemplate = new StringRedisTemplate(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }
}
