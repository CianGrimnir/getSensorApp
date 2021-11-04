package com.collection.getsensordata;

public class DataStorageClass {

    long timestamp;
    float x, y, z;

    // To avoid error for firebase - create empty constructor.
    public DataStorageClass() {
    }

    public DataStorageClass(long timestamp, float valueX, float valueY, float valueZ) {
        this.timestamp = timestamp;
        this.x = valueX;
        this.y = valueY;
        this.z = valueZ;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
