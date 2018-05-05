package com.huanke.iot.job;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.util.LocationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class DeviceRemoteJob {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private LocationUtils locationUtils;

    @Scheduled(cron = "0 0/2 * * * ?")
    public void doWork(){
        log.info("start work");
        List<DevicePo> devicePoList = deviceMapper.selectAll();
        devicePoList.forEach(devicePo -> {
            String ip = devicePo.getIp();
            if(StringUtils.isNotEmpty(devicePo.getIp())){
                locationUtils.getLocation(ip,true);
                locationUtils.getWeather(ip,true);
            }
        });
        log.info(" end work");
    }
}
