package com.iot.iotserver;

public enum DeviceType {
    LIGHT_SENSOR("lightSensor"),

    ;

    private final String value;
    DeviceType(String value){
        this.value=value;
    }
}
