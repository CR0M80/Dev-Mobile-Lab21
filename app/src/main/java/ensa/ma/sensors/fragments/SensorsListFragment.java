package ensa.ma.sensors.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ensa.ma.sensors.databinding.FragmentSensorsListBinding;
import ensa.ma.sensors.databinding.ItemSensorBinding;
import ensa.ma.sensors.utils.SensorFormatter;

import java.util.List;

public class SensorsListFragment extends Fragment {

    private FragmentSensorsListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSensorsListBinding.inflate(inflater, container, false);
        
        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        binding.recyclerSensors.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSensors.setAdapter(new SensorsAdapter(sensors));

        return binding.getRoot();
    }

    private static class SensorsAdapter extends RecyclerView.Adapter<SensorsAdapter.ViewHolder> {
        private final List<Sensor> sensors;

        SensorsAdapter(List<Sensor> sensors) {
            this.sensors = sensors;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemSensorBinding binding = ItemSensorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            Sensor sensor = sensors.get(i);
            holder.binding.textSensorName.setText(sensor.getName());
            
            String formattedText = SensorFormatter.format(sensor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.textSensorDetails.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.binding.textSensorDetails.setText(Html.fromHtml(formattedText));
            }
        }

        @Override
        public int getItemCount() {
            return sensors.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemSensorBinding binding;
            ViewHolder(ItemSensorBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
