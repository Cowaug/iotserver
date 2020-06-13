package vn.edu.hcmut.iotserver.database;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.Entities.DeviceMode;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static vn.edu.hcmut.iotserver.DeviceType.SENSOR_TEMP;

/**
 * Get data about IoT devices
 */
public class IoTSensorData {
    static Connection connection = JawMySQL.getConnection();

    /**
     * Push the payload into MySQL database
     * @param deviceType DeviceType enum
     * @param payload JSON Object received from server
     * @throws SQLException .
     */
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

    /**
     * Get current status of all devices (exclude sensor)
     * @param deviceType DeviceType enum
     * @return Array of JSON Object, each Object resprent status of a device
     * @throws SQLException .
     */
    public static JSONArray getNewestDeviceStatus(DeviceType deviceType) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // todo viết câu sql, dùng select * nha :)
            ResultSet resultSet = st.executeQuery("select *, MAX(_timestamp) as last_update  from "+deviceType.getDatabase()+" GROUP BY device_id");

            JSONArray timeAndStatus = new JSONArray();
            while (resultSet.next()) {
                JSONObject object = new JSONObject();
                object.put("time", resultSet.getTimestamp("_timestamp").getTime());
                object.put("device_id", resultSet.getString(2));
                switch (deviceType) {
                    case SENSOR_TEMP:
                        object.put("temperature", resultSet.getInt(3));
                        object.put("humid", resultSet.getInt(4));
                        break;
                    case SENSOR_LIGHT:
                        object.put("light_value", resultSet.getInt(3));
                        break;
                    case SENSOR_PLANT:
                        object.put("humid", resultSet.getInt(3));
                        break;
                    case INDICATE_LIGHT:
                        object.put("color", resultSet.getInt(3));
                        break;
                    case LIGHT_BULB:
                    case AIR_CONDITIONER:
                    case MOTOR:
                        object.put("status", resultSet.getInt(3));
                        break;
                }
                timeAndStatus.add(object);
            }
            return timeAndStatus;
        }
    }

    /**
     * For user request only
     */
    public static JSONObject getDeviceStatus7Day(DeviceType deviceType, String deviceId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            String sql = "";
            switch (deviceType) {
                case SENSOR_TEMP:
                    sql += "SELECT DATE(_timestamp) AS days, AVG(temp_value), AVG(humid_value) ";
                    break;
                case SENSOR_LIGHT:
                    sql += "SELECT DATE(_timestamp) AS days, AVG(light_value)";

                    break;
                case SENSOR_PLANT:
                    sql += "SELECT DATE(_timestamp) AS days,  AVG(humid_value)";
                    break;
            }
            sql += " FROM " + deviceType.getDatabase() +
                    " WHERE DATE_ADD(CURDATE(), INTERVAL -6 DAY) <= DATE(_timestamp) <= CURDATE() AND device_id = '" + deviceId + "' " +
                    " GROUP BY days " +
                    " ORDER BY days";
            ResultSet resultSet = st.executeQuery(sql);

            // Covert ResultSet to JSON format
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("device_id", deviceId);

            JSONArray timeAndStatus = new JSONArray();
            while (resultSet.next()) {
                JSONObject object = new JSONObject();
                switch (deviceType) {
                    case SENSOR_TEMP:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("temperature", resultSet.getInt(2));
                        object.put("humid", resultSet.getInt(3));
                        break;
                    case SENSOR_LIGHT:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("light_value", resultSet.getInt(2));
                        break;
                    case SENSOR_PLANT:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("humid", resultSet.getInt(2));
                        break;
                    case INDICATE_LIGHT:
                    case LIGHT_BULB:
                    case AIR_CONDITIONER:
                    case MOTOR:
                        break;
                }
                timeAndStatus.add(object);
            }
            jsonObject.put("data", timeAndStatus);
            return jsonObject;
        }

    }

    /**
     * Get which device the sensor control
     * @param sensorId
     * @return
     * @throws SQLException
     */
    public static ArrayList<String> getMapping(String sensorId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM SENSOR_DEVICE_INFOS WHERE sensor_id='" + sensorId + "'");

            ArrayList<String> strings = new ArrayList<>();
            while (resultSet.next()) {
                strings.add(resultSet.getString("device_id"));
            }
            return strings;
        }
    }

    /**
     * Get operating mode of the device (AUTO, ALWAYS ON, ALWAYS OFF)
     * @param deviceId
     * @return
     * @throws SQLException
     */
    public static DeviceMode getMode(String deviceId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM DEVICE_MODE WHERE device_id='" + deviceId + "'");

            if (resultSet.next()) {
                return DeviceMode.valueOf(resultSet.getString(2).toUpperCase());
            }
            return DeviceMode.OFF;
        }
    }

    /**
     * Set operating mode of the device
     * @param deviceId
     * @param mode
     * @throws SQLException
     */
    public static void setMode(String deviceId, DeviceMode mode) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("UPDATE DEVICE_MODE SET current_mode = '" + mode.toString() + "'WHERE device_id='" + deviceId + "'");
        }
    }

    public static void main(String[] args) throws Exception {
//        getDeviceStatus7Day(SENSOR_TEMP, "temp1");
        setMode("air_conditioner_1",DeviceMode.AUTO);
        System.out.println(getMode("air_conditioner_1"));
    }
}
