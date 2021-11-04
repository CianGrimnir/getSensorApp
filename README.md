# getSensorApp

Android app to collect Accelerometer in csv file for mapping with OpenData

### INSTRUCTIONS -

#### Enter to the root directory of the App project -

```
cd GetSensorData/
```

#### Execute gradle command to build the apk file - (Make sure you have correect version of jdk installed in your system ( =< jdk16.0.2), else gradlew task will fail.)

```
gradlew assembleDebug
```

#### Once the build task is completed, you will find the apk file under -

```
ls -l GetSensorData/app/build/outputs/apk/debug/
```

#### How to run it?

Copy the apk to your smartphone and once installed, open the app "GetSensorData" and you will see
start button. After selecting start button, App will start recording readings from Accelerometer to
the firebase Realtime database already configured behind it. At the sametime, data from the open
data API - https://data.gov.sg/dataset/realtime-weather-readings are collected and stored in the
same realtime database simultaneously.