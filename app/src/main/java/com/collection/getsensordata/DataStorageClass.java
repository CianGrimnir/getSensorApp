package com.collection.getsensordata;

public class DataStorageClass {

    long timestamp;
    float valueX, valueY, valueZ;

    // To avoid error for firebase - create empty constructor.
    public DataStorageClass() {
    }

    public DataStorageClass(long timestamp, float valueX, float valueY, float valueZ) {
        this.timestamp = timestamp;
        this.valueX = valueX;
        this.valueY = valueY;
        this.valueZ = valueZ;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getValueX() {
        return valueX;
    }

    public void setValueX(float valueX) {
        this.valueX = valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public void setValueY(float valueY) {
        this.valueY = valueY;
    }

    public float getValueZ() {
        return valueZ;
    }

    public void setValueZ(float valueZ) {
        this.valueZ = valueZ;
    }
}
