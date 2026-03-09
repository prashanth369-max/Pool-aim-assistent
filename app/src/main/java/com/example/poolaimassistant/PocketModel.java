package com.example.poolaimassistant;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the six pocket coordinates for a rectangular table projected to screen coordinates.
 */
public class PocketModel {

    private final List<PointF> pockets = new ArrayList<>();

    public void updateForTable(float left, float top, float right, float bottom) {
        pockets.clear();
        float centerX = (left + right) / 2f;

        pockets.add(new PointF(left, top));
        pockets.add(new PointF(centerX, top));
        pockets.add(new PointF(right, top));
        pockets.add(new PointF(left, bottom));
        pockets.add(new PointF(centerX, bottom));
        pockets.add(new PointF(right, bottom));
    }

    public List<PointF> getPockets() {
        return Collections.unmodifiableList(pockets);
    }
}
