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

    // try with commenting suppress lint
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
        String current_path = getExternalFilesDir(null).getAbsolutePath();


        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
         */
        buttonStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(false);
                buttonStop.setEnabled(true);
                list = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                // File root = android.os.Environment.getExternalStorageDirectory();
                // String current_path = root.getAbsolutePath() + "/sensorData";

                File directory = new File(current_path);

                if (! directory.exists()){

                    directory.mkdir();
                }
                //Log.d("sensorData","testing path - " + test_path);
                Log.d("sensorData", "writing to - " + current_path);
                try {
                    //writer = new FileWriter(new File(current_path, "sensor_" + System.currentTimeMillis() + ".csv"));
                    writer = new FileWriter(new File(current_path, getFilename()));
                } catch (IOException e) {
                    Log.d("sensorData","inside onTouch catch method");
                    e.printStackTrace();
                }
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
        buttonStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                buttonStart.setEnabled(true);
                buttonStop.setEnabled(false);
                isRunning = false;
                sensorManager.flush(MainActivity.this);
                sensorManager.unregisterListener(MainActivity.this);
                try{
                    writer.close();
                } catch (IOException e) {
                    Log.d("sensorData","inside setonTouchListener");
                    e.printStackTrace();
                }
                return true;
            }
        });
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onSensorChanged(SensorEvent event){
        if(isRunning){
            try {
                //Log.d("sensorData", String.valueOf("value is " + event.values[2] +":"+ event.values[1] + ":"+event.values[0]));
                mTextView.setText("x: " + event.values[0] + "\ny: " + event.values[1] + "\nz: " + event.values[2]);
                writer.write(String.format("%d, %f, %f, %f\n", event.timestamp, event.values[0], event.values[1], event.values[2]));
            } catch (IOException e) {
                Log.d("sensorData","inside onSensorChanged");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
            try {

                writer = new FileWriter(new File(getExternalFilesDir(null).getAbsolutePath(), getFilename()), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getFilename() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd", Locale.US);
        Date now = new Date();
        return "sensor_" + formatter.format(now) + ".csv";
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}