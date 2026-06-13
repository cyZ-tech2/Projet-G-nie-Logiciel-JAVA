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
    private int recoveredCells;

    private double susceptiblePopulation;
    private double exposedPopulation;
    private double infectedPopulation;
    private double recoveredPopulation;
    private double deadPopulation;

    /**
     * Creates an empty snapshot for the given district at the given step.
     * @param step         simulation step number
     * @param districtId   arrondissement identifier
     * @param districtName human-readable arrondissement name
     */
    public DistrictSnapshot(int step, String districtId, String districtName) {
        this.step = step;
        this.districtId = districtId;
        this.districtName = districtName;
    }

    /** Records one healthy cell in this district snapshot. */
    public void addHealthyCell() {
        healthyCells++;
        totalCells++;
    }

    /** Records one partially infected cell in this district snapshot. */
    public void addPartialCell() {
        partialCells++;
        totalCells++;
    }

    /** Records one infected cell in this district snapshot. */
    public void addInfectedCell() {
        infectedCells++;
        totalCells++;
    }

    /** Records one critically infected cell in this district snapshot. */
    public void addCriticalCell() {
        criticalCells++;
        totalCells++;
    }
    /** Records one recovered cell in this district snapshot. */
    public void addRecoveredCell() {
        recoveredCells++;
        totalCells++;
    }

    /**
     * Accumulates population counts from a single cell into this district snapshot.
     * @param susceptible number of susceptible individuals in the cell
     * @param exposed     number of exposed individuals
     * @param infected    number of infectious individuals
     * @param recovered   number of recovered individuals
     * @param dead        cumulative deaths
     */
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

    /** Returns the simulation step this snapshot covers. */
    public int getStep() {
        return step;
    }

    /** Returns the arrondissement identifier. */
    public String getDistrictId() {
        return districtId;
    }

    /** Returns the human-readable arrondissement name. */
    public String getDistrictName() {
        return districtName;
    }

    /** Returns the total number of cells in this district. */
    public int getTotalCells() {
        return totalCells;
    }

    /** Returns the number of healthy cells in this district. */
    public int getHealthyCells() {
        return healthyCells;
    }

    /** Returns the number of partially infected cells in this district. */
    public int getPartialCells() {
        return partialCells;
    }

    /** Returns the number of infected cells in this district. */
    public int getInfectedCells() {
        return infectedCells;
    }

    /** Returns the number of critically infected cells in this district. */
    public int getCriticalCells() {
        return criticalCells;
    }
    /** Returns the number of recovered cells in this district. */
    public int getRecoveredCells() {return recoveredCells;}

    /** Returns the total susceptible population in this district. */
    public double getSusceptiblePopulation() {
        return susceptiblePopulation;
    }

    /** Returns the total exposed population in this district. */
    public double getExposedPopulation() {
        return exposedPopulation;
    }

    /** Returns the total infectious population in this district. */
    public double getInfectedPopulation() {
        return infectedPopulation;
    }

    /** Returns the total recovered population in this district. */
    public double getRecoveredPopulation() {
        return recoveredPopulation;
    }

    /** Returns the cumulative number of deaths in this district. */
    public double getDeadPopulation() {
        return deadPopulation;
    }

    /** Returns the total living population (S + E + I + R + D) in this district. */
    public double getTotalPopulation() {
        return susceptiblePopulation
                + exposedPopulation
                + infectedPopulation
                + recoveredPopulation
                + deadPopulation;
    }

    /** Returns the percentage of infected cells relative to total district cells. */
    public double getInfectedCellPercentage() {
        return totalCells == 0 ? 0 : infectedCells * 100.0 / totalCells;
    }

    /** Returns the percentage of dead individuals relative to total district population. */
    public double getDeadPopulationPercentage() {
        double totalPopulation = getTotalPopulation();
        return totalPopulation == 0 ? 0 : deadPopulation * 100.0 / totalPopulation;
    }


}