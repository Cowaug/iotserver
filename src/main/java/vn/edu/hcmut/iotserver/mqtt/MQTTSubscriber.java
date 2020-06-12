package vn.edu.hcmut.iotserver.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.iotcontroller.IoTController;

public class MQTTSubscriber {
    static MqttClient client;
    static MqttConnectOptions MqttConnectOp = new MqttConnectOptions();

    static {
        try {
            client = new MqttClient("ws://52.163.200.209:8083/mqtt", MqttClient.generateClientId());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection to MQTT broker lost! " + throwable.getLocalizedMessage());
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage)  {
                    try{
                        System.out.println("<----- " + new String(mqttMessage.getPayload()));
                        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new String(mqttMessage.getPayload()));

                        DeviceType deviceType = DeviceType.valueOf(topic.split("/")[1]);
                        IoTSensorData.pushToDatabase(deviceType, jsonObject);
                        IoTController.processDataAndSendToDevice(deviceType, jsonObject);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            MqttConnectOp.setAutomaticReconnect(true);
            MqttConnectOp.setCleanSession(true);
            MqttConnectOp.setKeepAliveInterval(15);
            client.connect(MqttConnectOp);
            client.subscribe("Data/#", 1);
            System.out.println("Listening...");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public static void init() {
    }
}
