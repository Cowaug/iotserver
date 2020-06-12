package vn.edu.hcmut.iotserver.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;

public class MQTTPublisher {
    static MqttClient client;
    static MqttConnectOptions mqttConnectOp = new MqttConnectOptions();
    static {
        try {
            client = new MqttClient("ws://52.163.200.209:8083/mqtt", MqttClient.generateClientId());
            mqttConnectOp.setAutomaticReconnect(true);
            mqttConnectOp.setCleanSession(true);
            mqttConnectOp.setKeepAliveInterval(15);
            client.connect(mqttConnectOp);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static void sendCommandToIoTDevice(String topic, JSONObject command) throws MqttException {
        MqttMessage message = new MqttMessage(command.toJSONString().getBytes());
        message.setQos(1);
        client.publish(topic, message);
    }

}
