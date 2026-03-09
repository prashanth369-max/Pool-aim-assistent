package com.example.poolaimassistant;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates table model + shot calculator and returns current trajectory set.
 */
public class GeometryEngine {

    private final PocketModel pocketModel;
    private final ShotCalculator shotCalculator;

    public GeometryEngine(PocketModel pocketModel, ShotCalculator shotCalculator) {
        this.pocketModel = pocketModel;
        this.shotCalculator = shotCalculator;
    }

    public List<ShotCalculator.ShotPath> computePaths(
            PointF ball,
            float tableLeft,
            float tableTop,
            float tableRight,
            float tableBottom,
            boolean includeDirect,
            boolean includeBank
    ) {
        pocketModel.updateForTable(tableLeft, tableTop, tableRight, tableBottom);
        List<ShotCalculator.ShotPath> paths = new ArrayList<>();

        if (includeDirect) {
            paths.addAll(shotCalculator.calculateDirect(ball, pocketModel.getPockets()));
        }
        if (includeBank) {
            paths.addAll(shotCalculator.calculateBank(ball, pocketModel.getPockets(),
                    tableLeft, tableTop, tableRight, tableBottom));
        }
        return paths;
    }
}
