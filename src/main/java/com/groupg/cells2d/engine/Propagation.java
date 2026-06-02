package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.SimulationParams;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.SEIRData;

import java.util.List;

/**
 * takes the parameters of SEIR simulation from SimulationParams from model and applies the propagation on a cell
 */

public class Propagation{
    private SimulationParams params;

    /**
     * constructor for Propagation for standard propagation
     */
    public Propagation(){this.params = new SimulationParams();} // standard

    /**
     * constructor for Propagation for customised propagation
     * @param params the values we want to give to variables of SEIRcalculator
     */
    public Propagation(SimulationParams params){this.params = params;} // custom

    /**
     * getter for params
     * @return the params we need beta,sigma,gamma etc
     */
    public SimulationParams getParams(){return params;}

    /**
     * setter for params if we want to change them manually
     * @param params our simulation parameters
     */
    public void setParams(SimulationParams params){this.params = params;}

    /**
     * Applies SEIR propagation to a cell based on its neighbors
     * @param cell the target cell to update
     * @param neighbors the list of neighboring cells
     */
    public void apply(Cell cell, List<Cell> neighbors){
        if(cell.getPopulation()==0) return; //nothing to compute, there's nobody so we do nothing if no population

        SEIRData data=cell.getSeirData();

        double avgNeighborInfected=0;   //average infected population from the neighbors
        for(Cell neighbor:neighbors){
            avgNeighborInfected+=neighbor.getSeirData().getInfected();
        }

        if (neighbors.isEmpty()){       //verification to not divide by zero if no neighbors
            avgNeighborInfected = 0;}
        else{
            avgNeighborInfected = avgNeighborInfected / neighbors.size();}


        SEIRData newData= SEIRcalculator.compute(data, params.getBeta(), params.getSigma(), params.getGamma(), params.getMortalityRate(), params.getPropagationRate(), avgNeighborInfected, cell.getPopulation());

        cell.setSeirData(newData);

        double infectionRate = newData.getInfected() / cell.getPopulation();
        cell.updateState(infectionRate);
    }

}
