package vn.edu.hcmut.iotserver.iotcontroller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.entities.DeviceMode;
import vn.edu.hcmut.iotserver.database.IoTSensorData;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class is for logical control of the IoT device
 * After process data, it will send the data back to the MQTT server by calling methods from MQTTConnection class
 */
@Repository
public class IoTController {

    IoTSensorData ioTSensorData;
    
    private int temperatureThreshold = 27; // default is 27 Celsius
    private int humidityThreshold = 70;// default is 70 percent
    private int lightThreshold = 650;// default 650 lumen
    private int plantThreshold = 50;// default 50%
    private String scheduleOn = "00:00";
    private String scheduleOff = "00:00";

    @Autowired
    public IoTController(IoTSensorData ioTSensorData){
        this.ioTSensorData = ioTSensorData;
        {
            int tmp = Integer.valueOf(ioTSensorData.getDefault("temp"));
            if (tmp != -1)
                temperatureThreshold = tmp;
        }
        {
            int tmp = Integer.valueOf(ioTSensorData.getDefault("humid"));
            if (tmp != -1)
                humidityThreshold = tmp;
        }
        {
            int tmp = Integer.valueOf(ioTSensorData.getDefault("light"));
            if (tmp != -1)
                lightThreshold = tmp;
        }

        {
            int tmp = Integer.valueOf(ioTSensorData.getDefault("plant"));
            if (tmp != -1)
                plantThreshold = tmp;
        }

        {
            String tmp = ioTSensorData.getDefault("s_on");
            if (!tmp.equals("-1"))
                scheduleOn = tmp;
        }

        {
            String tmp = ioTSensorData.getDefault("s_off");
            if (!tmp.equals("-1"))
                scheduleOff = tmp;
        }

    }


    public void processDataAndSendToDevice(DeviceType sensorType, JSONObject payload) throws SQLException {
        //todo control IoT application based on given value
        // sensorValues theo thứ tự trong format payload

//        if (sensorType != DeviceType.Speaker && sensorType != DeviceType.LightD)
//        System.out.println("\n");
//        System.out.println("\u001B[34m <----- " + new String(payload.toString()).replace(" ", ""));
        System.out.println();
        System.out.print("Recieved "+new String(payload.toString()).replace(" ", ""));

        ioTSensorData.getMapping((String) payload.get("device_id")).forEach(deviceId -> {
            try {
                JSONObject returnJSONObject = new JSONObject();
                returnJSONObject.put("device_id", deviceId);

                Object[] deviceControlInfo = ioTSensorData.getMode(deviceId);
                DeviceMode deviceMode = DeviceMode.valueOf( (String) deviceControlInfo[0]);
                int sensorValue1 = (int) deviceControlInfo[1];
                int sensorValue2 = (int) deviceControlInfo[2];
                String schOn = (String) deviceControlInfo[3];
                String schOff = (String) deviceControlInfo[4];
                

                JSONArray array = (JSONArray) payload.get("values");
                JSONArray list = new JSONArray();
                switch (sensorType) {
                    case SENSOR_PLANT: {
                        int compareThreshold = sensorValue1 < 0 ? plantThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON ||
                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) < compareThreshold ||
                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
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
                        } else if (deviceMode == DeviceMode.ON ||
                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) > compareThreshold1 ||
                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
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
                        } else if (deviceMode == DeviceMode.ON ||
                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) < compareThreshold ||
                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
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
                        } else if (deviceMode == DeviceMode.ON ||
                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) < compareThreshold ||
                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
                            list.add("1");
                            list.add("1000");

                        } else {
                            list.add("0");
                            list.add("0");
                        }

                    }
                    break;

                    case TempHumi: {
//                        int compareThreshold1 = sensorValue1 < 0 ? temperatureThreshold : sensorValue1;
//                        int compareThreshold2 = sensorValue2 < 0 ? temperatureThreshold : sensorValue2;
//
//                        if (deviceMode == DeviceMode.OFF) {
//                            list.add("0");
//                            list.add("0");
//                        } else if (deviceMode == DeviceMode.ON ||
//                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) < compareThreshold1||
//                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
//                            list.add("1");
//                            list.add("128");
//                        } else {
//                            list.add("0");
//                            list.add("0");
//                        }
                    }
                    break;

                    case Light: {
                        int compareThreshold = sensorValue1 < 0 ? lightThreshold : sensorValue1;

                        if (deviceMode == DeviceMode.OFF) {
                            list.add("0");
                            list.add("0");
                        } else if (deviceMode == DeviceMode.ON ||
                                deviceMode == DeviceMode.AUTO && Integer.valueOf((String) array.get(0)) < compareThreshold ||
                                deviceMode == DeviceMode.SCHEDULE && activation(schOn,schOff)) {
                            list.add("1");
                            list.add("255");
                        } else {
                            list.add("0");
                            list.add("0");
                        }
                    }
                    break;

                    default:
                        System.out.println("No device found!");
                        return;
                }

                returnJSONObject.put("values", list);
//                MQTTPublisher.sendCommandToIoTDevice("Control/" + deviceId, returnJSONObject);
                if(deviceId.contains("_"))
                MQTTPublisher.sendCommandToIoTDevice("Topic/" + deviceId.substring(0,deviceId.lastIndexOf("_")).toUpperCase(), returnJSONObject);
                else
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
    public  void setLightThreshold(int lightThreshold) {
        this.lightThreshold = lightThreshold;
    }

    /**
     * Set plant humidity sensor threshold
     *
     * @param plantThreshold Value between 0 and 100
     */
    public  void setPlantThreshold(int plantThreshold) {
        this.plantThreshold = plantThreshold;
    }

    /**
     * Set temperature sensor threshold
     *
     * @param temperatureThreshold Value between 0 and 100
     */
    public  void setTemperatureThreshold(int temperatureThreshold) {
        this.temperatureThreshold = temperatureThreshold;
    }

    /**
     * Set humidity sensor threshold
     *
     * @param humidityThreshold Value between 0 and 100
     */
    public  void setHumidityThreshold(int humidityThreshold) {
        this.humidityThreshold = humidityThreshold;
    }

    public  int getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public  int getHumidityThreshold() {
        return humidityThreshold;
    }

    public  int getLightThreshold() {
        return lightThreshold;
    }

    public  int getPlantThreshold() {
        return plantThreshold;
    }

    private  boolean activation(String scheduleOn, String scheduleOff) {
        Date date = new Date(System.currentTimeMillis());   // given date
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(date);   // assigns calendar to given date
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
        int currentMinute = calendar.get(Calendar.MINUTE);

        String schOn[] = scheduleOn.split(":");
        int schOnHour = Integer.valueOf(schOn[0]);
        int schOnMinute = Integer.valueOf(schOn[1]);

        String schOff[] = scheduleOff.split(":");
        int schOffHour = Integer.valueOf(schOff[0]);
        int schOffMinute = Integer.valueOf(schOff[1]);

        boolean active = false;

        if (schOnHour > schOffHour ||
                schOffHour == schOnHour && schOnMinute > schOffMinute) {
            active = true;

            schOn = scheduleOff.split(":");
            schOnHour = Integer.valueOf(schOn[0]);
            schOnMinute = Integer.valueOf(schOn[1]);

            schOff = scheduleOn.split(":");
            schOffHour = Integer.valueOf(schOff[0]);
            schOffMinute = Integer.valueOf(schOff[1]);
        }
        if (currentHour < schOffHour && currentHour > schOnHour ||
                schOnHour != schOffHour && currentHour == schOffHour && currentMinute < schOffMinute ||
                schOnHour != schOffHour && currentHour == schOnHour && currentMinute >= schOnMinute ||
                schOnHour == schOffHour && currentMinute < schOffMinute && currentMinute >= schOnMinute
        )
            active = !active;

        return active;
    }
}
