package com.groupg.cells2d.model.board;
import java.awt.Point;
import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.enums.TimeStep;

/**
 * 
 */
public class Cell{
    private String cellId;
    private int population;
    private CellState state;
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

    public void evolve(TimeStep step) {
        switch (state) {
            case HEALTHY:
                state = CellState.PARTIAL;
                break;
            case PARTIAL:
                state = CellState.INFECTED;
                break;
            case INFECTED:
                state = CellState.CRITICAL;
                break;
            case CRITICAL:
                //Already critical, no changes for now 
                state = CellState.CRITICAL;
                break;
        }
    }

    public void addCase(PatientCase c) { //Simple version : if a patient is added , the cell becomes infected 
        this.state = CellState.INFECTED;
    }
    /**
     * Computes the infection rate of the cell
     * according to its current health state.
     *
     * HEALTHY   -> 0.0
     * PARTIAL   -> 0.3
     * INFECTED  -> 0.7
     * CRITICAL  -> 1.0
     *
     * @return infection rate between 0 and 1
     */
    public double getInfectionRate() {
        return switch (state) {
            case HEALTHY -> 0.0;
            case PARTIAL -> 0.3;
            case INFECTED -> 0.7;
            case CRITICAL -> 1.0;
        };
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

