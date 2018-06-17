package com.huanke.iot.base.innercache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.huanke.iot.base.dao.impl.publicnumber.PublicNumberMapper;
import com.huanke.iot.base.po.publicnumber.PublicNumberPo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class PublicNumberCache {

    @Autowired
    private PublicNumberMapper publicNumberMapper;

    private Cache<String, PublicNumberCacheVo> cache = CacheBuilder.newBuilder().expireAfterWrite(2000, TimeUnit.MILLISECONDS).maximumSize(20480).build(new CacheLoader<String, PublicNumberCacheVo>() {
        @Override
        public PublicNumberCacheVo load(String host) throws Exception {
            PublicNumberCacheVo cacheVo = new PublicNumberCacheVo();
            PublicNumberPo publicNumberPo = publicNumberMapper.selectByHost(host);
            if(publicNumberPo != null){
                BeanUtils.copyProperties(publicNumberPo, cacheVo);
                return cacheVo;
            }
            return null;
        }
    });

    public PublicNumberCacheVo selectByHost(String host){
        return cache.getIfPresent(host);
    }
}
