package ensa.ma.sensors.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CompassView extends View {

    private float azimuth = 0;
    private final Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint northPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint southPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path needlePath = new Path();

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        circlePaint.setColor(Color.parseColor("#EEEEEE"));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(8);

        northPaint.setColor(Color.parseColor("#F44336")); // Red
        northPaint.setStyle(Paint.Style.FILL);

        southPaint.setColor(Color.parseColor("#9E9E9E")); // Gray
        southPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 40;

        // Draw outer circle
        canvas.drawCircle(centerX, centerY, radius, circlePaint);

        // Draw N, S, E, W
        canvas.drawText("N", centerX, centerY - radius + 50, textPaint);
        canvas.drawText("S", centerX, centerY + radius - 20, textPaint);
        canvas.drawText("E", centerX + radius - 30, centerY + 15, textPaint);
        canvas.drawText("W", centerX - radius + 30, centerY + 15, textPaint);

        canvas.save();
        canvas.rotate(-azimuth, centerX, centerY);

        // Draw Needle
        needlePath.reset();
        needlePath.moveTo(centerX, centerY - radius + 70); // Tip
        needlePath.lineTo(centerX - 30, centerY);
        needlePath.lineTo(centerX + 30, centerY);
        needlePath.close();
        canvas.drawPath(needlePath, northPaint);

        needlePath.reset();
        needlePath.moveTo(centerX, centerY + radius - 70); // Bottom Tip
        needlePath.lineTo(centerX - 30, centerY);
        needlePath.lineTo(centerX + 30, centerY);
        needlePath.close();
        canvas.drawPath(needlePath, southPaint);

        canvas.restore();
    }
}
