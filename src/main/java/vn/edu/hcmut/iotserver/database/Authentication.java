package vn.edu.hcmut.iotserver.database;

import org.apache.commons.codec.binary.Hex;
import vn.edu.hcmut.iotserver.Entities.Permissions;
import vn.edu.hcmut.iotserver.Entities.User;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Authentication {
    static Connection connection = JawMySQL.getConnection();
    private static final byte[] SALT = System.getenv("SALT_CRYPTO").getBytes();
    private static final String SKF = System.getenv("SKF");

    public static User login(String id, String password) throws SQLException {
        String userId = id.replace("'", "");
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("select userId, permission from IoT_USERS where userId = '" + userId + "' and password = '" + hashPassword(password.toCharArray()) + "'");
            return rs.next() ? new User(userId, Permissions.valueOf(rs.getString(2))) : null;
        }
    }

    private static String hashPassword(final char[] password) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SKF);
            PBEKeySpec spec = new PBEKeySpec(password, SALT, 1024, 1024);
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
