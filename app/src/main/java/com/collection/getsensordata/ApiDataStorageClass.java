package com.collection.getsensordata;

public class ApiDataStorageClass {

    long timestamp;
    String device_id, areaName;
    float latitude, longitude;

    // To avoid error for firebase - create empty constructor.
    public ApiDataStorageClass() {
    }

    public ApiDataStorageClass(long timestamp, String device_id, String areaName, float latitude, float longitude) {
        this.timestamp = timestamp;
        this.device_id = device_id;
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
