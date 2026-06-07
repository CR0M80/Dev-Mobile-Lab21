package ensa.ma.sensors.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import ensa.ma.sensors.databinding.FragmentStepCounterBinding;

import java.util.Locale;

public class StepCounterFragment extends Fragment implements SensorEventListener {

    private FragmentStepCounterBinding binding;
    private SensorManager sensorManager;
    private Sensor stepCounterSensor;
    private float initialSteps = -1;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startSensor();
                } else {
                    Toast.makeText(requireContext(), "Permission denied for step counting", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStepCounterBinding.inflate(inflater, container, false);
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stepCounterSensor == null) {
            binding.textTotalSteps.setText("N/A");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
        } else {
            startSensor();
        }
    }

    private void startSensor() {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float totalSteps = event.values[0];
        if (initialSteps < 0) {
            initialSteps = totalSteps;
        }

        int sessionSteps = (int) (totalSteps - initialSteps);
        binding.textTotalSteps.setText(String.format(Locale.US, "%d", (int) totalSteps));
        binding.textSessionSteps.setText(String.format(Locale.US, "Steps this session: %d", sessionSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
