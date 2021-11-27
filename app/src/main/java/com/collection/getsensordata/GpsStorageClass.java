package com.collection.getsensordata;

public class GpsStorageClass {

    long timestamp;
    double longitude, latitude;
    String user;

    // To avoid error for firebase - create empty constructor.
    public GpsStorageClass() {
    }

    public GpsStorageClass(long timestamp, double longitude, double latitude, String androidID) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user = androidID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
