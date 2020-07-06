package vn.edu.hcmut.iotserver.models;

import org.json.simple.JSONObject;

import java.sql.Timestamp;

public class NewestDeviceStatus {
    private long time;
    private String deviceId;
    private String status;
    private String mode;
    private int temperature = -1;
    private int humid = -1;
    private int lightValue = -1;

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("time", time);
        jsonObject.put("device_id", deviceId);
        jsonObject.put("status", status);
        jsonObject.put("mode", mode.toLowerCase());

        if (temperature > -1)
            jsonObject.put("temperature", temperature);
        if (humid > -1)
            jsonObject.put("humid", humid);
        if (lightValue > -1)
            jsonObject.put("light_value", lightValue);

        return jsonObject;
    }

    public long getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time.getTime();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumid() {
        return humid;
    }

    public void setHumid(int humid) {
        this.humid = humid;
    }

    public int getLightValue() {
        return lightValue;
    }

    public void setLightValue(int lightValue) {
        this.lightValue = lightValue;
    }
}
