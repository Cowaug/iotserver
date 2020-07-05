package vn.edu.hcmut.iotserver.iotdevices.dao;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import vn.edu.hcmut.iotserver.DeviceType;

public interface DeviceDAO {
    public JSONArray getNewestDeviceStatus(DeviceType deviceType);
    public JSONObject getDeviceStatus7Day(DeviceType deviceType, String deviceId);
}
