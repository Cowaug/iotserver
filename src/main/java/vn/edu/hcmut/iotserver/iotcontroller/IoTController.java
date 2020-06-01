package vn.edu.hcmut.iotserver.iotcontroller;

/**
 * This class is for logical control of the IoT device
 * After process data, it will send the data back to the MQTT server by calling methods from MQTTConnection class
 */
public class IoTController {
    private static float temperatureThreshold = 27; // default is 27 Celsius
    private static float humidityThreshold = 70;// default is 70 percent
    private static int lightThreshold = 650;// default 650 lumen
    private static int plantThreshold = 50;// default 50%

    public static void processData(String deviceType, String... sensorValues){
        //todo control IoT application based on given value
    }

    /**
     * Set light sensor threshold
     * @param lightThreshold Value between 0 and 1023
     */
    public static void setLightThreshold(int lightThreshold) {
        IoTController.lightThreshold = lightThreshold;
    }

    /**
     * Set plant humidity sensor threshold
     * @param plantThreshold Value between 0 and 100
     */
    public static void setPlantThreshold(int plantThreshold) {
        IoTController.plantThreshold = plantThreshold;
    }

    /**
     * Set temperature sensor threshold
     * @param temperatureThreshold Value between 0 and 100
     */
    public static void setTemperatureThreshold(float temperatureThreshold) {
        IoTController.temperatureThreshold = temperatureThreshold;
    }

    /**
     * Set humidity sensor threshold
     * @param humidityThreshold Value between 0 and 100
     */
    public static void setHumidityThreshold(float humidityThreshold) {
        IoTController.humidityThreshold = humidityThreshold;
    }
}
