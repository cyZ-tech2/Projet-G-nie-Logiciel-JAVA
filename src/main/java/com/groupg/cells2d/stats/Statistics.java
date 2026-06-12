package com.groupg.cells2d.stats;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import java.util.HashMap;
import java.util.Map;

/**
 * les données
 */

public class Statistics {
    private Statistics(){
        //
    }

    

    public static Snapshot compute(Grid grid, int step){
        int healthy = 0;
        int partial = 0;
        int infected = 0;
        int critical = 0;
        int recovered = 0;
        int total = 0;

        double susceptiblePopulation = 0;
        double exposedPopulation = 0;
        double infectedPopulation = 0;
        double recoveredPopulation = 0;
        double deadPopulation = 0;

        for (int row = 0; row < grid.getRows(); row++){
            for(int col =0; col < grid.getCols(); col ++ ){
                Cell cell = grid.getCell(row, col);
                
                if (cell == null){
                    continue;
                }

                SEIRData data = cell.getSeirData();

                susceptiblePopulation += data.getSusceptible();
                exposedPopulation += data.getExposed();
                infectedPopulation += data.getInfected();
                recoveredPopulation += data.getRecovered();
                deadPopulation += data.getDead();

                total ++;

                switch (cell.getState()) {
                    case HEALTHY:
                        healthy++;
                        break;

                    case PARTIAL:
                        partial++;
                        break;

                    case INFECTED:
                        infected++;
                        break;

                    case CRITICAL:
                        critical++;
                        break;
                    case RECOVERED:
                        recovered++;
                        break;
                }
            }
        }
        double totalPopulation =
                susceptiblePopulation
                + exposedPopulation
                + infectedPopulation
                + recoveredPopulation
                + deadPopulation;

        return new Snapshot(step,total,healthy,partial,infected,critical,
            susceptiblePopulation,exposedPopulation,infectedPopulation,
                recoveredPopulation,deadPopulation,totalPopulation,recovered );
    }

    /**
     * stats of each district 
     * The grid is traversed cell by cell and cells belongs to the 
     * same district are grouped in a DistrictCnapshot
     * @param grid simulation grid 
     * @param step 
     * @return a map containing on DistrictSnapshot per district
     */
    public static Map<String, DistrictSnapshot> computeByDistrict(Grid grid, int step) {
        Map<String, DistrictSnapshot> districtStats = new HashMap<>();

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Cell cell = grid.getCell(row, col);

                if (cell == null || !cell.isInsideParis()) {
                    continue;
                }

                String districtId = cell.getDistrictId();
                String districtName = cell.getDistrictName();

                districtStats.putIfAbsent(
                        districtId,
                        new DistrictSnapshot(step, districtId, districtName)
                );

                DistrictSnapshot snapshot = districtStats.get(districtId);

                switch (cell.getState()) {
                    case HEALTHY:
                        snapshot.addHealthyCell();
                        break;
                    case PARTIAL:
                        snapshot.addPartialCell();
                        break;
                    case INFECTED:
                        snapshot.addInfectedCell();
                        break;
                    case CRITICAL:
                        snapshot.addCriticalCell();
                        break;
                    case RECOVERED:
                        snapshot.addRecoveredCell();
                        break;
                }

                SEIRData data = cell.getSeirData();

                snapshot.addPopulation(
                        data.getSusceptible(),
                        data.getExposed(),
                        data.getInfected(),
                        data.getRecovered(),
                        data.getDead()
                );
            }
        }

        return districtStats;
    }
}

