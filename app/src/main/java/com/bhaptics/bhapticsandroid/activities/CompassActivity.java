package com.bhaptics.bhapticsandroid.activities;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bhaptics.bhapticsandroid.BhapticsModule;
import com.bhaptics.bhapticsandroid.R;
import com.bhaptics.bhapticsmanger.HapticPlayer;
import com.bhaptics.commons.model.PathPoint;
import com.bhaptics.commons.model.PositionType;

import java.util.Arrays;


public class CompassActivity extends Activity implements View.OnClickListener {
    public static final String TAG = CompassActivity.class.getSimpleName();

    private ImageView imView;
    private TextView orientView;

    private Button backButton;
    private Button pauseButton;
    private Button uiButton;

    private float currentDegree = 0f;
    private boolean liveHaptics = true;
    private boolean isLockedIn = false;
    private int uiVersion = 2;

    private SensorManager sensorManager;
    private Sensor baseSensor;

    private HapticPlayer hapticPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        imView = findViewById(R.id.pointerView);
        orientView = findViewById(R.id.orientText);
        pauseButton = findViewById(R.id.pause_button);
        uiButton = findViewById(R.id.toggle_UI);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        baseSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        hapticPlayer = BhapticsModule.getHapticPlayer();

        SensorEventListener sensorEventListenerOrientation = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                int degree = Math.round(event.values[0]);
                RotateAnimation animation = new RotateAnimation(currentDegree,-degree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(500);
                animation.setFillAfter(true);
                imView.setAnimation(animation);
                currentDegree = -degree;
                orientView.setText(String.valueOf(-degree));
                if(liveHaptics) {
                    if(uiVersion == 2){
                        hapticOnLocation2(degree, 100);
                    }else {
                        hapticOnLocation(degree, 100);
                    }

                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        sensorManager.registerListener(sensorEventListenerOrientation, baseSensor, SensorManager.SENSOR_DELAY_FASTEST, SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    public void onClick(View v) {
        finish();
    }

    public void toggleHaptics (View view){
        if(liveHaptics){
            liveHaptics = false;
            pauseButton.setText("Resume Haptics");
        }else{
            liveHaptics = true;
            pauseButton.setText("Pause Haptics");
        }
    }

    public void toggleUI (View view){
        if(uiVersion == 1){
            uiVersion = 2;
            uiButton.setText("UI 2 Live");
        }else{
            uiVersion = 1;
            uiButton.setText("UI 1 Live");
        }
    }

    private void hapticOnLocation(float direction, int intensity){

        // steady vibration to magnetic north

        Log.i("Haptics", "Registering");

        float xNormalized;

        if(direction >= 270f || direction <= 90f){

            if(direction >= 270f){
                xNormalized = 2.5f - (direction / 180f);
            }else{
                xNormalized = .5f - (direction / 180f);
            }

            hapticPlayer.submitPath("VestFront", PositionType.VestFront,
                    Arrays.asList(new PathPoint(xNormalized, 1f, intensity)), 1000);
        }else {
            if(direction <= 180f){
                xNormalized = direction / 180f;
            }else{
                xNormalized = -(.5f - (direction / 180f));
            }

            hapticPlayer.submitPath("VestBack", PositionType.VestBack,
                    Arrays.asList(new PathPoint(xNormalized, 1f, intensity)), 1000);
        }
    }

    private void hapticOnLocation2(float direction, int intensity) {

        //hard vibration at front .5 any time user faces 350 - 10 degrees and back .5 at 170 - 190

        float xNormalized;

        if(direction >= 350f || direction <= 10f){

            if(!isLockedIn) {
                isLockedIn = true;
                hapticPlayer.submitPath("VestFront", PositionType.VestFront,
                        Arrays.asList(new PathPoint(.5f, 1f, 100)), 250);
            }
        }else if(direction >= 170f && direction <= 190f) {

            if(!isLockedIn) {
                isLockedIn = true;
                hapticPlayer.submitPath("VestBack", PositionType.VestBack,
                        Arrays.asList(new PathPoint(.5f, 1f, 100)), 250);
            }
        }else{
            isLockedIn = false;
        }
    }
}
