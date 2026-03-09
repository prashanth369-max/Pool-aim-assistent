package com.example.poolaimassistant;

import android.view.MotionEvent;

/**
 * Handles drag gestures for the target circle only.
 */
public class TargetController {

    private final TargetCircle targetCircle;
    private boolean dragging = false;
    private float dx;
    private float dy;

    public TargetController(TargetCircle targetCircle) {
        this.targetCircle = targetCircle;
    }

    public boolean onTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (isOnTarget(x, y)) {
                    dragging = true;
                    dx = targetCircle.getCenter().x - x;
                    dy = targetCircle.getCenter().y - y;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (dragging) {
                    targetCircle.setCenter(x + dx, y + dy);
                    targetCircle.setPlaced(true);
                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                boolean wasDragging = dragging;
                dragging = false;
                return wasDragging;
            default:
                return false;
        }
    }

    private boolean isOnTarget(float x, float y) {
        float tx = targetCircle.getCenter().x;
        float ty = targetCircle.getCenter().y;
        float r = targetCircle.getRadius() * 1.5f;
        float dist2 = (x - tx) * (x - tx) + (y - ty) * (y - ty);
        return dist2 <= r * r;
    }
}
