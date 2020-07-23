package vn.edu.hcmut.iotserver.database;

import org.apache.ibatis.session.SqlSession;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
public class IoTSensorData {
    @Autowired
    SqlSession sqlSession;

    long lastCleanUpCheckPoint = 0;

    final int GROUP_INTERVAL = 60 * 60; // minutes

    /**
     * Push the payload into MySQL database
     *
     * @param deviceType DeviceType enum
     * @param payload    JSON Object received from server
     * @throws SQLException .
     */
    @Transactional
    public void pushToDatabase(DeviceType deviceType, JSONObject payload){
            String sqlTimestamp = new Timestamp(System.currentTimeMillis()).toString();

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
            sqlSession.insert("IoTMapper.pushToDatabase", new Object[]{deviceType.getDatabase(),valueString});

            if(System.currentTimeMillis() - lastCleanUpCheckPoint > 10*60*1000) // 10 mins
            {
                sqlSession.insert("IoTMapper.groupSensorTemp", new Object[]{sqlTimestamp.toString(),GROUP_INTERVAL});
                sqlSession.insert("IoTMapper.groupSensorLight", new Object[]{sqlTimestamp,GROUP_INTERVAL});
                sqlSession.insert("IoTMapper.groupSensorPlant", new Object[]{sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.SENSOR_TEMP.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.SENSOR_LIGHT.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.SENSOR_PLANT.getDatabase(),sqlTimestamp,GROUP_INTERVAL});

                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.LIGHT_BULB.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.MOTOR.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.AIR_CONDITIONER.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.LightD.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                sqlSession.delete("IoTMapper.deleteOldRecord", new Object[]{DeviceType.Speaker.getDatabase(),sqlTimestamp,GROUP_INTERVAL});
                lastCleanUpCheckPoint = System.currentTimeMillis();
            }
    }


    /**
     * Get current status of all devices (exclude sensor)
     *
     * @param deviceType DeviceType enum
     * @return Array of JSON Object, each Object resprent status of a device
     * @throws SQLException .
     */
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
        try (Connection connection = JawMySQL.getConnection();Statement st = connection.createStatement()) {
            String sql = "";
            switch (deviceType) {
                case AIR_CONDITIONER:
                    sql += "SELECT DATE(S._timestamp) AS days, AVG(S.temp_value), AVG(S.humid_value) ";
                    break;
                case LightD:
                case LIGHTBULB:
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
                    case LIGHTBULB:
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
    public List<String> getMapping(String sensorId) throws SQLException {
        return sqlSession.selectList("IoTMapper.getMapping",sensorId);
    }

    /**
     * Get operating mode of the device (AUTO, ALWAYS ON, ALWAYS OFF)
     *
     * @param deviceId
     * @return
     * @throws SQLException
     */
    public Object[] getMode(String deviceId) throws SQLException {
        vn.edu.hcmut.iotserver.models.DeviceMode deviceMode = sqlSession.selectOne("IoTMapper.getMode", deviceId);
        if(deviceMode == null)
            return new Object[]{DeviceMode.OFF, -1, -1,"00:00","00:00"};
        return deviceMode.toObjectArray();
    }
    /**
     * Set operating mode of the device
     *
     * @param deviceId
     * @param mode
     * @throws SQLException
     */
    public void setMode(String deviceId, DeviceMode mode, int sensorValue1, int sensorValue2, String scheduleOn, String scheduleOff) {
        sqlSession.update("IoTMapper.setMode", new Object[]{deviceId,mode,sensorValue1,sensorValue2,scheduleOn,scheduleOff});
    }

    public void setDefault(String key,int value){
        sqlSession.update("IoTMapper.setDefault", new Object[]{key,value});
    }

    public String getDefault(String key) {
        String result = sqlSession.selectOne("IoTMapper.getDefault",key);
        if(result == null) return "-1";
        else return result;
    }
}
