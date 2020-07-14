package vn.edu.hcmut.iotserver.database;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.edu.hcmut.iotserver.DeviceType;
import vn.edu.hcmut.iotserver.entities.DeviceMode;
import vn.edu.hcmut.iotserver.models.NewestDeviceStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Get data about IoT devices
 */
@Repository
public class IoTSensorData  implements WebMvcConfigurer {
    @Autowired
    SqlSession sqlSession;

    static Connection connection = JawMySQL.getConnection();

    /**
     * Push the payload into MySQL database
     *
     * @param deviceType DeviceType enum
     * @param payload    JSON Object received from server
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
            valueString.append(",").append(0);
            st.execute("insert into " + deviceType.getDatabase() + " VALUES (" + valueString + ")");
        }
    }

    /**
     * Get current status of all devices (exclude sensor)
     *
     * @param deviceType DeviceType enum
     * @return Array of JSON Object, each Object resprent status of a device
     * @throws SQLException .
     */
//    public static JSONArray getNewestDeviceStatus(DeviceType deviceType) throws SQLException {
//        try (Statement st = connection.createStatement()) {
//            ResultSet resultSet = st.executeQuery("select *\n" +
//                    "from \n" +
//                    "vuw8gi9vft7kuo7g." + deviceType.getDatabase() + " AS D,\n" +
//                    "vuw8gi9vft7kuo7g.DEVICE_MODE AS M,\n" +
//                    "vuw8gi9vft7kuo7g.SENSOR_DEVICE_INFOS AS SDI,\n" +
//                    "vuw8gi9vft7kuo7g." + deviceType.getDatabase2() + " AS S,\n" +
//                    "\n" +
//                    "(select MAX(_timestamp) AS last_update,device_id\n" +
//                    "from vuw8gi9vft7kuo7g." + deviceType.getDatabase() + "\n" +
//                    "GROUP BY device_id) as DT,\n" +
//                    "\n" +
//                    "(select MAX(_timestamp) AS last_sensor_update,device_id\n" +
//                    "from vuw8gi9vft7kuo7g." + deviceType.getDatabase2() + "\n" +
//                    "GROUP BY device_id) as ST\n" +
//                    "\n" +
//                    "WHERE D.device_id = DT.device_id AND D._timestamp = DT.last_update\n" +
//                    "AND S.device_id = ST.device_id AND S._timestamp = ST.last_sensor_update\n" +
//                    "AND D.device_id = M.device_id AND D.device_id = SDI.device_id AND SDI.sensor_id = S.device_id;");
//            JSONArray timeAndStatus = new JSONArray();
//            while (resultSet.next()) {
//                JSONObject object = new JSONObject();
//                object.put("time", resultSet.getTimestamp("_timestamp").getTime());
//                object.put("device_id", resultSet.getString("device_id"));
//                object.put("status", resultSet.getString("status"));
//                object.put("mode", resultSet.getString("current_mode").toLowerCase());
//                switch (deviceType) {
//                    case AIR_CONDITIONER:
//                        object.put("temperature", resultSet.getInt("temp_value"));
//                        object.put("humid", resultSet.getInt("humid_value"));
//                        break;
//                    case LightD:
//                    case LIGHT_BULB:
//                        object.put("light_value", resultSet.getInt("light_value"));
//                        break;
//                    case Speaker:
//                    case MOTOR:
//                        object.put("humid", resultSet.getInt("humid_value"));
//                        break;
//
//                }
//                timeAndStatus.add(object);
//            }
//            return timeAndStatus;
//        }
//    }

    public JSONArray getNewestDeviceStatus(DeviceType deviceType){
        List<NewestDeviceStatus> newestDeviceStatusList = sqlSession.selectList("IoTMapper.getNewestDeviceStatus", new Object[]{deviceType.getDatabase(),deviceType.getDatabase2()});
        JSONArray jsonArray = new JSONArray();
        for(NewestDeviceStatus newestDeviceStatus : newestDeviceStatusList){
            jsonArray.add(newestDeviceStatus.toJSON());
        }
        return jsonArray;
    }

    /**
     * For user request only
     */
    public static JSONObject getDeviceStatus7Day(DeviceType deviceType, String deviceId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            String sql = "";
            switch (deviceType) {
                case AIR_CONDITIONER:
                    sql += "SELECT DATE(S._timestamp) AS days, AVG(S.temp_value), AVG(S.humid_value) ";
                    break;
                case LightD:
                case LIGHT_BULB:
                    sql += "SELECT DATE(S._timestamp) AS days, AVG(S.light_value)";

                    break;
                case Speaker:
                case MOTOR:
                    sql += "SELECT DATE(S._timestamp) AS days,  AVG(S.humid_value)";
                    break;
            }
            Timestamp currentDay = new Timestamp(System.currentTimeMillis());
            sql += " FROM " + deviceType.getDatabase() + " AS D, vuw8gi9vft7kuo7g.SENSOR_DEVICE_INFOS AS SDI, " + deviceType.getDatabase2() + " AS S " +
                    " WHERE  D.device_id = SDI.device_id AND SDI.sensor_id = S.device_id AND DATE_ADD('" + currentDay + "', INTERVAL -6 DAY) <= DATE(D._timestamp) <= '" + currentDay + "' AND D.device_id = '" + deviceId + "' " +
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
                    case AIR_CONDITIONER:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("temperature", resultSet.getInt(2));
                        object.put("humid", resultSet.getInt(3));
                        break;
                    case LightD:
                    case LIGHT_BULB:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("intensity", resultSet.getInt(2));
                        break;
                    case Speaker:
                    case MOTOR:
                        object.put("time", resultSet.getDate(1).getTime());
                        object.put("humid", resultSet.getInt(2));
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
     *
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
     *
     * @param deviceId
     * @return
     * @throws SQLException
     */
    public static Object[] getMode(String deviceId) throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet resultSet = st.executeQuery("SELECT * FROM DEVICE_MODE WHERE device_id='" + deviceId + "'");

            if (resultSet.next()) {
                return new Object[]{DeviceMode.valueOf(
                        resultSet.getString("current_mode").toUpperCase()),
                        resultSet.getInt("sensor_value_1"),
                        resultSet.getInt("sensor_value_2"),
                        resultSet.getString("schedule_on"),
                        resultSet.getString("schedule_off")
                };
            }
            return new Object[]{DeviceMode.OFF, -1, -1,"00:00","00:00"};
        }
    }

    /**
     * Set operating mode of the device
     *
     * @param deviceId
     * @param mode
     * @throws SQLException
     */
    public static void setMode(String deviceId, DeviceMode mode, int sensorValue1, int sensorValue2, String scheduleOn, String scheduleOff) throws
            SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("UPDATE vuw8gi9vft7kuo7g.DEVICE_MODE SET current_mode = '" + mode.toString() + "' , sensor_value_1 = " + sensorValue1 + " , sensor_value_2 = " + sensorValue2 + " , schedule_on = " + scheduleOn + " , schedule_off = " + scheduleOff + " WHERE device_id='" + deviceId + "'");
        }
    }

    public static void setDefault(String key,int value) throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("UPDATE vuw8gi9vft7kuo7g.SETTING SET device_value = " + value + " WHERE device_key='" + key + "'");
        }
    }

    public static String getDefault(String key) throws SQLException {
        try (Statement st = connection.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT device_value FROM vuw8gi9vft7kuo7g.SETTING WHERE device_key='" + key + "'");
            if(rs.next())
                return rs.getString("device_value");
            return "-1";
        }
    }
}
