package vn.edu.hcmut.iotserver.iotcontroller;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.entities.DeviceMode;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;

import java.sql.SQLException;

/**
 * This class is for logical control of the IoT device
 * After process data, it will send the data back to the MQTT server by calling methods from MQTTConnection class
 */
public class IoTController {
    private static int temperatureThreshold = 27; // default is 27 Celsius
    private static int humidityThreshold = 70;// default is 70 percent
    private static int lightThreshold = 650;// default 650 lumen
    private static int plantThreshold = 50;// default 50%

    static {
        try {
            int tmp = IoTSensorData.getDefault("temp");
            if (tmp != -1)
                temperatureThreshold = tmp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            int tmp = IoTSensorData.getDefault("humid");
            if (tmp != -1)
                humidityThreshold = tmp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            int tmp = IoTSensorData.getDefault("light");
            if (tmp != -1)
                lightThreshold = tmp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            int tmp = IoTSensorData.getDefault("plant");
            if (tmp != -1)
                plantThreshold = tmp;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void processDataAndSendToDevice(DeviceType sensorType, JSONObject payload) throws SQLException {
        //todo control IoT application based on given value
        // sensorValues theo thứ tự trong format payload

//        if (sensorType != DeviceType.Speaker && sensorType != DeviceType.LightD)
        System.out.println("\n");
        System.out.println("\u001B[34m <----- " + new String(payload.toString()).replace(" ", ""));


        IoTSensorData.getMapping((String) payload.get("device_id")).forEach(deviceId -> {
            try {
                JSONObject returnJSONObject = new JSONObject();
                returnJSONObject.put("device_id", deviceId);

                Object[] deviceControlInfo = IoTSensorData.getInfo(deviceId);
                DeviceMode deviceMode = (DeviceMode) deviceControlInfo[0];
                int sensorValue1 = (int) deviceControlInfo[1];
                int sensorValue2 = (int) deviceControlInfo[2];

                JSONArray array = (JSONArray) payload.get("values");
                JSONArray list = new JSONArray();
                switch (sensorType) {
                    case SENSOR_PLANT: {
                        int compareThreshold = sensorValue1 < 0 ? plantThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                            list.add("0");
                            list.add("0");
                        } else if (Integer.valueOf((String) array.get(0)) < compareThreshold) {
                            list.add("1");
                            list.add("50");
                            list.add("15");
                        } else {
                            list.add("0");
                            list.add("0");
                            list.add("0");
                        }
                    }
                    break;

                    case SENSOR_TEMP: {
                        int compareThreshold1 = sensorValue1 < 0 ? temperatureThreshold : sensorValue1;
                        int compareThreshold2 = sensorValue2 < 0 ? temperatureThreshold : sensorValue2;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                            list.add("0");
                        } else if (Integer.valueOf((String) array.get(0)) > compareThreshold1) {
                            list.add("1");
                            list.add("27");
                        } else {
                            list.add("0");
                            list.add("0");
                        }
                    }
                    break;

                    case SENSOR_LIGHT: {
                        int compareThreshold = sensorValue1 < 0 ? lightThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                        } else if (Integer.valueOf((String) array.get(0)) < compareThreshold) {
                            list.add("1");
                        } else {
                            list.add("0");
                        }
                    }
                    break;

                    case Mois: {
                        int compareThreshold = sensorValue1 < 0 ? plantThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                            list.add("1000");
                        } else if (Integer.valueOf((String) array.get(0)) < compareThreshold) {
                            list.add("1");
                            list.add("2500");
                        } else {
                            list.add("0");
                            list.add("0");
                        }

                    }
                    break;

                    case TempHumi: {
                        int compareThreshold1 = sensorValue1 < 0 ? temperatureThreshold : sensorValue1;
                        int compareThreshold2 = sensorValue2 < 0 ? temperatureThreshold : sensorValue2;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                            list.add("128");
                        } else if (Integer.valueOf((String) array.get(0)) > compareThreshold1) {
                            list.add("1");
                            list.add("128");
                        } else {
                            list.add("0");
                            list.add("0");
                        }
                    }
                    break;

                    case Light: {
                        int compareThreshold = sensorValue1 < 0 ? lightThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON) {
                            list.add("1");
                            list.add("255");
                        } else if (Integer.valueOf((String) array.get(0)) < compareThreshold) {
                            list.add("1");
                            list.add("255");
                        } else {
                            list.add("0");
                            list.add("0");
                        }
                    }
                    break;

                    default:
                        return;
                }

                returnJSONObject.put("values", list);
//                MQTTPublisher.sendCommandToIoTDevice("Control/" + deviceId, returnJSONObject);
                MQTTPublisher.sendCommandToIoTDevice("Topic/" + deviceId, returnJSONObject);
            } catch (Exception e) {
                System.out.println("\t" + e.getMessage());
//                e.printStackTrace();
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
    public static void setTemperatureThreshold(int temperatureThreshold) {
        IoTController.temperatureThreshold = temperatureThreshold;
    }

    /**
     * Set humidity sensor threshold
     *
     * @param humidityThreshold Value between 0 and 100
     */
    public static void setHumidityThreshold(int humidityThreshold) {
        IoTController.humidityThreshold = humidityThreshold;
    }

    public static int getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public static int getHumidityThreshold() {
        return humidityThreshold;
    }

    public static int getLightThreshold() {
        return lightThreshold;
    }

    public static int getPlantThreshold() {
        return plantThreshold;
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
