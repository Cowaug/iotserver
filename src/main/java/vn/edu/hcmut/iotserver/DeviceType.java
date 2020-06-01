package vn.edu.hcmut.iotserver;

public enum DeviceType {
    SENSOR_LIGHT("lightSensor"),
    SENSOR_TEMP("temperatureSensor"),
    SENSOR_PLANT("plantSensor"),
    IDENT_LIGHT("indicateLight"),
    LIGHT_BULB("lightBulb"),
    AIR_CONDITIONER("airConditioner"),
    MOTOR("motor");

    private final String value;
    DeviceType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
}