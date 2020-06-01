package vn.edu.hcmut.iotserver.mqtt;

/**
 * This class send and receive data from MQTT server
 *
 * Received data will be process by calling methods in IoTController class
 * and save to database by calling methods in JawMySQL class
 */
public class MQTTConnection {
    public static void startService(){
        //todo connect to server and collect data
    }
    public static void sendCommandToIoTDevice(String deviceType, String... controlValues){
        //todo send control payload to the server
    }
}
