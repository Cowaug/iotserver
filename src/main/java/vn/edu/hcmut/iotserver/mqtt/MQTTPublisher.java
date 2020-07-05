package vn.edu.hcmut.iotserver.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import static vn.edu.hcmut.iotserver.IotserverApplication.SERVER_URI;

public class MQTTPublisher {
    static MqttClient client;
    static MqttConnectOptions mqttConnectOp = new MqttConnectOptions();
    static {
        try {
            client = new MqttClient(SERVER_URI, MqttClient.generateClientId());
            mqttConnectOp.setAutomaticReconnect(true);
            mqttConnectOp.setCleanSession(true);
            mqttConnectOp.setKeepAliveInterval(15);
            client.connect(mqttConnectOp);
        } catch (MqttException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void sendCommandToIoTDevice(String topic, JSONObject command) throws MqttException {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(command);

        System.out.println("\t \u001B[32m -----> " + jsonArray.toJSONString());
        System.out.println("\n");

        MqttMessage message = new MqttMessage(jsonArray.toJSONString().getBytes());
        message.setQos(1);
        client.publish(topic, message);
    }

}
