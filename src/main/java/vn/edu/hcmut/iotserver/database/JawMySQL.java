package vn.edu.hcmut.iotserver.database;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

public class JawMySQL {
    static String url = null;

    public static Connection getConnection() {
        Connection connection = null;
        System.out.print(" .");
        try {
            URI jdbUri = (url == null ? new URI(System.getenv("DB_URL")) : new URI(url));
            while (connection == null) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + jdbUri.getPort() + jdbUri.getPath();
                    connection = DriverManager.getConnection(jdbUrl, jdbUri.getUserInfo().split(":")[0], jdbUri.getUserInfo().split(":")[1]);
                    System.out.print(". ");
                } catch (SQLException | ClassNotFoundException e) {
//                e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        } catch (URISyntaxException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }
}