package com.example.poolaimassistant;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Full-screen transparent drawing layer for target + trajectory guides.
 */
public class OverlayView extends View {

    private final TargetCircle targetCircle;
    private final TargetController targetController;
    private final GeometryEngine geometryEngine;
    private final PathRenderer pathRenderer;
    private final Paint targetPaint;
    private final Paint targetStrokePaint;
    private final Paint tableBoundsPaint;

    private boolean showDirect = true;
    private boolean showBank = true;
    private boolean overlayVisible = true;
    private final List<ShotCalculator.ShotPath> cachedPaths = new ArrayList<>();

    public OverlayView(Context context) {
        super(context);
        targetCircle = new TargetCircle();
        targetController = new TargetController(targetCircle);
        geometryEngine = new GeometryEngine(new PocketModel(), new ShotCalculator());
        pathRenderer = new PathRenderer();
        pathRenderer.setThickness(5f);

        targetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetPaint.setStyle(Paint.Style.FILL);
        targetPaint.setColor(Color.argb(80, 0, 170, 255));

        targetStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        targetStrokePaint.setStyle(Paint.Style.STROKE);
        targetStrokePaint.setStrokeWidth(4f);
        targetStrokePaint.setColor(Color.CYAN);

        tableBoundsPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tableBoundsPaint.setStyle(Paint.Style.STROKE);
        tableBoundsPaint.setColor(Color.argb(90, 255, 255, 255));
        tableBoundsPaint.setStrokeWidth(2f);

        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!overlayVisible) {
            return;
        }

        float margin = 24f;
        float left = margin;
        float top = margin;
        float right = getWidth() - margin;
        float bottom = getHeight() - margin;

        canvas.drawRect(left, top, right, bottom, tableBoundsPaint);

        if (targetCircle.isPlaced()) {
            cachedPaths.clear();
            cachedPaths.addAll(geometryEngine.computePaths(
                    targetCircle.getCenter(), left, top, right, bottom, showDirect, showBank));
            pathRenderer.draw(canvas, cachedPaths);
        }

        canvas.drawCircle(targetCircle.getCenter().x, targetCircle.getCenter().y,
                targetCircle.getRadius(), targetPaint);
        canvas.drawCircle(targetCircle.getCenter().x, targetCircle.getCenter().y,
                targetCircle.getRadius(), targetStrokePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = targetController.onTouch(event);
        if (consumed) {
            invalidate();
        }
        // Returning false when not touching the target keeps most of the overlay touch-transparent.
        return consumed;
    }

    public void setShowDirect(boolean showDirect) {
        this.showDirect = showDirect;
        invalidate();
    }

    public void setShowBank(boolean showBank) {
        this.showBank = showBank;
        invalidate();
    }

    public void setThickness(float px) {
        pathRenderer.setThickness(px);
        invalidate();
    }

    public void clearTarget() {
        targetCircle.setPlaced(false);
        invalidate();
    }

    public void setOverlayVisible(boolean visible) {
        this.overlayVisible = visible;
        invalidate();
    }
}
