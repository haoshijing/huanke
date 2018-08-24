package com.huanke.iot.gateway.test;

import org.eclipse.paho.client.mqttv3.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GateWayTest {

    public static void main(String[] args) {
        try {

            ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
            MqttClient mqttClient = new MqttClient("tcp://127.0.0.1:8077", "TestClient");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            mqttClient.connect(options);
            service.scheduleAtFixedRate(()->{
                try {
                    mqttClient.publish("test",
                            new MqttMessage(String.valueOf(System.currentTimeMillis()).getBytes()));
                }catch( MqttException e1){
                    e1.printStackTrace();
                }
                System.out.println(mqttClient.isConnected());
            },10,1000, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
