package com.huanke.iot.api.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
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
public class DeviceAbilityCache {

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceTypeAbilitysMapper deviceTypeAbilitysMapper;
    private LoadingCache<Integer,List<Integer>> abilityCache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).
                    build(new CacheLoader<Integer, List<Integer>>() {
        @Override
        public List<Integer> load(Integer deviceId) throws Exception {

            DevicePo devicePo = deviceMapper.selectById(deviceId);
            if(devicePo != null){
                Integer modelId = devicePo.getModelId();

                DeviceModelPo deviceModelPo = deviceModelMapper.selectById(modelId);

                if(deviceModelPo != null){
                  Integer typeId =   deviceModelPo.getTypeId();

                    List<DeviceTypeAbilitysPo> deviceTypeAbilitysPos = deviceTypeAbilitysMapper.selectByTypeId(typeId);

                    if(CollectionUtils.isNotEmpty(deviceTypeAbilitysPos)){

                        return deviceTypeAbilitysPos.stream().map(
                                DeviceTypeAbilitysPo::getAbilityId
                        ).collect(Collectors.toList());
                    }
                }
            }
            return Lists.newArrayList();
        }
    });


    public List<Integer> getAbilitys(Integer deviceId){
        return abilityCache.getUnchecked(deviceId);
    }

}
