package vn.edu.hcmut.iotserver.iotcontroller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class is for logical control of the IoT device
 * After process data, it will send the data back to the MQTT server by calling methods from MQTTConnection class
 */
public class IoTController {
    private static float temperatureThreshold = 27; // default is 27 Celsius
    private static float humidityThreshold = 70;// default is 70 percent
    private static int lightThreshold = 650;// default 650 lumen
    private static int plantThreshold = 50;// default 50%

    public static void processDataAndSendToDevice(DeviceType deviceType, String sensorId, JSONObject payload) throws SQLException {
        //todo control IoT application based on given value
        // sensorValues theo thứ tự trong format payload

        IoTSensorData.getMapping(sensorId).forEach(s -> {
            try {
                JSONObject returnJSONObject = new JSONObject();
                returnJSONObject.put("device_id", s);

                switch (deviceType) {
                    case MOTOR:
                    case AIR_CONDITIONER:
                    case INDICATE_LIGHT:
                    case LIGHT_BULB:
                        return;

                    case SENSOR_PLANT:
                        break;

                    case SENSOR_TEMP:

                        break;

                    case SENSOR_LIGHT:

                        break;
                }

                MQTTPublisher.sendCommandToIoTDevice("Control/" + s, returnJSONObject);
                System.out.println("----->" + returnJSONObject.toJSONString());
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
}
