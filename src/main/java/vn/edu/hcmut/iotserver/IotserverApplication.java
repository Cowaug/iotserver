package vn.edu.hcmut.iotserver;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.json.GsonBuilderUtils;
import vn.edu.hcmut.iotserver.database.JawMySQL;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;

@SpringBootApplication
public class IotserverApplication implements CommandLineRunner {
    public static String serverIp = System.getenv("MQTT");
    public static String usingWS = System.getenv("WS");
    public static final String SERVER_URI =
            usingWS == null ?
                    (serverIp == null ? "tcp://52.163.209.230:1883" : "tcp://" + serverIp +":1883"):
                    (serverIp == null ? "ws://52.163.209.230:8083/mqtt" : "ws://" + serverIp +":8083/mqtt");

//    public static final String SERVER_URI = "ws://52.187.125.59:8083/mqtt";
//    public static final String SERVER_URI = "tcp://52.187.125.59:1883";

    public static void main(String[] args) throws MqttException {
        SpringApplication.run(IotserverApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(System.getenv("MQTT"));
        System.out.println("Using WS: " + (System.getenv("WS") != null));
        new MQTTPublisher();
    }
}
