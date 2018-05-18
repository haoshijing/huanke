package com.huanke.iot.job.stat;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.dao.impl.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author haoshijing
 * @version 2018年05月16日 20:27
 **/

@Repository
@Slf4j
public class DeviceSensorStatWorker {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(16,
            new DefaultThreadFactory("StatSensorThread"));

    @Scheduled(cron = "0 10/30 * * * ?")
    public void doWork() {
        log.info("DeviceSensorStatWorker start work");
        DevicePo queryPo = new DevicePo();
        int offset = 0;
        List<DevicePo> devicePoList = deviceMapper.selectList(queryPo, 100, offset);
        do {
            final Long startTime = new DateTime().withMillisOfSecond(0).plusMinutes(-40).getMillis();
            final Long endTime = new DateTime().withMillisOfSecond(0).plusMinutes(-40).getMillis();
            devicePoList.forEach(devicePo -> {
                Future<Integer> co2 = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(), startTime, endTime, SensorTypeEnums.CO2_IN.getCode()));
                Future<Integer>  hcho = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.HCHO_IN.getCode()));
                Future<Integer>  tvoc = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.TVOC_IN.getCode()));
                Future<Integer>  hum = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.HUMIDITY_IN.getCode()));
                Future<Integer>  tem = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.TEMPERATURE_IN.getCode()));
                Future<Integer>  pm = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.PM25_IN.getCode()));

                DeviceSensorStatPo deviceSensorStatPo = new DeviceSensorStatPo();
                deviceSensorStatPo.setDeviceId(devicePo.getId());
                deviceSensorStatPo.setStartTime(startTime);
                deviceSensorStatPo.setEndTime(endTime);
                deviceSensorStatPo.setInsertTime(System.currentTimeMillis());
                try {
                    deviceSensorStatPo.setCo2(co2.get());
                    deviceSensorStatPo.setHum(hum.get());
                    deviceSensorStatPo.setPm(pm.get());
                    deviceSensorStatPo.setHcho(hcho.get());
                    deviceSensorStatPo.setTvoc(tvoc.get());
                    deviceSensorStatPo.setTem(tem.get());
                }catch (Exception e){

                }
                try {
                    deviceSensorStatMapper.insert(deviceSensorStatPo);
                }catch (Exception e){
                    log.error("",e);
                }
            });

            offset += devicePoList.size();
        } while (devicePoList.size() > 0);
        log.info("DeviceSensorStatWorker end work");
    }

    @AllArgsConstructor
    @NoArgsConstructor
    class QueryTask implements Callable<Integer> {

        private Integer deviceId;
        private Long startTime;
        private Long endTime;
        private String type;

        @Override
        public Integer call() throws Exception {
            Integer data = deviceSensorDataMapper.selectAvgData(deviceId, startTime, endTime,
                    Integer.valueOf(type));
            if (data == null) {
                data = 0;
            }
            return data;
        }
    }
}
