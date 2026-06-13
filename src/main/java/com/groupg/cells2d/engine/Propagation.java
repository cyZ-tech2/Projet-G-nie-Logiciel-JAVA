package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads SEIR parameters from a {@link SimulationParams} instance and applies
 * the propagation formula to a single cell based on its neighbours.
 */

public class Propagation implements Serializable{
    private SimulationParams params;
    private static final long serialVersionUID = 1L;

    /** Creates a Propagation instance with default SEIRDS parameters. */
    public Propagation(){this.params = new SimulationParams();}

    /**
     * Creates a Propagation instance with custom SEIRDS parameters.
     * @param params the SEIRDS parameter set to use
     * @throws IllegalArgumentException if params is null
     */
    public Propagation(SimulationParams params){
        if(params == null) throw new IllegalArgumentException("params cannot be null");
        this.params = params;
    }

    /**
     * Returns the current simulation parameter set.
     * @return simulation parameters (beta, sigma, gamma, etc.)
     */
    public SimulationParams getParams(){return params;}

    /**
     * Replaces the current parameter set.
     * @param params new simulation parameters
     * @throws IllegalArgumentException if params is null
     */
    public void setParams(SimulationParams params){
        if(params == null) throw new IllegalArgumentException("params cannot be null");
        this.params = params;
    }

    /**
     * Applies one SEIRDS propagation step to a cell.
     * @param cell      the target cell to update
     * @param neighbors the list of directly adjacent cells
     * @return the updated SEIRDS data for the next simulation step
     */
    public SEIRData apply(Cell cell, List<Cell> neighbors){
        if(cell == null) return null;
        if(cell.getSeirData() == null) return null;
        if(neighbors == null) neighbors = new ArrayList<>();
        if(cell.getPopulation()==0){return cell.getSeirData();}

        double totalNeighborInfected = 0; // cumulative infected count across all neighbours
        for(Cell neighbor : neighbors){
            if(neighbor != null && neighbor.getSeirData() != null){
                totalNeighborInfected += neighbor.getSeirData().getInfected();
            }
        }

        return SEIRcalculator.compute(
                cell.getSeirData(),
                params.getBeta(), params.getSigma(), params.getGamma(),
                params.getMortalityRate(), params.getPropagationRate(),
                totalNeighborInfected, cell.getPopulation(),
                params.getXi()
        );
    }
}