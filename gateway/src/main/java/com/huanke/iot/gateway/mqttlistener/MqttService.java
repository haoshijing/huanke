package com.huanke.iot.gateway.mqttlistener;

import com.huanke.iot.gateway.io.AbstractHandler;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@Slf4j
public class MqttService {

    @Value("${mqttServerUrl}")
    private String mqttServerUrl;

    MqttClient mqttClient;
    @PostConstruct
    public void init(){
        if(mqttClient == null){
            try {
                mqttClient = new MqttClient(mqttServerUrl, "ServerClient");
            }catch (Exception e){

            }
        }
    }
    @EventListener
    public void start(ApplicationReadyEvent  event){
        if(mqttClient != null){
            try {
                mqttClient.connect();
                MqttMessageListener listener = new MqttMessageListener();
                mqttClient.subscribe("/up/alarm/+",listener);
                mqttClient.subscribe("/up/location/+",listener);
                mqttClient.subscribe("/up/sensor/+",listener);
                mqttClient.subscribe("/up/control/+",listener);
                mqttClient.subscribe("/up/timer/+",listener);
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    public class MqttMessageListener implements IMqttMessageListener{

        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(8,new DefaultThreadFactory("MqttMessageThread"));
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            defaultEventLoopGroup.submit(new Runnable() {
                @Override
                public void run() {
                    AbstractHandler.getHandler(topic).doHandler(topic,message.getPayload());
                }
            });
        }
    }
}
