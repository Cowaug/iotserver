package vn.edu.hcmut.iotserver;

public enum DeviceType {
    // Sensor
    SENSOR_LIGHT("SENSOR_LIGHT",null),
    Light("SENSOR_LIGHT",null),

    SENSOR_TEMP("SENSOR_TEMP",null),
    TempHumi("SENSOR_TEMP",null),

    SENSOR_PLANT("SENSOR_PLANT",null),
    Mois("SENSOR_PLANT",null),


    // Device
    INDICATE_LIGHT("INDICATE_LIGHT",null),
    LightD("INDICATE_LIGHT_TEST","SENSOR_LIGHT"),
    Speaker("SPEAKER","SENSOR_PLANT"),
    LIGHT_BULB("LIGHTBULB","SENSOR_LIGHT"),
    AIR_CONDITIONER("AIR_CONDITIONER","SENSOR_TEMP"),
    MOTOR("MOTOR","SENSOR_PLANT");

    private final String databaseName;
    private final String databaseName2;
    DeviceType(String databaseName,String databaseName2){
        this.databaseName = databaseName;
        this.databaseName2=databaseName2;
    }

    public String getDatabase() {
        return databaseName;
    }
    public String getDatabase2() {
        return databaseName2;
    }
}