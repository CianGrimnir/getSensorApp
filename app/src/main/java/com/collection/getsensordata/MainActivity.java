package com.collection.getsensordata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    FileWriter writer;
    private TextView mTextView;
    private List list;
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
        super.onCreate(savedState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.textView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStop = (Button) findViewById(R.id.buttonStop);
        //String current_path = getExternalFilesDir(null).getAbsolutePath();

        /**
         * This callback will be invoked when a start button is dispatched.
         */
        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                /*
                File directory = new File(current_path);
                if (! directory.exists()){

                    directory.mkdir();
                }
                Log.d("sensorData", "writing to - " + current_path);
                try {
                    writer = new FileWriter(new File(current_path, getFilename()));
                } catch (IOException e) {
                    Log.d("sensorData","Exemption raised while creating a writer.");
                    e.printStackTrace();
                }
                */

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
                /*
                try{
                    writer.close();
                } catch (IOException e) {
                    Log.d("sensorData","Exemption raised while closing the writer.");
                    e.printStackTrace();
                }*/
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
                long timestamp = event.timestamp;
                float xvalue = event.values[0];
                float yvalue = event.values[1];
                float zvalue = event.values[2];
                DataStorageClass storeData = new DataStorageClass(timestamp, xvalue, yvalue, zvalue);
                mTextView.setText("x: " + xvalue + "\ny: " + yvalue + "\nz: " + zvalue);
                sensorRef.child(String.valueOf(timestamp)).setValue(storeData);
                //writer.write(String.format("%d, %f, %f, %f\n", timestamp, xvalue, yvalue, zvalue));
            } catch (Exception e) {
                Log.d("sensorData", "Exemption raised while writing to a firebase.");
                e.printStackTrace();
            }
        }
    }

    /** Called when the activity will start interacting with the user.
     @Override public void onResume(){
     super.onResume();
     try {
     writer = new FileWriter(new File(getExternalFilesDir(null).getAbsolutePath(), getFilename()), true);
     } catch (IOException e) {
     e.printStackTrace();
     }
     }
     */

    /**
     * Returns filename for the csv after formatting with date.
     * <p>
     * return String - csv filename.
     * <p>
     * public String getFilename() {
     * SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
     * Date now = new Date();
     * return "sensor_" + formatter.format(now) + ".csv";
     * }
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}