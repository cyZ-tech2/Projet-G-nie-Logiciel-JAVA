package com.groupg.cells2d.stats;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;

import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private Statistics() {}

    public static Snapshot compute(Grid grid, int step) {
        int healthy = 0, partial = 0, infected = 0, critical = 0, recovered = 0, total = 0;
        double susceptiblePop = 0, exposedPop = 0, infectedPop = 0, recoveredPop = 0, deadPop = 0;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null) continue;

                SEIRData data = cell.getSeirData();
                susceptiblePop += data.getSusceptible();
                exposedPop     += data.getExposed();
                infectedPop    += data.getInfected();
                recoveredPop   += data.getRecovered();
                deadPop        += data.getDead();
                total++;

                switch (cell.getState()) {
                    case HEALTHY:   healthy++;   break;
                    case PARTIAL:   partial++;   break;
                    case INFECTED:  infected++;  break;
                    case CRITICAL:  critical++;  break;
                    case RECOVERED: recovered++; break;
                    default: break;
                }
            }
        }

        double totalPop = susceptiblePop + exposedPop + infectedPop + recoveredPop + deadPop;
        return new Snapshot(step, total, healthy, partial, infected, critical,
            susceptiblePop, exposedPop, infectedPop, recoveredPop, deadPop, totalPop, recovered);
    }

    public static Map<String, DistrictSnapshot> computeByDistrict(Grid grid, int step) {
        Map<String, DistrictSnapshot> districtStats = new HashMap<>();

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Cell cell = grid.getCell(row, col);
                if (cell == null || !cell.isInsideParis()) continue;

                String districtId = cell.getDistrictId();
                districtStats.putIfAbsent(districtId,
                    new DistrictSnapshot(step, districtId, cell.getDistrictName()));

                DistrictSnapshot snapshot = districtStats.get(districtId);

                switch (cell.getState()) {
                    case HEALTHY:   snapshot.addHealthyCell();   break;
                    case PARTIAL:   snapshot.addPartialCell();   break;
                    case INFECTED:  snapshot.addInfectedCell();  break;
                    case CRITICAL:  snapshot.addCriticalCell();  break;
                    case RECOVERED: snapshot.addRecoveredCell(); break;
                    default: break;
                }

                SEIRData data = cell.getSeirData();
                snapshot.addPopulation(data.getSusceptible(), data.getExposed(),
                    data.getInfected(), data.getRecovered(), data.getDead());
            }
        }
        return districtStats;
    }
}
