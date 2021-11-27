package com.collection.getsensordata;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mSensor;
    Button buttonStart;
    Button buttonStop;
    boolean isRunning;
    private TextView mTextView;
    private TextView weatherTextView;
    private TextView aqiTextView;
    private TextView forecastTextView;
    private List list;
    private final static long ACC_CHECK_INTERVAL = 100000;
    private long lastAccCheck, weather_last_updated, aqi_last_updated;
    private FusedLocationProviderClient client;
    boolean apiFlag = true;
    private String geoWeatherURL, geoAQIURL, geoForecastURL, weatherInfo, aqiInfo;
    private long timestamp = 0;
    private final static String FORECAST_URL = "http://metwdb-openaccess.ichec.ie/metno-wdb2ts/locationforecast?lat=%s;long=%s;from=%s;to=%s";
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&APPID=%s&units=metric";
    private static final String AQ_URL = "https://api.waqi.info/feed/geo:%s;%s/?token=%s";
    private final String aqi_token = "6723c996f2f554c12e670e1481673643c84bd21c";
    private final String weather_token = "0dec160c060873c9131842af25ea13d5";
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference sensorRef = rootRef.child("gps_data");
    DatabaseReference openDataRef = rootRef.child("weather_data");
    DatabaseReference aqiDataRef = rootRef.child("aqi_data");
    DatabaseReference forecastDataRef = rootRef.child("forecast_data");
    String nodeRef, androidId = "";


    /**
     * Called when the activity is starting. This is where most initialization should go
     *
     * @param savedState -  Bundle object that is passed into the onCreate method of every Android
     *                   Activity, contains the most recent data , specially the data which was used
     *                   previously in the activity.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedState) {
        isRunning = false;
        lastAccCheck = 0;
        super.onCreate(savedState);
        requestPermission();
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);
        weatherTextView = (TextView) findViewById(R.id.textView2);
        aqiTextView = (TextView) findViewById(R.id.textView3);
        forecastTextView = (TextView) findViewById(R.id.textView4);
        forecastTextView.setMovementMethod(new ScrollingMovementMethod());
        client = LocationServices.getFusedLocationProviderClient(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        /**
         * This callback will be invoked when a start button is dispatched.
         */
        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                if (list.size() > 0) {
                    mSensor = (Sensor) list.get(0);
                    sensorManager.registerListener(MainActivity.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    Toast.makeText(getBaseContext(), "Error: Accelerometer not found", Toast.LENGTH_LONG).show();
                }
                isRunning = true;
                return true;
            }
        });
        /**
         * This callback will be invoked when a stop button is dispatched.
         */
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                sensorManager.flush(MainActivity.this);
                sensorManager.unregisterListener(MainActivity.this);
                return true;
            }
        });
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    /**
     * Called when there is a new sensor event.
     *
     * @param event SensorEvent =  holds information such as the sensor's type,
     *              the time-stamp, accuracy and of course the sensor's data.
     */
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning) {
            try {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("sensor", "permission issue");
                    requestPermission();
                }

                client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onSuccess(Location o) {
                        double latitude = o.getLatitude();
                        double longitude = o.getLongitude();
                        long currTime = System.currentTimeMillis();
                        long epochTimeStamp = System.currentTimeMillis() + ((event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L);
                        nodeRef = String.format("%s%s", Math.abs(latitude), Math.abs(longitude)).replace(".", "");
                        mTextView.setText("Latitude: " + latitude + "\nLongitude: " + longitude);
                        GpsStorageClass storeData = new GpsStorageClass(epochTimeStamp, longitude, latitude, androidId);
                        if (currTime - lastAccCheck > ACC_CHECK_INTERVAL) {
                            DatabaseReference gpsNodeRef = sensorRef.child(androidId).child(nodeRef);
                            gpsNodeRef.child(String.valueOf(epochTimeStamp)).setValue(storeData);
                            lastAccCheck = currTime;
                            apiFlag = true;
                        }
                        DateFormatForecastWrapper forecastRange = utils.formatTimeDate((long) (epochTimeStamp / Math.pow(10, 3)));
                        geoForecastURL = String.format(FORECAST_URL, latitude, longitude, forecastRange.currentTimeStamp, forecastRange.ForecastTimeStamp);
                        geoWeatherURL = String.format(WEATHER_URL, latitude, longitude, weather_token);
                        geoAQIURL = String.format(AQ_URL, latitude, longitude, aqi_token);
                    }
                });
            } catch (Exception e) {
                Log.d("sensorData", "Exemption raised while writing to a firebase.");
                e.printStackTrace();
            }

            if (apiFlag) {
                new FetchOpenDataTask().execute(geoWeatherURL, "weather");
                new FetchOpenDataTask().execute(geoAQIURL, "aqi");
                new FetchOpenDataTask().execute(geoForecastURL, "forecast");
                apiFlag = false;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Class to fetch data from the open api and feed it to the firebase database.
     */
    private class FetchOpenDataTask extends AsyncTask<String, Void, GetWrapper> {

        @Override
        protected GetWrapper doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            InputStream inputStream;
            GetWrapper wrapper = new GetWrapper();
            try {
                if (params[0] == null) {
                    return null;
                }
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                wrapper.response_data = buffer.toString();
                wrapper.api_info = params[1];
                return wrapper;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(GetWrapper dataFetched) {
            //parse the JSON/XML data and then display
            if (dataFetched == null) {
                return;
            }
            if (dataFetched.api_info.equals("weather")) parseWeatherJSON(dataFetched.response_data);
            if (dataFetched.api_info.equals("aqi")) parseAQIJSON(dataFetched.response_data);
            if (dataFetched.api_info.equals("forecast")) {
                String forecastData = utils.parseForecastXML(dataFetched.response_data, forecastDataRef, nodeRef, androidId);
                forecastTextView.setText(forecastData);
            }

        }

        /**
         * parse json data fetched from the weather api and push the parsed data to firebase
         *
         * @param data - metadata from the api
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void parseWeatherJSON(String data) {
            try {
                String weatherInfo = " Last updated %s\n cloud description %s\n temperature %s\u2103\t\t feels like %s\u2103\n min temperature %s\u2103\n max " +
                        "temperature %s\u2103\n wind speed %sm/s\n sunrise time %s\n sunset time %s\n";
                JSONObject jsonObject = new JSONObject(data);
                JSONObject getTemperature = jsonObject.getJSONObject("main");
                float latitude = (float) jsonObject.getJSONObject("coord").getDouble("lat");
                float longitude = (float) jsonObject.getJSONObject("coord").getDouble("lon");
                float temperature = (float) getTemperature.getDouble("temp");
                float feel_like_temperature = (float) getTemperature.getDouble("feels_like");
                float min_temperature = (float) getTemperature.getDouble("temp_min");
                float max_temperature = (float) getTemperature.getDouble("temp_max");
                float wind_speed = (float) jsonObject.getJSONObject("wind").getDouble("speed");
                String sunrise = utils.epochToDate(jsonObject.getJSONObject("sys").getLong("sunrise"));
                String sunset = utils.epochToDate(jsonObject.getJSONObject("sys").getLong("sunset"));
                long time_stamp = jsonObject.getLong("dt");
                DateFormatForecastWrapper tet = utils.formatTimeDate(time_stamp);
                String formatted_ts = utils.epochToDate(time_stamp);
                String cloud_des = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                WeatherDataStorageClass weatherMetadata = new WeatherDataStorageClass(latitude, longitude, temperature, feel_like_temperature, min_temperature, max_temperature, wind_speed, sunrise, sunset, cloud_des, formatted_ts, androidId, time_stamp);
                String weatherman = String.format(weatherInfo, formatted_ts, cloud_des, temperature, feel_like_temperature, min_temperature, max_temperature, wind_speed, sunrise, sunset);
                weatherTextView.setText(weatherman);
                DatabaseReference nodeDataRef = openDataRef.child(androidId).child(nodeRef);
                nodeDataRef.child(String.valueOf(time_stamp)).setValue(weatherMetadata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * parse json data fetched from the air quality api and push the parsed data to firebase
         *
         * @param data - metadata from the api
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        private void parseAQIJSON(String data) {
            try {
                String aqiText = " AQI %s\n last updated %s\n";
                JSONObject jsonObject = new JSONObject(data);
                JSONObject jsonData = jsonObject.getJSONObject("data");
                int aqi = jsonData.getInt("aqi");
                JSONArray coordinate = jsonData.getJSONObject("city").getJSONArray("geo");
                float latitude = (float) coordinate.getDouble(0);
                float longitude = (float) coordinate.getDouble(1);
                long last_updated = jsonObject.getJSONObject("data").getJSONObject("time").getLong("v");
                String aqiInfo = String.format(aqiText, aqi, utils.epochToDate(last_updated));
                aqiTextView.setText(aqiInfo);
                AQIDataStorageClass aqiMetadata = new AQIDataStorageClass(aqi, last_updated, latitude, longitude, androidId);
                DatabaseReference nodeDataRef = aqiDataRef.child(androidId).child(nodeRef);
                nodeDataRef.child(String.valueOf(last_updated)).setValue(aqiMetadata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}