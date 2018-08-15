package com.huanke.iot.job;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.util.LocationUtils;
import com.huanke.iot.job.gateway.MqttSendService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.List;

@Repository
@Slf4j
public class DeviceRemoteJob {
    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private LocationUtils locationUtils;

    @Autowired
    private MqttSendService mqttSendService;
    @PostConstruct
    public void init(){
        doWork();
    }


    @Scheduled(cron = "0 0/2 * * * ?")
    public void doWork(){
        log.info("start remote work");
        List<DevicePo> devicePoList = deviceMapper.selectAll();
        devicePoList.stream().filter(devicePo -> {
            return devicePo != null;
        }).forEach(devicePo -> {
            String ip = devicePo.getIp();
            if(StringUtils.isNotEmpty(devicePo.getIp())){
                locationUtils.getLocation(ip,true);
                JSONObject jsonObject = locationUtils.getWeather(ip,true);
                if(jsonObject != null){
                    String topic = "/down/cfg/"+devicePo.getId();
                    if(jsonObject.containsKey("result")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String humidity = result.getString("humidity");
                        String aqi = result.getString("aqi");
                        String tem = result.getString("temperature_curr");
                        if (humidity == null || aqi == null ||
                                tem == null) {
                            return;
                        }
                        ByteBuf byteBuf = Unpooled.buffer(2 + 2 + aqi.getBytes().length + 2 +
                                humidity.getBytes().length +
                                2 + tem.getBytes().length);
                        byteBuf.writeShortLE(0X0E11);
                        byteBuf.writeShortLE(tem.getBytes().length);
                        byteBuf.writeBytes(tem.getBytes());

                        byteBuf.writeShortLE(humidity.getBytes().length);
                        byteBuf.writeBytes(humidity.getBytes());

                        byteBuf.writeShortLE(aqi.getBytes().length);
                        byteBuf.writeBytes(aqi.getBytes());
                        mqttSendService.sendMessage(topic, byteBuf.array());
                    }
                }
            }
        });
        log.info(" end remote work ");
    }
}
