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

import ensa.ma.sensors.databinding.FragmentSensorMonitorBinding;

import java.util.Locale;

public class SensorMonitorFragment extends Fragment implements SensorEventListener {

    private static final String ARG_TYPE = "type";
    private static final String ARG_TITLE = "title";
    private static final String ARG_USE_MAGNITUDE = "use_magnitude";

    private FragmentSensorMonitorBinding binding;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int sensorType;
    private boolean useMagnitude;

    public static SensorMonitorFragment newInstance(int type, String title, boolean useMagnitude) {
        SensorMonitorFragment fragment = new SensorMonitorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        args.putString(ARG_TITLE, title);
        args.putBoolean(ARG_USE_MAGNITUDE, useMagnitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sensorType = getArguments().getInt(ARG_TYPE);
            useMagnitude = getArguments().getBoolean(ARG_USE_MAGNITUDE);
        }
        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorMonitorBinding.inflate(inflater, container, false);
        binding.textSensorTitle.setText(getArguments().getString(ARG_TITLE));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            binding.textSensorValue.setText("Sensor not available");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value;
        if (useMagnitude && event.values.length >= 3) {
            value = (float) Math.sqrt(event.values[0] * event.values[0] +
                    event.values[1] * event.values[1] +
                    event.values[2] * event.values[2]);
        } else {
            value = event.values[0];
        }

        binding.textSensorValue.setText(String.format(Locale.US, "%.2f", value));
        binding.chartView.addValue(value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
