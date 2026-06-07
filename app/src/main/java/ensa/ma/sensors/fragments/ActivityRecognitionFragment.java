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

import ensa.ma.sensors.R;
import ensa.ma.sensors.databinding.FragmentActivityRecognitionBinding;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

public class ActivityRecognitionFragment extends Fragment implements SensorEventListener {

    private FragmentActivityRecognitionBinding binding;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private final float[] gravity = new float[3];
    private final Queue<Float> movementWindow = new LinkedList<>();
    private static final int WINDOW_SIZE = 40;
    private static final float ALPHA = 0.85f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentActivityRecognitionBinding.inflate(inflater, container, false);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        // Low-pass filter to isolate gravity
        gravity[0] = ALPHA * gravity[0] + (1 - ALPHA) * x;
        gravity[1] = ALPHA * gravity[1] + (1 - ALPHA) * y;
        gravity[2] = ALPHA * gravity[2] + (1 - ALPHA) * z;

        // Remove gravity to get linear acceleration
        float linearX = x - gravity[0];
        float linearY = y - gravity[1];
        float linearZ = z - gravity[2];

        float magnitude = (float) Math.sqrt(linearX * linearX + linearY * linearY + linearZ * linearZ);
        
        if (movementWindow.size() >= WINDOW_SIZE) {
            movementWindow.poll();
        }
        movementWindow.add(magnitude);

        updateActivity(x, y, z);
    }

    private void updateActivity(float x, float y, float z) {
        if (movementWindow.size() < WINDOW_SIZE) {
            binding.textActivityLabel.setText("Calibrating...");
            return;
        }

        float sum = 0, max = 0;
        for (float m : movementWindow) {
            sum += m;
            if (m > max) max = m;
        }
        float avg = sum / WINDOW_SIZE;

        String activity;
        int iconRes;

        if (max > 12f) {
            activity = "Jumping!";
            iconRes = R.drawable.ic_menu_send; // Placeholder for jumping
        } else if (avg > 1.5f) {
            activity = "Walking";
            iconRes = R.drawable.ic_menu_share; // Placeholder for walking
        } else if (Math.abs(z) > 8.5f) {
            activity = "Stationary (Flat)";
            iconRes = R.drawable.ic_home;
        } else {
            activity = "Stationary (Upright)";
            iconRes = R.drawable.ic_home;
        }

        binding.textActivityLabel.setText(activity);
        binding.textMovementStats.setText(String.format(Locale.US, "Intensity: %.2f", avg));
        binding.imageActivityIcon.setImageResource(iconRes);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
