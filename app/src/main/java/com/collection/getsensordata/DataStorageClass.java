package com.collection.getsensordata;

public class DataStorageClass {

    long timestamp;
    double longitude, latitude;

    // To avoid error for firebase - create empty constructor.
    public DataStorageClass() {
    }

    public DataStorageClass(long timestamp, double longitude, double latitude) {
        this.timestamp = timestamp;
        this.longitude = longitude;
        this.latitude = latitude;
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
}
