package com.huanke.iot.job.devicecheck;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.util.LocationUtils;
import com.huanke.iot.job.gateway.MqttSendService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class DevicePowderStatusCheckJob {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @PostConstruct
    public void init(){
    }


    @Scheduled(cron = "0/5 * * * * ?")
    public void doWork(){
        updatePowderStatus();
    }


        private void updatePowderStatus(){
            DevicePo queryPo = new DevicePo();
            queryPo.setPowerStatus(DeviceConstant.POWER_STATUS_YES);
            List<DevicePo> devicePoList = deviceMapper.selectList(queryPo,100000,0);
            List<Integer> ids = devicePoList.stream().filter(devicePo -> {
               String values = (String)stringRedisTemplate.opsForHash().get("control2."+devicePo.getId(),"210");
                return values.equals("0");
            }).map(DevicePo::getId).collect(Collectors.toList());

            if(CollectionUtils.isNotEmpty(ids)) {
                deviceMapper.batchUpdatePowerStatus(ids);
            }

    }
}
