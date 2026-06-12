package com.groupg.cells2d.stats;

/**
 * photo de chaque instance de la map pour pouvoir revenir en arrière et pour montrer la simu
 */

public class Snapshot {
    private final int step;
    private final int totalCells;
    private final int healthyCells;
    private final int infectedCells;
    private final int criticalCells;
    private final int partialCells;

    public Snapshot(
            int step,
            int totalCells,
            int healthyCells,
            int partialCells,
            int infectedCells,
            int criticalCells
    ) {
        this.step = step;
        this.totalCells = totalCells;
        this.healthyCells = healthyCells;
        this.partialCells = partialCells;
        this.infectedCells = infectedCells;
        this.criticalCells = criticalCells;
    }


    public int getStep(){
        return step;
    }

    public int getTotalCells(){
        return totalCells;
    }
    public int getHealthyCells(){
        return healthyCells;
    }
    public int getPartialCells(){
        return partialCells;
    }
    public int getInfectedCells(){
        return infectedCells;
    }
    public int getCriticalCells(){
        return criticalCells;
    }
    public double getInfectedCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (infectedCells * 100.0) / totalCells;
    }

    public double getCriticalCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (criticalCells * 100.0) / totalCells;
    }

    public double getHealthyCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (healthyCells * 100.0) / totalCells;
    }

    public double getPartialCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (partialCells * 100.0) / totalCells;
    }



    
}
