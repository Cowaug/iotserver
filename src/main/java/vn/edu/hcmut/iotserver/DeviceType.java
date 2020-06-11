package vn.edu.hcmut.iotserver;

public enum DeviceType {
    SENSOR_LIGHT("SENSOR_LIGHT"),
    SENSOR_TEMP("SENSOR_TEMP"),
    SENSOR_PLANT("SENSOR_PLANT"),
    INDICATE_LIGHT("INDICATE_LIGHT"),
    LIGHT_BULB("LIGHTBULB"),
    AIR_CONDITIONER("AIR_CONDITIONER"),
    MOTOR("MOTOR");

    private final String databaseName;
    DeviceType(String databaseName){
        this.databaseName = databaseName;
    }

    public String getDatabase() {
        return databaseName;
    }
}