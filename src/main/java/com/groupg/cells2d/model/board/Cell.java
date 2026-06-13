package com.groupg.cells2d.model.board;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.exceptions.InvalidPopulationException;
import java.io.Serializable;
/**
 * Represents a simulation cell inside the epidemic grid.
 *
 * <p>
 * A cell models a local geographical area containing
 * a population and epidemic SEIR data.
 * </p>
 *
 * <p>
 * Cells evolve over time according to epidemic
 * parameters and may interact with neighboring cells.
 * </p>
 */
public class Cell implements Serializable {
    private String cellId;
    private int population;
    private CellState state; // must remain consistent with seirData values
    private SEIRData seirData;
    private int row;
    private int col; 
    private boolean insideParis;
    private String districtId;
    private String districtName;
    private boolean isAlive;
    private static final long serialVersionUID = 1L;

    /**
     * Creates a healthy cell with a fully susceptible population.
     * @param cellId     unique cell identifier
     * @param population initial population (must be ≥ 0)
     * @param row        row index in the grid
     * @param col        column index in the grid
     */
    public Cell(String cellId, int population, int row, int col) {
        if(population < 0) {
            throw new InvalidPopulationException(population);
        }
        this.cellId = cellId;
        this.population = population;
        this.row = row ;
        this.col = col;
        this.state = CellState.HEALTHY;
        this.seirData = new SEIRData(population);
        this.isAlive = true;
    }

    /**
     * Creates a cell with an explicit epidemic state and SEIR data.
     * Used when restoring a cell from a saved snapshot.
     * @param cellId     unique cell identifier
     * @param population total population (must be ≥ 0)
     * @param state      initial epidemic state
     * @param seirData   initial SEIR compartment values
     * @param row        row index in the grid
     * @param col        column index in the grid
     */
    public Cell(String cellId, int population, CellState state, SEIRData seirData, int row , int col) {
        if(population < 0) {
            throw new InvalidPopulationException(population);
        }
        this.cellId = cellId;
        this.population = population;
        this.state = state;
        this.seirData = seirData;
        this.col = col;
        this.row = row;
    }
        /**
     * Evolves the internal epidemic state of the cell
     * without considering neighboring cells.
     * @param beta transmission rate
     * @param sigma incubation rate
     * @param gamma recovery rate
     * @param mortalityRate mortality rate
     */
    /**public void evolve(TimeStep step){
        seirData.computeNextStep();
    }
    */
    public void addCase(PatientCase c) { // simplified rule: any new patient case marks the cell as infected
        this.state = CellState.INFECTED;
    }
    /**
     * Computes the local infection rate.
     *
     * @return infection rate between 0 and 1
     */
    public double getInfectionRate() {

        if (population == 0)
            return 0;

        return seirData.getInfected() / population;
    }
    /**
     * Computes the mortality rate.
     *
     * @return mortality rate
     */
    public double getMortalityRate() {

        if (population == 0)
            return 0;

        return seirData.getDead() / population;
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        return "Cell{" + "cellId='" + cellId + '\'' + ", population=" + population + ", state=" + state + ", coordinates=" + "(" +col + "," + row + ")" + '}';
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    /** Returns the row index of this cell in the grid. */
    public int getRow() {
        return row;
    }
    
    /** Sets the row index of this cell in the grid. */
    public void setRow (int row) {
        this.row = row;
    }

    /** Returns the column index of this cell in the grid. */
    public int getCol() {
        return col;
    }
    
    /** Sets the column index of this cell in the grid. */
    public void setCol(int col) {
        this.col = col;
    }

    /** Returns the unique identifier of this cell. */
    public String getCellId() {
        return cellId;
    }

    /** Sets the unique identifier of this cell. */
    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    /** Returns the total population of this cell. */
    public int getPopulation() {
        return population;
    }

    /** Sets the total population of this cell. */
    public void setPopulation(int population) {
        this.population = population;
    }

    /** Returns the current epidemic state of this cell. */
    public CellState getState() {
        return state;
    }

    /** Sets the epidemic state of this cell. */
    public void setState(CellState state) {
        this.state = state;
    }

    /** Returns true if the cell still has a living population (more than 100 survivors). */
    public boolean isAlive() {
        return isAlive;
    }
    /** Explicitly marks this cell as alive or dead. */
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    /** Returns the SEIR compartment data for this cell. */
    public SEIRData getSeirData() {
        return seirData;
    }

    /** Replaces the SEIR data of this cell. */
    public void setSeirData(SEIRData seirData) {
        this.seirData = seirData;
    }

    /** Returns true if this cell falls within the Paris boundary mask. */
    public boolean isInsideParis() {
        return insideParis;
    }

    /** Marks whether this cell is inside the Paris boundary mask. */
    public void setInsideParis( boolean insideParis){
        this.insideParis = insideParis;
    }
    /** Returns the arrondissement identifier assigned to this cell. */
    public String getDistrictId() {
        return districtId;
    }

    /** Sets the arrondissement identifier for this cell. */
    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    /** Returns the human-readable arrondissement name of this cell. */
    public String getDistrictName() {
        return districtName;
    }

    /** Sets the human-readable arrondissement name for this cell. */
    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    /**
     * Updates the cell state based on the infection rate.
     * @param infectionRate ratio of infected over total population
     */
    public void updateState(double infectionRate){
        if(infectionRate < 0.10){
            if(seirData.getRecovered()>this.population/2){
                setState(CellState.RECOVERED);
            }
            else {
                setState(CellState.HEALTHY);
            }
        } else if(infectionRate < 0.30){
            setState(CellState.PARTIAL);
        } else if(infectionRate < 0.60){
            setState(CellState.INFECTED);
        } else {
            setState(CellState.CRITICAL);
        }
        if(this.population-this.seirData.getDead()<= 100){
            this.isAlive = false;
        }
        else{
            this.isAlive = true;
        }
    }
}

