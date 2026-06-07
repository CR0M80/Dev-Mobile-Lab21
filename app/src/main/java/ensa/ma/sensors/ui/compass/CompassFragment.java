package ensa.ma.sensors.ui.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import ensa.ma.sensors.R;
import ensa.ma.sensors.views.CompassView;

public class CompassFragment extends Fragment implements SensorEventListener {

    private CompassView image;
    private float currentDegree = 0f;
    private SensorManager mSensorManager;
    private Sensor mCompassSensor;
    TextView tvHeading;

    public CompassFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compass, container, false);

        image = root.findViewById(R.id.compass_view);
        tvHeading = root.findViewById(R.id.text_azimuth);

        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mCompassSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        if (mCompassSensor == null) {
            Toast.makeText(getContext(), R.string.message_neg, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mCompassSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float degree = Math.round(event.values[0]);
        tvHeading.setText("Heading: " + degree + " degrees");

        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(210);
        ra.setFillAfter(true);
        image.startAnimation(ra);

        currentDegree = -degree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}