package com.groupg.cells2d.model.board;

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
    private int row;
    private int col;

    public Cell(String cellId, int population, int row, int col) {
        this.cellId = cellId;
        this.population = population;
        this.row = row;
        this.col = col;
        this.state = CellState.HEALTHY;
        this.seirData = new SEIRData();
    }

    public Cell(String cellId, int population, CellState state, SEIRData seirData, int row, int col) {
        this.cellId = cellId;
        this.population = population;
        this.state = state;
        this.seirData = seirData;
        this.row = row;
        this.col = col;
    }


    //à voir on laisse celle-ci pour les tests pas à pas ou on le supprime car on a updateState
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
        return "Cell{" + "cellId='" + cellId + '\'' + ", population=" + population + ", state=" + state + ", row=" + row + ", col="+col+"}";
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


    /**
     * returns the row of our cell
     * @return
     */
    public int getRow(){return row;}

    /**
     * returns the col of our cell
     * @return
     */
    public int getCol(){return col;}

    /**
     * Updates the cell state based on the infection rate.
     * @param infectionRate ratio of infected over total population
     */
    public void updateState(double infectionRate){
        if(infectionRate < 0.10){
            setState(CellState.HEALTHY);
        } else if(infectionRate < 0.30){
            setState(CellState.PARTIAL);
        } else if(infectionRate < 0.60){
            setState(CellState.INFECTED);
        } else {
            setState(CellState.CRITICAL);
        }
    }

}

