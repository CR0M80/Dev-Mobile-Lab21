package ensa.ma.sensors.ui.home;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import ensa.ma.sensors.R;
import ensa.ma.sensors.databinding.FragmentHomeBinding;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        
        setupClickListeners();
        updateSensorCount();
        
        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.cardSensorsList.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.nav_list));
            
        binding.cardActivityAi.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.nav_activity));
            
        binding.cardStepsTracker.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.nav_steps));
            
        binding.cardSmartCompass.setOnClickListener(v -> 
            Navigation.findNavController(v).navigate(R.id.nav_compass));
    }

    private void updateSensorCount() {
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        binding.textSensorCount.setText("Detected " + sensors.size() + " active sensors on this device.");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
