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
    public void init(){
    }


    @Scheduled(cron = "* 0/1 * * * ?")
    public void doWork(){
        updatePowderStatus();
    }


        private void updatePowderStatus(){
            DevicePo queryPo = new DevicePo();
            List<Integer> deviceIds = deviceMapper.queryAllDeviceId();
            List<DevicePo> updateDevicePos = Lists.newArrayList();
            deviceIds.stream().forEach(id -> {
                String value210 = (String)stringRedisTemplate.opsForHash().get("control2."+devicePo.getId(),"210");
                String value2C0 = (String)stringRedisTemplate.opsForHash().get("control2."+devicePo.getId(),"2C0");
                DevicePo updatePo = new DevicePo();
                updatePo.setId(id);
               if(StringUtils.equalsIgnoreCase(value210,"0")  || StringUtils.equalsIgnoreCase(value2C0,"0")){

                   updatePo.setPowerStatus(0);
               }else{
                   updatePo.setPowerStatus(1);
               }
               log.info(" deviceid = {}, powerStatus = {}",id,updatePo.getPowerStatus());
                updateDevicePos.add(updatePo);
            });


            deviceMapper.batchUpdateDevice(updateDevicePos);


    }
}
