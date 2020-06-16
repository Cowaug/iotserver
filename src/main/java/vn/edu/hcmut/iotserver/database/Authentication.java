package vn.edu.hcmut.iotserver.database;

import org.apache.commons.codec.binary.Hex;
import vn.edu.hcmut.iotserver.entities.Permissions;
import vn.edu.hcmut.iotserver.entities.User;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Bình
 * Verifing user, create new user
 * DO NOT MODIFY
 */
public class Authentication {
    static Connection connection = JawMySQL.getConnection();
    private static final byte[] SALT = System.getenv("SALT_CRYPTO").getBytes();
    private static final String SKF = System.getenv("SKF");

    /**
     * Check username and password in database
     * @param id user id
     * @param password password
     * @return User Object if user id and password is valid
     * @throws SQLException .
     */
    public static User login(String id, String password) throws SQLException {
        String userId = id.replace("'", "");
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("select userId, permission from IoT_USERS where userId = '" + userId + "' and password = '" + hashPassword(password.toCharArray()) + "'");
            return rs.next() ? new User(userId, Permissions.valueOf(rs.getString(2))) : null;
        }
    }

    /**
     * Hash the password to compare it in database
     * @param password user's password
     * @return hashed password
     */
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

    /**
     * Registing a new user for testing
     * @param userId user id
     * @param password password
     * @return new User
     */
    public static User register(String userId, String password) {
        return new User(userId, Permissions.ADMIN);
    }
}
