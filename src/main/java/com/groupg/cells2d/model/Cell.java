package com.groupg.cells2d.model;

import java.awt.Point;

import com.groupg.cells2d.model.enums.CellState;

/**
 * Represents a cell in the simulation grid 
 * Each cell contains:
 * unique identifier cellID
 * population 
 * health state 
 * seirData
 * coordinates inside the grid  
 */
public class Cell {
    private String cellID;
    private int population;
    private CellState state;
    //private SEIRData seirData;
    private Point coordinates;

    /**
     * Creates a new cell
     * @param cellId
     * @param population
     * @param state
     * @param coordinates 
     * @param coordinates
     * @param seirdata
     * 
     */
    public Cell (String cellId, int population, CellState state, Point coordinates /*SEIRData seirData*/) {
            this.cellID = cellId;
            this.population = population;
            this.state = state;
            //this.seirData = seirData;
            this.coordinates = coordinates;
    }
    /**
     * Returns the cell identifier.
     *
     * @return cell ID
     */
    public String getCellId() {
        return cellID;
    }

    /**
     * Returns the population of the cell.
     *
     * @return population size
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Returns the current state of the cell.
     *
     * @return health state
     */
    public CellState getState() {
        return state;
    }
    /**
     *
     * @return health seirdata
     */
    /*public SEIRData getSeirData() { ---> A mettre après 
        return seirData;
    }
    */

    /**
     * Updates the state of the cell.
     *
     * @param state new health state
     */
    public void setState(CellState state) {
        this.state = state;
    }

    /**
     * Adds a patient case to the cell.
     *
     * @param c patient case to add
     */
    /*public void addCase(PatientCase c) {

        // Simple example:
        // a new patient case infects the cell ----> A mettre après 

        this.state = CellState.INFECTED;
    }*/

    /**
     * Computes the infection rate of the cell.
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

    /**
     * Simulates the evolution of the cell over time. --> Version simple à ameliorer 
     *
     * @param step simulation time step
     */
    /*public void evolve(TimeStep step) { ---> A mettre en place après

        switch (state) {

            case HEALTHY -> state = CellState.PARTIAL;

            case PARTIAL -> state = CellState.INFECTED;

            case INFECTED -> state = CellState.CRITICAL;

            case CRITICAL -> {
                // No further evolution
            }
        }
    }*/

    /**
     * Returns a textual representation of the cell.
     *
     * @return formatted cell information
     */
    @Override
    public String toString() {
        return cellID + "(" + state + ")";
    }

        
}

