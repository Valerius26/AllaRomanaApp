package com.example.allaromanaapp;
import com.example.allaromanaapp.notificationActivity;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity{

    private TextView light;
    private SeekBar brightBar;
    public int brightness;
    private ContentResolver contentResolver;
    public Window window;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorEventListener lightEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        brightBar = (SeekBar) findViewById(R.id.brightBar);
        light = findViewById(R.id.lightSensor);

        contentResolver = getContentResolver();
        window = getWindow();
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        brightBar.setMax(255);
        brightBar.setKeyProgressIncrement(1);

        if(lightSensor==null){
            Toast.makeText(getApplicationContext(),"Il dispositivo non ha il sensore di luce :(", Toast.LENGTH_LONG).show();
            return;
        }

        try{
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            brightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        }catch (Exception e){
            Log.e("ERROR","CANNOT ACCESS SYSTEM BRIGHTNESS");
            e.printStackTrace();
        }

        brightBar.setProgress(brightness);

        brightBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress<=10){
                    brightness = 10;
                }else{
                    brightness = progress;
                }



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                android.provider.Settings.System.putInt(contentResolver,
                        android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
                WindowManager.LayoutParams layoutParams = window.getAttributes();

                layoutParams.screenBrightness = brightness / (float) 255;
                window.setAttributes(layoutParams);
            }
        });






        lightEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float value = sensorEvent.values[0];
                light.setText(getString(R.string.brightness) + " " + value + "lx");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightEventListener);

        try{
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            brightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        }catch (Exception e){
            Log.e("ERROR","CANNOT ACCESS SYSTEM BRIGHTNESS");
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(lightEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        android.provider.Settings.System.putInt(contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        layoutParams.screenBrightness = brightness / (float) 255;
        window.setAttributes(layoutParams);
    }


}
