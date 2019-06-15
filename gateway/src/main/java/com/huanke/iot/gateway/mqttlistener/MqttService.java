package com.huanke.iot.gateway.mqttlistener;

import com.huanke.iot.gateway.io.AbstractHandler;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
@Slf4j
public class MqttService {


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
                oldServerClient = new MqttClient(mqttOldServerUrl, "ServerClientNew");
            }catch (Exception e){
                log.error("",e);
            }
        }

        if(newServerClient == null){
            try {
                newServerClient = new MqttClient(mqttNewServerUrl, "ServerClientNew");
            }catch (Exception e){
                log.error("",e);
            }
        }
    }
    @EventListener
    public void start(ApplicationReadyEvent  event){
        if(oldServerClient != null){
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setAutomaticReconnect(true);
                oldServerClient.connect(connOpts);
                MqttMessageListener listener = new MqttMessageListener();
                oldServerClient.subscribe("/up2/#",listener);
            }catch (Exception e){
                log.error("",e);
            }
        }

        if(newServerClient != null){
            try {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);
                connOpts.setAutomaticReconnect(true);
                newServerClient.connect(connOpts);
                MqttMessageListener listener = new MqttMessageListener();
                newServerClient.subscribe("/up2/#",listener);
            }catch (Exception e){
                log.error("",e);
            }
        }
    }

    public class MqttMessageListener implements IMqttMessageListener{

        DefaultEventLoopGroup defaultEventLoopGroup = new DefaultEventLoopGroup(9,new DefaultThreadFactory("MqttMessageThread"));
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            defaultEventLoopGroup.submit(new Runnable() {
                @Override
                public void run() {
                    AbstractHandler.getHandler(topic).handler(topic,message.getPayload());
                }
            });
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

    private MqttClient getClient(boolean isOld){
        MqttClient mqttClient = oldServerClient;
        if(!isOld){
            mqttClient = newServerClient;
        }
        return mqttClient;
    }
}
