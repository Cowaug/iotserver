package vn.edu.hcmut.iotserver.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.iotcontroller.IoTController;

import static vn.edu.hcmut.iotserver.IotserverApplication.SERVER_URI;

@Service
public class MQTTSubscriber {
    @Autowired
    IoTSensorData ioTSensorData;

    @Autowired
    IoTController ioTController;

    MqttClient client;
    MqttConnectOptions MqttConnectOp = new MqttConnectOptions();

    {
        try {
            System.out.println("Connecting to MQTT Server...");
            client = new MqttClient(SERVER_URI, MqttClient.generateClientId());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("Connection to MQTT broker lost! " + throwable.getLocalizedMessage());
                    throwable.printStackTrace();
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    try {
//                        System.out.println("<----- " + new String(mqttMessage.getPayload()).replace(" ",""));
                        Object jsonObject = new JSONParser().parse(new String(mqttMessage.getPayload()));

                        if (jsonObject instanceof JSONArray) {
                            ((JSONArray) jsonObject).forEach(object -> {
                                try {
                                    DeviceType deviceType = DeviceType.valueOf(topic.split("/")[1]);
                                    ioTSensorData.pushToDatabase(deviceType, (JSONObject) object);
                                    ioTController.processDataAndSendToDevice(deviceType, (JSONObject) object);
                                } catch (IllegalArgumentException argumentException) {
                                    System.err.println(argumentException.getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            DeviceType deviceType = DeviceType.valueOf(topic.split("/")[1]);
                            ioTSensorData.pushToDatabase(deviceType, (JSONObject) jsonObject);
                            ioTController.processDataAndSendToDevice(deviceType, (JSONObject) jsonObject);
                        }

                    } catch (IllegalArgumentException argumentException) {
                        System.err.println("\t" + argumentException.getMessage());
                    } catch (Exception e) {
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
//            client.subscribe("Data/#", 1);
            client.subscribe("Topic/#", 1);
            System.out.println("Listening...");
        } catch (MqttException e) {
            System.out.println(e.getMessage());
        }
    }
}
