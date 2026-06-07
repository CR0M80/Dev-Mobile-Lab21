package ensa.ma.sensors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LineChartView extends View {

    private final List<Float> values = new ArrayList<>();
    private final int maxPoints = 100;

    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path linePath = new Path();
    private final Path fillPath = new Path();

    public LineChartView(Context context) {
        this(context, null);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        axisPaint.setColor(Color.parseColor("#BDBDBD"));
        axisPaint.setStrokeWidth(2);

        linePaint.setColor(Color.parseColor("#6200EE"));
        linePaint.setStrokeWidth(6);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND);
        linePaint.setStrokeCap(Paint.Cap.ROUND);

        fillPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.parseColor("#757575"));
        textPaint.setTextSize(32);
    }

    public void addValue(float value) {
        if (values.size() >= maxPoints) {
            values.remove(0);
        }
        values.add(value);
        postInvalidateOnAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float padding = 60f;

        // Draw background grid or axes
        canvas.drawLine(padding, height - padding, width - padding, height - padding, axisPaint);
        canvas.drawLine(padding, padding, padding, height - padding, axisPaint);

        if (values.size() < 2) {
            canvas.drawText("Waiting for sensor data...", width / 2f - 150, height / 2f, textPaint);
            return;
        }

        float min = Float.MAX_VALUE;
        float max = -Float.MAX_VALUE;

        for (float value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        if (max == min) {
            max = min + 1;
        }

        float range = max - min;
        float chartWidth = width - 2 * padding;
        float chartHeight = height - 2 * padding;

        linePath.reset();
        fillPath.reset();

        for (int i = 0; i < values.size(); i++) {
            float x = padding + i * (chartWidth / (maxPoints - 1));
            float normalizedValue = (values.get(i) - min) / range;
            float y = height - padding - normalizedValue * chartHeight;

            if (i == 0) {
                linePath.moveTo(x, y);
                fillPath.moveTo(x, height - padding);
                fillPath.lineTo(x, y);
            } else {
                linePath.lineTo(x, y);
                fillPath.lineTo(x, y);
            }
            
            if (i == values.size() - 1) {
                fillPath.lineTo(x, height - padding);
                fillPath.close();
            }
        }

        // Gradient for fill
        fillPaint.setShader(new LinearGradient(0, padding, 0, height - padding,
                Color.parseColor("#446200EE"), Color.TRANSPARENT, Shader.TileMode.CLAMP));

        canvas.drawPath(fillPath, fillPaint);
        canvas.drawPath(linePath, linePaint);

        // Draw current value, min, max
        String status = String.format("Current: %.2f | Min: %.2f | Max: %.2f", values.get(values.size()-1), min, max);
        canvas.drawText(status, padding + 10, padding - 10, textPaint);
    }
}
