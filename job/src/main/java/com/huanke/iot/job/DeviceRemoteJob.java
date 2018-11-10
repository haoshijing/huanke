package com.huanke.iot.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.util.LocationUtils;
import com.huanke.iot.job.DeviceTimerJob.FuncItemMessage;
import com.huanke.iot.job.DeviceTimerJob.FuncListMessage;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                    String topic = "/down2/cfg/"+devicePo.getId();
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
    
    @Scheduled(cron = "0 0/2 * * * ?")
    public void weatherPush(){
        log.info("start weather work");
        List<DevicePo> devicePoList = deviceMapper.selectAll();
        devicePoList.stream().filter(devicePo -> {
            return devicePo != null;
        }).forEach(devicePo -> {
            String ip = devicePo.getIp();
            if(StringUtils.isNotEmpty(devicePo.getIp())){
                locationUtils.getLocation(ip,true);
                JSONObject jsonObject = locationUtils.getWeather(ip,true);
                if(jsonObject != null){
                	String topic = "/down2/sensor/" + devicePo.getId();
                    if(jsonObject.containsKey("result")) {
                        JSONObject result = jsonObject.getJSONObject("result");
                        Map<String,String> weather = new HashMap<String,String>();
                        weather.put(SensorTypeEnums.HUMIDITY_WEATHER.getCode(), result.getString("humidity").replace("%", ""));
                        weather.put(SensorTypeEnums.PM25_WEATHER.getCode(), result.getString("aqi"));
                        weather.put(SensorTypeEnums.TEMPERATURE_WEATHER.getCode(), result.getString("temperature_curr").replace("℃", ""));
                        weather.put(SensorTypeEnums.WEATHER.getCode(), result.getString("weatid"));
                        boolean flag = true;
                        for(String temp : weather.values()) {
                        	if(temp == null) flag = false;
                        };
                        if(flag) {
	                        List<FuncItemMessage> funcItemMessages = new ArrayList<FuncItemMessage>();
	                        for(String key : weather.keySet()) {
	                        	FuncItemMessage funcItemMessage = new FuncItemMessage();
	                        	funcItemMessage.setType(key);
	                        	funcItemMessage.setValue(weather.get(key));
	                        	funcItemMessages.add(funcItemMessage);
	                        }
	                        FuncListMessage funcListMessage = new FuncListMessage();
	                        String requestId = UUID.randomUUID().toString().replace("-", "");
	                        funcListMessage.setMsg_type("control");
	                        funcListMessage.setMsg_id(requestId);
	                        funcListMessage.setDatas(funcItemMessages);
	                        log.info(" push weather work diviceId:{}",devicePo.getId());
	                        mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
                        }
                    }
                }
            }
        });
        log.info(" end weather work ");
    }
}
