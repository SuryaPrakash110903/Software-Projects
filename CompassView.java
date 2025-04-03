 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-feature android:name="android.hardware.sensor.compass"/>
   package com.example.mycompass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class CompassView extends View {
    private float azimuth = 0;
    private Paint paint;
    private Paint textPaint;
    private Paint cardinalPaint; // Paint for cardinal directions

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#FFFF00"));
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);

        cardinalPaint = new Paint(); // Initialize cardinalPaint
        cardinalPaint.setAntiAlias(true);
        cardinalPaint.setColor(Color.RED);
        cardinalPaint.setTextSize(48); // Larger size for cardinal directions
        cardinalPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(centerX, centerY) - 20;
        int innerRadius = radius - 30;

        // Paint for translucent glass effect
        Paint glassPaint = new Paint();
        glassPaint.setAntiAlias(true);
        glassPaint.setStyle(Paint.Style.FILL);
        glassPaint.setColor(Color.argb(100, 200, 200, 200)); // Light gray with transparency

        // Draw the translucent glass effect inside the compass
        canvas.drawCircle(centerX, centerY, radius, glassPaint);

        // Draw compass border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.LTGRAY);
        canvas.drawCircle(centerX, centerY, radius, paint);

        // Draw compass needle
        paint.setStyle(Paint.Style.FILL);
        float stopX = centerX + (float) (radius * Math.sin(Math.toRadians(-azimuth)));
        float stopY = centerY - (float) (radius * Math.cos(Math.toRadians(-azimuth)));
        canvas.drawLine(centerX, centerY, stopX, stopY, paint);

        // Draw degree markings
        drawDegreeMarkings(canvas, centerX, centerY, innerRadius);
    }


    private void drawDegreeMarkings(Canvas canvas, int centerX, int centerY, int radius) {
        int[] degrees = {30, 60,  120, 150, 210, 240, 300, 330};
        String[] degreeStrings = {"30", "60", "120", "150",  "210", "240", "300", "330"};

        for (int i = 0; i < degrees.length; i++) {
            float angle = (float) Math.toRadians(degrees[i] - 90);
            float x = centerX + (float) (radius * Math.cos(angle));
            float y = centerY + (float) (radius * Math.sin(angle));

            Rect bounds = new Rect();
            textPaint.getTextBounds(degreeStrings[i], 0, degreeStrings[i].length(), bounds);
            y += bounds.height() / 2f;

            canvas.drawText(degreeStrings[i], x, y, textPaint);
        }

        // Draw cardinal directions (using cardinalPaint)
        drawCardinalDirection(canvas, centerX, centerY, radius, 0, "N");
        drawCardinalDirection(canvas, centerX, centerY, radius, 90, "E");
        drawCardinalDirection(canvas, centerX, centerY, radius, 180, "S");
        drawCardinalDirection(canvas, centerX, centerY, radius, 270, "W");
    }

    private void drawCardinalDirection(Canvas canvas, int centerX, int centerY, int radius, int degree, String text) {
        float angle = (float) Math.toRadians(degree - 90);
        float x = centerX + (float) (radius * Math.cos(angle));
        float y = centerY + (float) (radius * Math.sin(angle));

        Rect bounds = new Rect();
        cardinalPaint.getTextBounds(text, 0, text.length(), bounds);
        y += bounds.height() / 2f;

        canvas.drawText(text, x, y, cardinalPaint); // Use cardinalPaint
    }
}
