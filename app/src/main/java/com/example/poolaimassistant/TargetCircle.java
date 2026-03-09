package com.example.poolaimassistant;

import android.graphics.PointF;

/**
 * State holder for the user-selected target ball representation.
 */
public class TargetCircle {
    private final PointF center = new PointF(500f, 900f);
    private float radius = 36f;
    private boolean placed = false;

    public PointF getCenter() {
        return center;
    }

    public void setCenter(float x, float y) {
        center.set(x, y);
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }
}
