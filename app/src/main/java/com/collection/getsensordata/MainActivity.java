package com.collection.getsensordata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private List list;
    private final static long ACC_CHECK_INTERVAL = 100000;
    private long lastAccCheck;

    private long timestamp = 0;
    private final static String URL = "https://api.data.gov.sg/v1/environment/air-temperature";
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference sensorRef = rootRef.child("sensor_data");
    DatabaseReference openDataRef = rootRef.child("open_data");

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
        lastAccCheck = System.currentTimeMillis();
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);

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
                long currTime = System.currentTimeMillis();
                long epochTimeStamp = System.currentTimeMillis() + ((event.timestamp - SystemClock.elapsedRealtimeNanos()) / 1000000L);
                float xvalue = event.values[0];
                float yvalue = event.values[1];
                float zvalue = event.values[2];
                DataStorageClass storeData = new DataStorageClass(timestamp, xvalue, yvalue, zvalue);
                mTextView.setText("x: " + xvalue + "\ny: " + yvalue + "\nz: " + zvalue);
                if (currTime - lastAccCheck > ACC_CHECK_INTERVAL) {
                    sensorRef.child(String.valueOf(epochTimeStamp)).setValue(storeData);
                    lastAccCheck = currTime;
                }
            } catch (Exception e) {
                Log.d("sensorData", "Exemption raised while writing to a firebase.");
                e.printStackTrace();
            }
            new FetchOpenDataTask().execute(URL);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Class to fetch data from the open api and feed it to the firebase database.
     */
    private class FetchOpenDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder buffer = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
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

        @Override
        protected void onPostExecute(String dataFetched) {
            //parse the JSON data and then display
            parseJSON(dataFetched);
        }

        /**
         * parse json data fetched from the api and push the parsed data to firebase
         *
         * @param data - metadata from the api
         */
        private void parseJSON(String data) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONObject getTimestamp = jsonObject.getJSONArray("items").getJSONObject(0);
                long time_stamp = getEpoch(getTimestamp.getString("timestamp"));
                if (time_stamp > timestamp) {
                    JSONObject stations = jsonObject.getJSONObject("metadata");
                    JSONArray stationArray = stations.getJSONArray("stations");
                    for (int i = 0; i < stationArray.length(); i++) {
                        JSONObject sensorData = stationArray.getJSONObject(i);
                        String deviceId = sensorData.getString("device_id");
                        String name = sensorData.getString("name");
                        JSONObject location = sensorData.getJSONObject("location");
                        float latitude = (float) location.getDouble("latitude");
                        float longitude = (float) location.getDouble("longitude");
                        ApiDataStorageClass apiData = new ApiDataStorageClass(time_stamp, deviceId, name, latitude, longitude);
                        DatabaseReference areaRef = openDataRef.child(name);
                        areaRef.child(String.valueOf(time_stamp)).setValue(apiData);
                        timestamp = time_stamp;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Convert datetime format to epoch time
         *
         * @param timestamp - Date - 2021-11-04T09:10:00+08:00
         * @return converted epoch time
         */
        private long getEpoch(String timestamp) {
            long epoch = 0;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");
            try {
                Date date = df.parse(timestamp);
                epoch = date.getTime();
            } catch (Exception e) {
                Log.d("sensorData", String.valueOf(e));
            }
            return epoch;
        }
    }
}