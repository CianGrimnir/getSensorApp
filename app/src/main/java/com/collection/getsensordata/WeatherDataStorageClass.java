package com.collection.getsensordata;

public class WeatherDataStorageClass {
    float latitude, longitude, temperature, feel_like_temperature, min_temperature, max_temperature, wind_speed;
    String sunrise, sunset, cloud_description, formatted_timeStamp;
    Long time_stamp;

    public WeatherDataStorageClass() {
    }

    public WeatherDataStorageClass(float latitude, float longitude, float temperature, float feel_like_temperature, float min_temperature, float max_temperature, float wind_speed, String sunrise, String sunset, String cloud_description, String formatted_timeStamp, Long time_stamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.feel_like_temperature = feel_like_temperature;
        this.min_temperature = min_temperature;
        this.max_temperature = max_temperature;
        this.wind_speed = wind_speed;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.cloud_description = cloud_description;
        this.formatted_timeStamp = formatted_timeStamp;
        this.time_stamp = time_stamp;
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

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getFeel_like_temperature() {
        return feel_like_temperature;
    }

    public void setFeel_like_temperature(float feel_like_temperature) {
        this.feel_like_temperature = feel_like_temperature;
    }

    public float getMin_temperature() {
        return min_temperature;
    }

    public void setMin_temperature(float min_temperature) {
        this.min_temperature = min_temperature;
    }

    public float getMax_temperature() {
        return max_temperature;
    }

    public void setMax_temperature(float max_temperature) {
        this.max_temperature = max_temperature;
    }

    public float getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(float wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public String getCloud_description() {
        return cloud_description;
    }

    public void setCloud_description(String cloud_description) {
        this.cloud_description = cloud_description;
    }

    public String getFormatted_timeStamp() {
        return formatted_timeStamp;
    }

    public void setFormatted_timeStamp(String formatted_timeStamp) {
        this.formatted_timeStamp = formatted_timeStamp;
    }

    public Long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
