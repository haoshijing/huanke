package com.huanke.iot.base.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CustomerAppCache {

    @Autowired
    private CustomerMapper customerMapper;
    private LoadingCache<String, CustomerPo> customerPoLoadingCache = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.DAYS).build(new CacheLoader<String, CustomerPo>() {
        @Override
        public CustomerPo load(String key) throws Exception {
            CustomerPo customerPo = customerMapper.selectBySLD(key);
            return customerPo;
        }
    });

    public CustomerPo getAppId(String host) {
        return customerPoLoadingCache.getUnchecked(host);
    }
}
