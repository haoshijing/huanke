package com.huanke.iot.job.stat;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorWarnMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorWarnPo;
import com.huanke.iot.base.po.project.ProjectJobInfo;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    private DeviceSensorWarnMapper deviceSensorWarnMapper;
    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;
    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;
    @Autowired
    private RedisTemplate<Object,Object> template;


    private DefaultEventExecutorGroup defaultEventExecutorGroup = new DefaultEventExecutorGroup(16,
            new DefaultThreadFactory("StatSensorThread"));

    @Scheduled(cron = "0 10/30 * * * ?")
    public void doWork() {
        log.info("DeviceSensorStatWorker start work");
        DevicePo queryPo = new DevicePo();
        int offset = 0;
        List<DevicePo> devicePoList = deviceMapper.selectList(queryPo, 100, offset);
        final Long startTime = new DateTime().withMillisOfSecond(0).plusMinutes(-40).getMillis();
        final Long endTime = new DateTime().withMillisOfSecond(0).plusMinutes(-10).getMillis();
        do {
            devicePoList.forEach(devicePo -> {
                //未被分配的设备不记录
                Integer customerId = devicePo.getCustomerId();
                if(customerId == null){
                    return;
                }
                Future<Integer> co2 = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(), startTime, endTime, SensorTypeEnums.CO2_IN.getCode()));
                Future<Integer>  hcho = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.HCHO_IN.getCode()));
                Future<Integer>  tvoc = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.TVOC_IN.getCode()));
                Future<Integer>  hum = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.HUMIDITY_IN.getCode()));
                Future<Integer>  tem = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.TEMPERATURE_IN.getCode()));
                Future<Integer>  pm = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.PM25_IN.getCode()));
                Future<Integer>  nh3 = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.NH3_IN.getCode()));
                Future<Integer>  anion = defaultEventExecutorGroup.submit(new QueryTask(devicePo.getId(),startTime,endTime,SensorTypeEnums.ANION_IN.getCode()));

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
                    deviceSensorStatPo.setNh3(nh3.get());
                    deviceSensorStatPo.setAnion(anion.get());
                    //根据传感器数值大小，判断是否设备报警
                    if(customerId == 3 && devicePo.getPowerStatus() == 1){//韩师傅 和 开机设备开启设备报警
                        deviceWarn(devicePo.getId(), customerId, deviceSensorStatPo);
                    }
                }catch (Exception e){
                    log.error("",e);
                }
                try {
                    deviceSensorStatMapper.insert(deviceSensorStatPo);
                }catch (Exception e){
                    log.error("",e);
                }
            });

            offset += devicePoList.size();
            devicePoList = deviceMapper.selectList(queryPo, 100, offset);
        } while (devicePoList.size() > 0);
        log.info("DeviceSensorStatWorker end work");
    }

    private void deviceWarn(Integer deviceId, Integer customerId, DeviceSensorStatPo deviceSensorStatPo) {
        DeviceSensorWarnPo deviceSensorWarnPo = (DeviceSensorWarnPo)template.opsForValue().get("sensorWarn" + customerId);
        if(deviceSensorWarnPo == null){
            deviceSensorWarnPo = deviceSensorWarnMapper.selectByCustomerId(customerId);
            if(deviceSensorWarnPo == null){
                log.info("该公众号没有配置");
                return;
            }
            template.opsForValue().set("sensorWarn" + customerId, deviceSensorWarnPo);
        }
        StringBuilder sb = new StringBuilder();
        //判断温度
        Integer temMax = deviceSensorWarnPo.getTemMax();
        if(deviceSensorStatPo.getTem() > temMax){
            sb.append("温度过高报警。");
        }
        Integer temMin = deviceSensorWarnPo.getTemMin();
        if(deviceSensorStatPo.getTem() < temMin){
            sb.append("温度过低报警。");
        }
        Integer humMax = deviceSensorWarnPo.getHumMax();
        if(deviceSensorStatPo.getHum() > humMax){
            sb.append("湿度过高报警。");
        }
        Integer humMin = deviceSensorWarnPo.getHumMin();
        if(deviceSensorStatPo.getHum() < humMin){
            sb.append("湿度过低报警。");
        }
        Integer pm = deviceSensorWarnPo.getPm();
        if(deviceSensorStatPo.getPm() > pm){
            sb.append("PM过高报警。");
        }
        Integer hcho = deviceSensorWarnPo.getHcho();
        if(deviceSensorStatPo.getHcho() > hcho){
            sb.append("甲醛过高报警。");
        }
        Integer tvoc = deviceSensorWarnPo.getTvoc();
        if(deviceSensorStatPo.getTvoc() > tvoc){
            sb.append("TVOC过高报警。");
        }
        Integer co2 = deviceSensorWarnPo.getCo2();
        if(deviceSensorStatPo.getCo2() > co2){
            sb.append("CO2报警。");
        }
        if(!sb.toString().equals("")){
            ProjectJobInfo projectJobInfo = new ProjectJobInfo();
            projectJobInfo.setCustomerId(customerId);
            projectJobInfo.setType(1);
            projectJobInfo.setLinkDeviceId(deviceId);
            projectJobInfo.setName("设备报警");
            projectJobInfo.setDescription(sb.toString());
            projectJobInfo.setIsRule(0);
            projectJobInfo.setWarnLevel(3);
            projectJobInfo.setSourceType(3);
            projectJobInfo.setWarnStatus(2);
            projectJobInfo.setFlowStatus(1);
            projectJobInfo.setStatus(1);
            projectJobInfo.setCreateTime(new Date());
            jobMapper.insert(projectJobInfo);
        }
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
            log.info("data = {}",data);
            return data;
        }
    }
}
