package com.collection.getsensordata;

public class ApiDataStorageClass {

    long timestamp;
    String device_id, area_name;
    float latitude, longitude, reading;

    // To avoid error for firebase - create empty constructor.
    public ApiDataStorageClass() {
    }

    public ApiDataStorageClass(long timestamp, String device_id, String area_name, float latitude, float longitude, float reading) {
        this.timestamp = timestamp;
        this.device_id = device_id;
        this.area_name = area_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.reading = reading;
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

    public String getArea_name() {
        return area_name;
    }

    public void setArea_name(String area_name) {
        this.area_name = area_name;
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

    public float getReading() {
        return reading;
    }

    public void setReading(float reading) {
        this.reading = reading;
    }
}
