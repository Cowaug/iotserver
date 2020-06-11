package vn.edu.hcmut.iotserver.database;

import vn.edu.hcmut.iotserver.DeviceType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static vn.edu.hcmut.iotserver.DeviceType.SENSOR_TEMP;

public class IoTSensorData {
    static Connection connection = JawMySQL.getConnection();

    public static void pushToDatabase(DeviceType deviceType, String deviceId, long timestamp, int... values) throws SQLException {
        try (Statement st = connection.createStatement()) {
            Timestamp sqlTimestamp = new Timestamp(timestamp);
            StringBuilder valueString = new StringBuilder();
            valueString
                    .append("'").append(sqlTimestamp).append("'")
                    .append(",")
                    .append("'").append(deviceId).append("'");
            for (int i : values) {
                valueString.append(",").append(i);
            }
            st.execute("insert into " + deviceType.getDatabase() + " VALUES (" + valueString + ")");
        }
    }

    public static void main(String[] args) throws Exception {
        pushToDatabase(SENSOR_TEMP, "tmp_99", System.currentTimeMillis(), 1, 2);
    }
}
