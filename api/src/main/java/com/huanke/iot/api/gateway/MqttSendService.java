package com.huanke.iot.api.gateway;

import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@Slf4j
public class MqttSendService {

    @Value("${mqttServerUrl}")
    private String mqttServerUrl;

    MqttClient mqttClient;
    @PostConstruct
    public void init(){
        if(mqttClient == null){
            try {
                mqttClient = new MqttClient(mqttServerUrl, "MessageProducer");
            }catch (Exception e){

            }
        }
    }
    @EventListener
    public void start(ApplicationReadyEvent event){
        if(mqttClient != null){
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                mqttClient.connect(connOpts);
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    public void sendMessage(String topic,String message){
        if(mqttClient != null){
            try {
                MqttDeliveryToken mqttDeliveryToken = new MqttDeliveryToken();
                mqttClient.publish(topic, new MqttMessage(message.getBytes()));
            }catch (Exception e){
                log.error("",e);
            }
        }
    }
}
