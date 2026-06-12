package com.groupg.cells2d.stats;

/**
 * Statistics snapshot for one district at one simulation step.
 */
public class DistrictSnapshot {

    private final int step;
    private final String districtId;
    private final String districtName;

    private int totalCells;
    private int healthyCells;
    private int partialCells;
    private int infectedCells;
    private int criticalCells;

    private double susceptiblePopulation;
    private double exposedPopulation;
    private double infectedPopulation;
    private double recoveredPopulation;
    private double deadPopulation;

    public DistrictSnapshot(int step, String districtId, String districtName) {
        this.step = step;
        this.districtId = districtId;
        this.districtName = districtName;
    }

    public void addHealthyCell() {
        healthyCells++;
        totalCells++;
    }

    public void addPartialCell() {
        partialCells++;
        totalCells++;
    }

    public void addInfectedCell() {
        infectedCells++;
        totalCells++;
    }

    public void addCriticalCell() {
        criticalCells++;
        totalCells++;
    }

    public void addPopulation(
            double susceptible,
            double exposed,
            double infected,
            double recovered,
            double dead
    ) {
        susceptiblePopulation += susceptible;
        exposedPopulation += exposed;
        infectedPopulation += infected;
        recoveredPopulation += recovered;
        deadPopulation += dead;
    }

    public int getStep() {
        return step;
    }

    public String getDistrictId() {
        return districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public int getTotalCells() {
        return totalCells;
    }

    public int getHealthyCells() {
        return healthyCells;
    }

    public int getPartialCells() {
        return partialCells;
    }

    public int getInfectedCells() {
        return infectedCells;
    }

    public int getCriticalCells() {
        return criticalCells;
    }

    public double getSusceptiblePopulation() {
        return susceptiblePopulation;
    }

    public double getExposedPopulation() {
        return exposedPopulation;
    }

    public double getInfectedPopulation() {
        return infectedPopulation;
    }

    public double getRecoveredPopulation() {
        return recoveredPopulation;
    }

    public double getDeadPopulation() {
        return deadPopulation;
    }

    public double getTotalPopulation() {
        return susceptiblePopulation
                + exposedPopulation
                + infectedPopulation
                + recoveredPopulation
                + deadPopulation;
    }

    public double getInfectedCellPercentage() {
        return totalCells == 0 ? 0 : infectedCells * 100.0 / totalCells;
    }

    public double getDeadPopulationPercentage() {
        double totalPopulation = getTotalPopulation();
        return totalPopulation == 0 ? 0 : deadPopulation * 100.0 / totalPopulation;
    }
}