package com.huanke.iot.api.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.huanke.iot.api.requestcontext.UserRequestContextHolder;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class CustomerNameCache {

    @Autowired
    private CustomerMapper customerMapper;

    private LoadingCache<Integer,String> customerName = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).
                    build(new CacheLoader<Integer, String>() {
                        @Override
                        public String load(Integer customerId) throws Exception {
                            CustomerPo customerPo = customerMapper.selectById(customerId);
                            if(customerPo != null){
                                return  customerPo.getPublicName();

                            }
                            return "";
                        }
                    });


    public String getName(Integer customerId){
        return customerName.getUnchecked(customerId);
    }
}
