package com.groupg.cells2d.stats;

/**
 * Immutable snapshot of global simulation statistics captured at a given step.
 * Used to power the statistics charts and the step history.
 */

public class Snapshot {
    private final int step;
    private final int totalCells;
    private final int healthyCells;
    private final int infectedCells;
    private final int criticalCells;
    private final int partialCells;
    private final int recoveredCells;

    private final double susceptiblePopulation;
    private final double exposedPopulation;
    private final double infectedPopulation;
    private final double recoveredPopulation;
    private final double deadPopulation;
    private final double totalPopulation;

    /**
     * Creates a new global statistics snapshot.
     * All counts and population values are supplied by {@link Statistics#compute}.
     */
    public Snapshot(
            int step, int totalCells, int healthyCells,
            int partialCells, int infectedCells, int criticalCells,
            double susceptiblePopulation , double exposedPopulation,
            double infectedPopulation, double recoveredPopulation,
            double deadPopulation, double totalPopulation,int recoveredCells) {
        this.step = step;
        this.totalCells = totalCells;
        this.healthyCells = healthyCells;
        this.partialCells = partialCells;
        this.infectedCells = infectedCells;
        this.criticalCells = criticalCells;
        this.recoveredCells = recoveredCells;
        this.susceptiblePopulation = susceptiblePopulation;
        this.exposedPopulation = exposedPopulation;
        this.infectedPopulation = infectedPopulation;
        this.recoveredPopulation = recoveredPopulation;
        this.deadPopulation = deadPopulation;
        this.totalPopulation = totalPopulation;
    }


    /** Returns the simulation step at which this snapshot was taken. */
    public int getStep(){
        return step;
    }

    /** Returns the total number of Paris cells at this step. */
    public int getTotalCells(){
        return totalCells;
    }
    /** Returns the number of healthy cells. */
    public int getHealthyCells(){
        return healthyCells;
    }
    /** Returns the number of partially infected cells. */
    public int getPartialCells(){
        return partialCells;
    }
    /** Returns the number of infected cells. */
    public int getInfectedCells(){
        return infectedCells;
    }
    /** Returns the number of critically infected cells. */
    public int getCriticalCells(){
        return criticalCells;
    }
    /** Returns the number of recovered cells. */
    public int getRecoveredCells(){return recoveredCells;}
    /** Returns the percentage of infected cells over total cells. */
    public double getInfectedCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (infectedCells * 100.0) / totalCells;
    }

    /** Returns the percentage of critically infected cells over total cells. */
    public double getCriticalCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (criticalCells * 100.0) / totalCells;
    }

    /** Returns the percentage of healthy cells over total cells. */
    public double getHealthyCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (healthyCells * 100.0) / totalCells;
    }

    /** Returns the percentage of partially infected cells over total cells. */
    public double getPartialCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (partialCells * 100.0) / totalCells;
    }
    /** Returns the percentage of recovered cells over total cells. */
    public double getRecoveredCellPercentage() {
        if (totalCells == 0) {
            return 0;
        }
        return (recoveredCells * 100.0) / totalCells;
    }

    /** Returns the total susceptible population across all cells. */
    public double getSusceptiblePopulation() {
    return susceptiblePopulation;
    }

    /** Returns the total exposed population across all cells. */
    public double getExposedPopulation() {
        return exposedPopulation;
    }

    /** Returns the total infectious population across all cells. */
    public double getInfectedPopulation() {
        return infectedPopulation;
    }

    /** Returns the total recovered population across all cells. */
    public double getRecoveredPopulation() {
        return recoveredPopulation;
    }

    /** Returns the cumulative number of deaths across all cells. */
    public double getDeadPopulation() {
        return deadPopulation;
    }

    /** Returns the total living population (S + E + I + R) across all cells. */
    public double getTotalPopulation() {
        return totalPopulation;
    }

    public double getPopulationRatio() { return deadPopulation/totalPopulation;}



    
}
