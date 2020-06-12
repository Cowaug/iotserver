package vn.edu.hcmut.iotserver.iotcontroller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.Entities.DeviceMode;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class is for logical control of the IoT device
 * After process data, it will send the data back to the MQTT server by calling methods from MQTTConnection class
 */
public class IoTController {
    private static float temperatureThreshold = 27; // default is 27 Celsius
    private static float humidityThreshold = 70;// default is 70 percent
    private static int lightThreshold = 650;// default 650 lumen
    private static int plantThreshold = 50;// default 50%

    public static void processDataAndSendToDevice(DeviceType sensorType, JSONObject payload) throws SQLException {
        //todo control IoT application based on given value
        // sensorValues theo thứ tự trong format payload

        IoTSensorData.getMapping((String) payload.get("device_id")).forEach(deviceId -> {
            try {
                JSONObject returnJSONObject = new JSONObject();
                returnJSONObject.put("device_id", deviceId);

                JSONArray array = (JSONArray) payload.get("values");
                JSONArray list = new JSONArray();
                switch (sensorType) {
                    case SENSOR_PLANT:
                        if (Integer.valueOf((String) array.get(0)) < plantThreshold) {
                            list.add("1");
                            list.add("50");
                            list.add("15");
                        } else {
                            list.add("0");
                            list.add("0");
                            list.add("0");
                        }
                        break;

                    case SENSOR_TEMP:
                        if (Integer.valueOf((String) array.get(0)) > temperatureThreshold) {
                            list.add("1");
                            list.add("27");
                        } else {
                            list.add("0");
                            list.add("0");
                        }
                        break;

                    case SENSOR_LIGHT:
                        if (Integer.valueOf((String) array.get(0)) < lightThreshold) {
                            list.add("1");
                        } else {
                            list.add("0");
                        }
                        break;
                    default:
                        return;
                }

                returnJSONObject.put("values", list);
                System.out.println("-----> " + returnJSONObject.toJSONString());
                MQTTPublisher.sendCommandToIoTDevice("Control/" + deviceId, returnJSONObject);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set light sensor threshold
     *
     * @param lightThreshold Value between 0 and 1023
     */
    public static void setLightThreshold(int lightThreshold) {
        IoTController.lightThreshold = lightThreshold;
    }

    /**
     * Set plant humidity sensor threshold
     *
     * @param plantThreshold Value between 0 and 100
     */
    public static void setPlantThreshold(int plantThreshold) {
        IoTController.plantThreshold = plantThreshold;
    }

    /**
     * Set temperature sensor threshold
     *
     * @param temperatureThreshold Value between 0 and 100
     */
    public static void setTemperatureThreshold(float temperatureThreshold) {
        IoTController.temperatureThreshold = temperatureThreshold;
    }

    /**
     * Set humidity sensor threshold
     *
     * @param humidityThreshold Value between 0 and 100
     */
    public static void setHumidityThreshold(float humidityThreshold) {
        IoTController.humidityThreshold = humidityThreshold;
    }

    public static void main(String[] args) throws SQLException {
        JSONObject object = new JSONObject();


        JSONArray jsonArray = new JSONArray();
        jsonArray.add("70");

        object.put("device_id", "sensor_plant_1");
        object.put("values", jsonArray);
        processDataAndSendToDevice(DeviceType.SENSOR_PLANT, object);
    }
}
