package com.example.poolaimassistant;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes direct and one-cushion bank trajectories from target to each pocket.
 */
public class ShotCalculator {

    public static class ShotPath {
        public final PointF start;
        public final PointF reflection; // null for direct path
        public final PointF end;
        public final boolean bank;

        public ShotPath(PointF start, PointF reflection, PointF end, boolean bank) {
            this.start = start;
            this.reflection = reflection;
            this.end = end;
            this.bank = bank;
        }
    }

    public List<ShotPath> calculateDirect(PointF ball, List<PointF> pockets) {
        List<ShotPath> output = new ArrayList<>();
        for (PointF pocket : pockets) {
            output.add(new ShotPath(new PointF(ball.x, ball.y), null,
                    new PointF(pocket.x, pocket.y), false));
        }
        return output;
    }

    /**
     * Uses mirrored-pocket method for one-cushion bank shots.
     */
    public List<ShotPath> calculateBank(
            PointF ball,
            List<PointF> pockets,
            float left,
            float top,
            float right,
            float bottom
    ) {
        List<ShotPath> output = new ArrayList<>();
        for (PointF pocket : pockets) {
            addBankForVerticalCushion(ball, pocket, left, true, top, bottom, output);
            addBankForVerticalCushion(ball, pocket, right, false, top, bottom, output);
            addBankForHorizontalCushion(ball, pocket, top, true, left, right, output);
            addBankForHorizontalCushion(ball, pocket, bottom, false, left, right, output);
        }
        return output;
    }

    private void addBankForVerticalCushion(
            PointF ball,
            PointF pocket,
            float cushionX,
            boolean isLeft,
            float tableTop,
            float tableBottom,
            List<ShotPath> out
    ) {
        float mirroredX = isLeft ? (2f * cushionX - pocket.x) : (2f * cushionX - pocket.x);
        PointF mirroredPocket = new PointF(mirroredX, pocket.y);
        PointF reflection = lineIntersectionWithVertical(ball, mirroredPocket, cushionX);
        if (reflection != null && reflection.y >= tableTop && reflection.y <= tableBottom) {
            out.add(new ShotPath(new PointF(ball.x, ball.y), reflection, new PointF(pocket.x, pocket.y), true));
        }
    }

    private void addBankForHorizontalCushion(
            PointF ball,
            PointF pocket,
            float cushionY,
            boolean isTop,
            float tableLeft,
            float tableRight,
            List<ShotPath> out
    ) {
        float mirroredY = isTop ? (2f * cushionY - pocket.y) : (2f * cushionY - pocket.y);
        PointF mirroredPocket = new PointF(pocket.x, mirroredY);
        PointF reflection = lineIntersectionWithHorizontal(ball, mirroredPocket, cushionY);
        if (reflection != null && reflection.x >= tableLeft && reflection.x <= tableRight) {
            out.add(new ShotPath(new PointF(ball.x, ball.y), reflection, new PointF(pocket.x, pocket.y), true));
        }
    }

    private PointF lineIntersectionWithVertical(PointF a, PointF b, float xVertical) {
        float dx = b.x - a.x;
        if (Math.abs(dx) < 0.001f) {
            return null;
        }
        float t = (xVertical - a.x) / dx;
        if (t <= 0f || t >= 1f) {
            return null;
        }
        float y = a.y + t * (b.y - a.y);
        return new PointF(xVertical, y);
    }

    private PointF lineIntersectionWithHorizontal(PointF a, PointF b, float yHorizontal) {
        float dy = b.y - a.y;
        if (Math.abs(dy) < 0.001f) {
            return null;
        }
        float t = (yHorizontal - a.y) / dy;
        if (t <= 0f || t >= 1f) {
            return null;
        }
        float x = a.x + t * (b.x - a.x);
        return new PointF(x, yHorizontal);
    }
}
