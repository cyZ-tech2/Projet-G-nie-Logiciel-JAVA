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
    private CellState state; //A revoir car peut etre incoherent avec seirDATA
    private SEIRData seirData;
    private int row;
    private int col; 
    private boolean insideParis;
    private String districtId;
    private String districtName;
    private boolean isAlive;
    private static final long serialVersionUID = 1L;

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
    public void addCase(PatientCase c) { //Simple version : if a patient is added , the cell becomes infected 
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
     *s
     * @return mortality rate
     */
    public double getMortalityRate() {

        if (population == 0)
            return 0;

        return seirData.getDead() / population;
    }

    /* Override */

    @Override
    public String toString() {
        return "Cell{" + "cellId='" + cellId + '\'' + ", population=" + population + ", state=" + state + ", coordinates=" + "(" +col + "," + row + ")" + '}';
    }

    /* Getters and setters */

    public int getRow() {
        return row;
    }
    
    public void setRow (int row) {
        this.row = row;
    }

     public int getCol() {
        return col;
    }
    
    public void setCol(int col) {
        this.col = col;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public boolean isAlive() {
        return isAlive;
    }
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public SEIRData getSeirData() {
        return seirData;
    }

    public void setSeirData(SEIRData seirData) {
        this.seirData = seirData;
    }

    public boolean isInsideParis() {
        return insideParis;
    }

    public void setInsideParis( boolean insideParis){
        this.insideParis = insideParis;
    }
     public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

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

