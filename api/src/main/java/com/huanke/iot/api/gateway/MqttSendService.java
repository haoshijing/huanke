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

    @Value("${mqttOldServerUrl}")
    private String mqttOldServerUrl;

    @Value("${mqttNewServerUrl}")
    private String mqttNewServerUrl;

    MqttClient oldServerClient;

    MqttClient newServerClient;

    @PostConstruct
    public void init(){
        if(oldServerClient == null){
            try {
                oldServerClient = new MqttClient(mqttOldServerUrl, "MessageProducer1");
            }catch (Exception e){
                log.error("",e);
            }
        }

        if(newServerClient == null){
            try {
                newServerClient = new MqttClient(mqttNewServerUrl, "MessageProducer2");
            }catch (Exception e){
                log.error("",e);
            }
        }
    }
    @EventListener
    public void start(ApplicationReadyEvent event){
        if(oldServerClient != null){
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setAutomaticReconnect(true);
                connOpts.setCleanSession(true);
                oldServerClient.connect(connOpts);
            }catch (Exception e){
                log.error("",e);
            }
        }

        if(newServerClient != null){
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setAutomaticReconnect(true);
                connOpts.setCleanSession(true);
                newServerClient.connect(connOpts);
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    public void sendMessage(String topic,String message,boolean isOld){
        MqttClient mqttClient = getClient(isOld);
        if(mqttClient != null){
            try {
                mqttClient.publish(topic, new MqttMessage(message.getBytes()));
            }catch (Exception e){
                log.error("",e);
            }
        }
    }



    public void sendMessage(String topic,byte[] datas,boolean isOld){
        MqttClient mqttClient = getClient(isOld);
        if(mqttClient != null){
            try {
                mqttClient.publish(topic, new MqttMessage(datas));
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    private MqttClient getClient(boolean isOld){
        MqttClient mqttClient = oldServerClient;
        if(!isOld){
            mqttClient = newServerClient;
        }
        return mqttClient;
    }
}
