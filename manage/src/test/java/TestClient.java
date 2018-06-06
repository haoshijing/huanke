import org.eclipse.paho.client.mqttv3.*;

import javax.annotation.PostConstruct;

/**
 * @author haoshijing
 * @version 2018年06月06日 18:37
 **/
public class TestClient {
    public static void main(String[] args) throws Exception{
        MqttClient mqttClient =  new MqttClient("tcp://39.104.96.141:8077", "MessageProducer");
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setAutomaticReconnect(true);
        connOpts.setCleanSession(true);
        mqttClient.connect(connOpts);
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("topic = [" + topic + "], message = [" + new String(message.getPayload()) + "]");
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        mqttClient.subscribe("/down/fota/252");

    }
}
