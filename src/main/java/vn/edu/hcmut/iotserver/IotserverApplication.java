package vn.edu.hcmut.iotserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import vn.edu.hcmut.iotserver.database.JawMySQL;

@SpringBootApplication
public class IotserverApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(IotserverApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        JawMySQL.init();
    }
}
