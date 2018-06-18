package com.huanke.iot.base.innercache;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.huanke.iot.base.dao.impl.publicnumber.PublicNumberMapper;
import com.huanke.iot.base.po.publicnumber.PublicNumberPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class PublicNumberCache {

    @Autowired
    private PublicNumberMapper publicNumberMapper;

    private LoadingCache<String, PublicNumberCacheVo> cache = CacheBuilder.newBuilder().expireAfterWrite(2000, TimeUnit.MILLISECONDS).maximumSize(20480).build(new CacheLoader<String, PublicNumberCacheVo>() {
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
        try {
            return cache.get(host);
        }catch (Exception e){
            log.error("",e);
            return null;
        }
    }
}
