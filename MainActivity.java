package com.example.mycompass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, magnetometer;
    private float[] gravity, geomagnetic;
    private TextView degreesText, directionText;
    private CompassView compassView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        degreesText = findViewById(R.id.degreesText);
        directionText = findViewById(R.id.directionText);
        compassView = findViewById(R.id.compassView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }

        if (gravity != null && geomagnetic != null) {
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];

            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuthInDegrees = (float) Math.toDegrees(orientation[0]);

                if (azimuthInDegrees < 0) {
                    azimuthInDegrees += 360;
                }

                // Update UI
                degreesText.setText(String.format("%.0f°", azimuthInDegrees));
                directionText.setText(getDirection(azimuthInDegrees));
                compassView.setAzimuth(azimuthInDegrees);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private String getDirection(float azimuth) {
        if (azimuth >= 337.5 || azimuth < 22.5) return "North";
        else if (azimuth >= 22.5 && azimuth < 67.5) return "Northeast";
        else if (azimuth >= 67.5 && azimuth < 112.5) return "East";
        else if (azimuth >= 112.5 && azimuth < 157.5) return "Southeast";
        else if (azimuth >= 157.5 && azimuth < 202.5) return "South";
        else if (azimuth >= 202.5 && azimuth < 247.5) return "Southwest";
        else if (azimuth >= 247.5 && azimuth < 292.5) return "West";
        else return "Northwest";
    }
}
