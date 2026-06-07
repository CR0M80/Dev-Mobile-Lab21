package ensa.ma.sensors.utils;

import android.hardware.Sensor;
import android.os.Build;
import java.util.Locale;

public class SensorFormatter {

    public static String format(Sensor sensor) {
        StringBuilder sb = new StringBuilder();
        sb.append("<b>Name:</b> ").append(sensor.getName()).append("<br/>");
        sb.append("<b>Vendor:</b> ").append(sensor.getVendor()).append("<br/>");
        sb.append("<b>Type:</b> ").append(sensor.getStringType()).append(" (").append(sensor.getType()).append(")<br/>");
        sb.append("<b>Resolution:</b> ").append(String.format(Locale.US, "%.4f", sensor.getResolution())).append("<br/>");
        sb.append("<b>Power:</b> ").append(sensor.getPower()).append(" mA<br/>");
        sb.append("<b>Max Range:</b> ").append(sensor.getMaximumRange()).append("<br/>");
        sb.append("<b>Min Delay:</b> ").append(sensor.getMinDelay()).append(" µs");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sb.append("<br/><b>Id:</b> ").append(sensor.getId());
        }
        
        return sb.toString();
    }
}
