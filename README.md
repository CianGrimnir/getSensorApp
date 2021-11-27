# getSensorApp

Android app to collect sensor data from gps sensors and store it in a firebase for mapping with
OpenData

### INSTRUCTIONS -

#### Enter to the root directory of the App project -

```
cd GetSensorData/
```

#### Execute gradle command to build the apk file - (Make sure you have correct version of jdk installed in your system ( =< jdk16.0.2), else gradlew task will fail.)

```
gradlew assembleDebug
```

#### Once the build task is completed, you will find the apk file under -

```
ls -l GetSensorData/app/build/outputs/apk/debug/
```

#### How to run it?

Copy the apk to your smartphone and once installed, open the app "GetSensorData" and you will see
start button. After selecting start button, App will start recording readings from gps sensor to the
firebase Realtime database already configured behind it. At the sametime, data from the below open
mentioned data API are collected and stored in the same realtime database simultaneously.

- https://data.gov.ie/dataset/met-eireann-weather-forecast-api
- https://api.openweathermap.org
- https://aqicn.org/api/