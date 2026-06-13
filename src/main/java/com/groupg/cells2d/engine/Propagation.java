package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;

import java.io.Serializable;
import java.util.List;

/**
 * Reads SEIR parameters from a {@link SimulationParams} instance and applies
 * the propagation formula to a single cell based on its neighbours.
 */

public class Propagation implements Serializable{
    private SimulationParams params;
    private static final long serialVersionUID = 1L;

    /** Creates a Propagation instance with default SEIR parameters. */
    public Propagation(){this.params = new SimulationParams();}

    /**
     * Creates a Propagation instance with custom SEIR parameters.
     * @param params the SEIR parameter set to use
     */
    public Propagation(SimulationParams params){this.params = params;}

    /**
     * Returns the current simulation parameter set.
     * @return simulation parameters (beta, sigma, gamma, etc.)
     */
    public SimulationParams getParams(){return params;}

    /**
     * Replaces the current parameter set.
     * @param params new simulation parameters
     */
    public void setParams(SimulationParams params){this.params = params;}

    /**
     * Applies one SEIR propagation step to a cell.
     * @param cell      the target cell to update
     * @param neighbors the list of directly adjacent cells
     * @return the updated SEIR data for the next simulation step
     */
    public SEIRData apply(Cell cell, List<Cell> neighbors){
        if(cell.getPopulation()==0){return cell.getSeirData();}

        double totalNeighborInfected = 0; // cumulative infected count across all neighbours
        for(Cell neighbor : neighbors){
            totalNeighborInfected += neighbor.getSeirData().getInfected();
        }

//        if(neighbors.isEmpty()){  //verification to not divide by zero if no neighbors
//            avgNeighborInfected = 0;
//        } else {
//            avgNeighborInfected = avgNeighborInfected / neighbors.size();
//        }

        return SEIRcalculator.compute(
                cell.getSeirData(),
                params.getBeta(), params.getSigma(), params.getGamma(),
                params.getMortalityRate(), params.getPropagationRate(),
                totalNeighborInfected, cell.getPopulation(),
                params.getXi()
        );
    }
}