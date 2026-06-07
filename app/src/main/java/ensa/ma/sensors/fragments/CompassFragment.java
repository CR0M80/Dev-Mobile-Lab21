package ensa.ma.sensors.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ensa.ma.sensors.databinding.FragmentCompassBinding;

import java.util.Locale;

public class CompassFragment extends Fragment implements SensorEventListener {

    private FragmentCompassBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private final float[] gravityValues = new float[3];
    private final float[] magneticValues = new float[3];
    private boolean hasGravity = false;
    private boolean hasMagnetic = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompassBinding.inflate(inflater, container, false);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
        
        if (accelerometer == null || magnetometer == null) {
            binding.textDirection.setText("Missing Sensors");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3);
            hasGravity = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magneticValues, 0, 3);
            hasMagnetic = true;
        }

        if (hasGravity && hasMagnetic) {
            float[] rotationMatrix = new float[9];
            float[] orientation = new float[3];

            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, magneticValues)) {
                SensorManager.getOrientation(rotationMatrix, orientation);
                float azimuthRadians = orientation[0];
                float azimuthDegrees = (float) Math.toDegrees(azimuthRadians);
                if (azimuthDegrees < 0) azimuthDegrees += 360;

                binding.compassView.setAzimuth(azimuthDegrees);
                binding.textAzimuth.setText(String.format(Locale.US, "%.0f°", azimuthDegrees));
                binding.textDirection.setText(getDirectionName(azimuthDegrees));
            }
        }
    }

    private String getDirectionName(float degree) {
        if (degree >= 337.5 || degree < 22.5) return "North";
        if (degree < 67.5) return "North-East";
        if (degree < 112.5) return "East";
        if (degree < 157.5) return "South-East";
        if (degree < 202.5) return "South";
        if (degree < 247.5) return "South-Ouest";
        if (degree < 292.5) return "Ouest";
        return "North-Ouest";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
