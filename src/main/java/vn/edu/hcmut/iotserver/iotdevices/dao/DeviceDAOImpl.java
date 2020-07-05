package vn.edu.hcmut.iotserver.iotdevices.dao;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import vn.edu.hcmut.iotserver.DeviceType;

public class DeviceDAOImpl implements DeviceDAO{



    @Override
    public JSONArray getNewestDeviceStatus(DeviceType deviceType) {
        return null;
    }

    @Override
    public JSONObject getDeviceStatus7Day(DeviceType deviceType, String deviceId) {
        return null;
    }
}
