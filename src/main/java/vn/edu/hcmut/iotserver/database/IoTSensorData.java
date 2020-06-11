package vn.edu.hcmut.iotserver.database;

import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;

import java.sql.*;

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

    public static JSONObject getNewestDeviceStatus(DeviceType deviceType) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // todo viết câu sql, dùng select * nha :)
            ResultSet resultSet = st.executeQuery("");
//            return (JSONObject) convertResultSetToJson(deviceType, resultSet);
        }
    }

    public static JSONObject[] getDeviceStatus1Day(DeviceType deviceType) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // todo viết câu sql, dùng select * nha :)
            ResultSet resultSet = st.executeQuery("");
//            return (JSONObject[]) convertResultSetToJson(deviceType, resultSet);
        }
    }

//    // for communicate to android device
//    private static Object convertResultSetToJson(DeviceType deviceType, ResultSet resultSet) throws SQLException {
//        while(resultSet.next()) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("device_id", resultSet.getString("device_id"));
//            switch (deviceType) {
//                case LIGHT_BULB:
//                case SENSOR_LIGHT:
//                case INDICATE_LIGHT:
//                case SENSOR_PLANT:
//                    jsonObject.put("values", new int[]{
//                            resultSet.getInt(3)
//                    });
//                    break;
//                case SENSOR_TEMP:
//                case AIR_CONDITIONER:
//                    jsonObject.put("values", new int[]{
//                            resultSet.getInt(3),
//                            resultSet.getInt(4)
//                    });
//                    break;
//                case MOTOR:
//                    jsonObject.put("values", new int[]{
//                            resultSet.getInt(3),
//                            resultSet.getInt(4),
//                            resultSet.getInt(5)
//                    });
//                    break;
//            }
//            if (resultSet.getFetchSize() == 1) {
//                return jsonObject;
//            }
//            else nestedJSONObject.put()
//        }
//
//    }

    public static void main(String[] args) throws Exception {
        pushToDatabase(SENSOR_TEMP, "tmp_99", System.currentTimeMillis(), 1, 2);
    }
}
