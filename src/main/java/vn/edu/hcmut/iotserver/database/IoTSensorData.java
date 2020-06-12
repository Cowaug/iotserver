package vn.edu.hcmut.iotserver.database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static vn.edu.hcmut.iotserver.DeviceType.SENSOR_TEMP;

public class IoTSensorData {
    static Connection connection = JawMySQL.getConnection();


    public static void pushToDatabase(DeviceType deviceType, JSONObject payload) throws SQLException {
        try (Statement st = connection.createStatement()) {
            Timestamp sqlTimestamp = new Timestamp(System.currentTimeMillis());
            StringBuilder valueString = new StringBuilder();
            valueString
                    .append("'").append(sqlTimestamp).append("'")
                    .append(",")
                    .append("'").append(payload.get("device_id")).append("'");
            JSONArray jsonArray = (JSONArray) payload.get("values");

            for (Object j : jsonArray) {
                valueString.append(",").append(Integer.valueOf((String) j));
            }

            st.execute("insert into " + deviceType.getDatabase() + " VALUES (" + valueString + ")");
        }
    }

    public static JSONObject[] getNewestDeviceStatus(DeviceType deviceType) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // todo viết câu sql, dùng select * nha :)
            ResultSet resultSet = st.executeQuery("");

            ArrayList<JSONObject> timeAndStatus = new ArrayList<>();
            while (resultSet.next()) {
                JSONObject object = new JSONObject();
                switch (deviceType) {
                    case SENSOR_TEMP:
                        object.put("time", resultSet.getInt(1));
                        object.put("device_id", resultSet.getInt(2));
                        object.put("temperature", resultSet.getInt(3));
                        object.put("humid", resultSet.getInt(4));
                        break;
                    case SENSOR_LIGHT:
                        object.put("time", resultSet.getInt(1));
                        object.put("device_id", resultSet.getInt(2));
                        object.put("light_value", resultSet.getInt(3));
                        break;
                    case SENSOR_PLANT:
                        object.put("time", resultSet.getInt(1));
                        object.put("device_id", resultSet.getInt(2));
                        object.put("humid", resultSet.getInt(3));
                        break;
                    case INDICATE_LIGHT:
                        object.put("time", resultSet.getInt(1));
                        object.put("device_id", resultSet.getInt(2));
                        object.put("color", resultSet.getInt(3));
                        break;
                    case LIGHT_BULB:
                    case AIR_CONDITIONER:
                    case MOTOR:
                        object.put("time", resultSet.getInt(1));
                        object.put("device_id", resultSet.getInt(2));
                        object.put("status", resultSet.getInt(3));
                        break;
                }
                timeAndStatus.add(object);
            }
            return (JSONObject[]) timeAndStatus.toArray();
        }
    }

    public static JSONObject getDeviceStatus7Day(DeviceType deviceType, String deviceId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // todo viết câu sql, dùng select * nha :)
            ResultSet resultSet = st.executeQuery("");

            // Covert ResultSet to JSON format
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_id", deviceId);

            ArrayList<JSONObject> timeAndStatus = new ArrayList<>();
            while (resultSet.next()) {
                JSONObject object = new JSONObject();
                switch (deviceType) {
                    case SENSOR_TEMP:
                        object.put("time", resultSet.getInt(1));
                        object.put("temperature", resultSet.getInt(3));
                        break;
                    case SENSOR_LIGHT:
                        object.put("time", resultSet.getInt(1));
                        object.put("light_value", resultSet.getInt(3));
                        break;
                    case SENSOR_PLANT:
                        object.put("time", resultSet.getInt(1));
                        object.put("humid", resultSet.getInt(3));
                        break;
                    case INDICATE_LIGHT:
                        object.put("time", resultSet.getInt(1));
                        object.put("color", resultSet.getInt(3));
                        break;
                    case LIGHT_BULB:
                    case AIR_CONDITIONER:
                    case MOTOR:
                        object.put("time", resultSet.getInt(1));
                        object.put("status", resultSet.getInt(3));
                        break;
                }
                timeAndStatus.add(object);
            }
            jsonObject.put("data", timeAndStatus.toArray());
            return jsonObject;
        }
    }

    public static ArrayList<String> getMapping(String sensorId) throws SQLException {

        try (Statement st = connection.createStatement()) {
//            ResultSet resultSet = st.executeQuery("");

            ArrayList<String> strings = new ArrayList<>();
            strings.add("motor1");
//            while (resultSet.next()) {
//                strings.add(resultSet.getString("device_id"));
//            }
            return strings;
        }
    }
}
