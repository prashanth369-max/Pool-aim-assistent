package com.example.poolaimassistant;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

/**
 * Draws path primitives with independent styles for direct and bank shots.
 */
public class PathRenderer {

    private final Paint directPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bankPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint reflectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PathRenderer() {
        directPaint.setStyle(Paint.Style.STROKE);
        directPaint.setColor(Color.GREEN);

        bankPaint.setStyle(Paint.Style.STROKE);
        bankPaint.setColor(Color.YELLOW);

        reflectionPaint.setStyle(Paint.Style.FILL);
        reflectionPaint.setColor(Color.WHITE);
    }

    public void setThickness(float thicknessPx) {
        directPaint.setStrokeWidth(thicknessPx);
        bankPaint.setStrokeWidth(thicknessPx);
    }

    public void draw(Canvas canvas, List<ShotCalculator.ShotPath> paths) {
        for (ShotCalculator.ShotPath path : paths) {
            if (!path.bank) {
                canvas.drawLine(path.start.x, path.start.y, path.end.x, path.end.y, directPaint);
            } else if (path.reflection != null) {
                canvas.drawLine(path.start.x, path.start.y, path.reflection.x, path.reflection.y, bankPaint);
                canvas.drawLine(path.reflection.x, path.reflection.y, path.end.x, path.end.y, bankPaint);
                canvas.drawCircle(path.reflection.x, path.reflection.y, 5f, reflectionPaint);
            }
        }
    }
}
