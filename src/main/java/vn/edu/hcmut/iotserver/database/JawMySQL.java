package vn.edu.hcmut.iotserver.database;

import org.apache.commons.codec.binary.Hex;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.Entities.Permissions;
import vn.edu.hcmut.iotserver.Entities.User;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

import static vn.edu.hcmut.iotserver.DeviceType.*;

public class JawMySQL {

    private static Connection connection = null;
    static String url = null;

    public static void init() {
        if (connection != null) return;
        System.out.println("Connecting to database (IoT Server)");
        try {
            URI jdbUri = (url == null ? new URI(System.getenv("DB_URL")) : new URI(url));
            while (connection == null) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + jdbUri.getPort() + jdbUri.getPath();
                    connection = DriverManager.getConnection(jdbUrl, jdbUri.getUserInfo().split(":")[0], jdbUri.getUserInfo().split(":")[1]);
                    System.out.println("Connection to database (IoT Server) initialed!");
                } catch (SQLException | ClassNotFoundException e) {
//                e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        } catch (URISyntaxException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        if (connection == null) init();
        return connection;
    }
}