package com.huanke.iot.job.devicecheck;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
@Slf4j
public class DevicePowderStatusCheckJob {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostConstruct
    public void init() {
    }


    @Scheduled(cron = "* 0/1 * * * ?")
    public void doWork() {
        updatePowderStatus();
    }


    private void updatePowderStatus() {
        List<DevicePo> deviceIds = deviceMapper.queryAllDeviceId();
        List<DevicePo> updateDevicePos = Lists.newArrayList();
        deviceIds.stream().forEach(devicePo -> {
            Integer id = devicePo.getId();
            String value210 = (String) stringRedisTemplate.opsForHash().get("control2." + id, "210");
            String value2C0 = (String) stringRedisTemplate.opsForHash().get("control2." + id, "2C0");
            DevicePo updatePo = new DevicePo();
            updatePo.setId(devicePo.getId());
            boolean isClose = StringUtils.equals(value210,"0") &&
                    devicePo.getOld() == 0;

             if(devicePo.getOld() == 1){
                 isClose =  StringUtils.equals(value2C0,"0");
             }

            if (isClose) {
                updatePo.setPowerStatus(0);
                updatePo.setOnlineStatus(0);
            } else {
                updatePo.setPowerStatus(1);
                updatePo.setOnlineStatus(1);
            }
            updateDevicePos.add(updatePo);
        });


        deviceMapper.batchUpdateDevice(updateDevicePos);


    }
}
