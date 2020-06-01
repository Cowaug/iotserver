package vn.edu.hcmut.iotserver.database;

import org.apache.commons.codec.binary.Hex;
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

public class JawMySQL {
    private static final byte[] SALT = System.getenv("SALT_CRYPTO").getBytes();
    private static final String SKF = System.getenv("SKF");
    private static Connection connection = null;
    static String url = null;

    public static void setDBUrl(String url) {
        JawMySQL.url = url;
    }

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

    public static void pushToDatabase(String deviceType, long timeStamp, String... value){

    }

    public static User login(String userId, String password) throws SQLException {

        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("select userId, permission from IoT_USERS where userId = '" + userId + "' and password = '" + hashPassword(password.toCharArray()) + "'");
            return rs.next()? new User(userId,Permissions.valueOf(rs.getString(2))):null;
        }
    }

    private static String hashPassword(final char[] password) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SKF);
            PBEKeySpec spec = new PBEKeySpec(password, JawMySQL.SALT, 1024, 1024);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return Hex.encodeHexString(res);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static User register(String userId, String password) {
        return new User(userId, Permissions.ADMIN);
    }
}
