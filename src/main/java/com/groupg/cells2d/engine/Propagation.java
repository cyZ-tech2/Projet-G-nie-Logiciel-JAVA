package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;    //j'ai mis celui de board car je pense que les doublons
import com.groupg.cells2d.model.board.SEIRData;
// vont être supprimés dans model
import java.util.List;


/**
 * paramètres du la fonction de l'évolution de propagation à voir pour que ce ne soit pas la même
 * chose que simulationparams et/ou seircalculator
 */

public class Propagation {
    private double beta;
    private double sigma;
    private double gamma;
    private double propagationRate;
    private double mortalityRate;

    /**
     * constructor for standard propagation
     */
    public Propagation(){
        this.beta=0.3;
        this.sigma=0.2;
        this.gamma=0.1;
        this.propagationRate=0.05;
        this.mortalityRate=0.02;
    }

    /**
     * constructer for custom propagation
     * @param beta transmission rate
     * @param sigma incubation rate
     * @param gamma recovery rate
     * @param propagationRate mobility rate between cells
     */
    public Propagation(double beta, double sigma, double gamma, double propagationRate, double mortalityRate){
        this.beta=beta;
        this.sigma=sigma;
        this.gamma=gamma;
        this.propagationRate=propagationRate;
        this.mortalityRate=mortalityRate;
    }

    /**
     * getter to enable access to the transmission rate
     * @return beta our transmission rate
     */
    public double getBeta(){return beta;}

    /**
     * setter enables the changement of the transmission rate
     * @param beta takes the current transmission rate and changes it to what we want
     */
    public void setBeta(double beta){this.beta=beta;}

    /**
     * getter to enable access to the incubation rate
     * @return sigma our incubation rate
     */
    public double getSigma(){return sigma;}

    /**
     * setter enables the changement of the incubation rate
     * @param sigma takes the current incubation rate and changes it to what we want
     */
    public void setSigma(double sigma){this.sigma=sigma;}

    /**
     * getter to enable access to the recovery rate
     * @return gamma our recovery rate
     */
    public double getGamma(){return gamma;}

    /**
     * setter enables the changement of the recovery rate
     * @param gamma takes the current recovery rate and changes it to what we want
     */
    public void setGamma(double gamma){this.gamma=gamma;}

    /**
     * getter to enable access to the propagation rate
     * @return propagationRate our recovery rate
     */
    public double getPropagationRate(){return propagationRate;}

    /**
     * setter enables the changement of the propagation rate
     * @param propagationRate takes the current propagation rate and changes it to what we want
     */
    public void setPropagationRate(double propagationRate){this.propagationRate=propagationRate;}

    /**
     * setter enables the changement of the mortality rate
     * @param mortalityRate takes the current mortality rate and changes it to what we want
     */
    public void setMortalityRate(double mortalityRate){this.mortalityRate=mortalityRate;}

    /**
     * getter to enable access to the mortality rate
     * @return mortalityRate our recovery rate
     */
    public double getMortalityRate(){return mortalityRate;}

    /**
     * Applies SEIR propagation to a cell based on its neighbors
     * @param cell the target cell to update
     * @param neighbors the list of neighboring cells
     */
    public void apply(Cell cell, List<Cell> neighbors){
        if(cell.getPopulation()==0) return; //nothing to compute, there's nobody

        SEIRData data=cell.getSeirData();

        double avgNeighborInfected=0;
        for(Cell neighbor:neighbors){
            avgNeighborInfected+=neighbor.getSeirData().getInfected();
        }
        avgNeighborInfected=neighbors.isEmpty()?0:avgNeighborInfected/neighbors.size();

        SEIRData newData= SEIRcalculator.compute(data, beta, sigma, gamma, mortalityRate, propagationRate, avgNeighborInfected, cell.getPopulation());

        cell.setSeirData(newData);

        //lier avec cellstate
    }

}
