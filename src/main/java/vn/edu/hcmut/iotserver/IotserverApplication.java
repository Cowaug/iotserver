package vn.edu.hcmut.iotserver;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.simple.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.edu.hcmut.iotserver.database.JawMySQL;
import vn.edu.hcmut.iotserver.mqtt.MQTTPublisher;
import vn.edu.hcmut.iotserver.mqtt.MQTTSubscriber;

import static vn.edu.hcmut.iotserver.DeviceType.LIGHT_BULB;

@SpringBootApplication
public class IotserverApplication implements CommandLineRunner {

    public static void main(String[] args) throws MqttException {
        SpringApplication.run(IotserverApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JawMySQL.init();
        MQTTSubscriber.init();
    }
}
