package vn.edu.hcmut.iotserver.models;
public class DeviceMode {
    private String mode;
    private int sensor1;
    private int sensor2;
    private String schOn;
    private String schOff;

    public Object[] toObjectArray(){
        return new Object[]{mode,sensor1,sensor2,schOn,schOff};
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode.toUpperCase();
    }

    public int getSensor1() {
        return sensor1;
    }

    public void setSensor1(int sensor1) {
        this.sensor1 = sensor1;
    }

    public int getSensor2() {
        return sensor2;
    }

    public void setSensor2(int sensor2) {
        this.sensor2 = sensor2;
    }

    public String getSchOn() {
        return schOn;
    }

    public void setSchOn(String schOn) {
        this.schOn = schOn;
    }

    public String getSchOff() {
        return schOff;
    }

    public void setSchOff(String schOff) {
        this.schOff = schOff;
    }
}
