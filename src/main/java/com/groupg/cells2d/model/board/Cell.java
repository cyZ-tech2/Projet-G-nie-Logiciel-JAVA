package com.groupg.cells2d.model.board;
import java.awt.Point;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.enums.TimeStep;

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
public class Cell{
    private String cellId;
    private int population;
    private CellState state; //A revoir car peut etre incoherent avec seirDATA
    private SEIRData seirData;
    private Point coordinates;

    public Cell(String cellId, int population, Point coordinates) {
        this.cellId = cellId;
        this.population = population;
        this.coordinates = coordinates;
        this.state = CellState.HEALTHY;
        this.seirData = new SEIRData();
    }

    public Cell(String cellId, int population, CellState state, SEIRData seirData, Point coordinates) {
        this.cellId = cellId;
        this.population = population;
        this.state = state;
        this.seirData = seirData;
        this.coordinates = coordinates;
    }
    /**
     * Evolves the cell epidemic state
     * using simulation parameters.
     *
     * @param params simulation parameters
     */
    public void evolve(SimulationParams params) { // A revoir puisque propagation.apply fait deja l'evolution 
        seirData.computeNextStep(params);
    }

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
     *
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
        return "Cell{" + "cellId='" + cellId + '\'' + ", population=" + population + ", state=" + state + ", coordinates=" + coordinates + '}';
    }

    /* Getters and setters */

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

    public SEIRData getSeirData() {
        return seirData;
    }

    public void setSeirData(SEIRData seirData) {
        this.seirData = seirData;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Point coordinates) {
        this.coordinates = coordinates;
    }



}

