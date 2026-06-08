package com.groupg.cells2d.model.board;

/**
 * parameters of our SEIR formules
 */

public class SimulationParams{
    private double beta;    //transmission
    private double sigma;   //incubation
    private double gamma;   //recovery
    private double propagationRate;     //propagation
    private double mortalityRate;   //mortality

    /**
     * constructor for standard propagation
     */
    public SimulationParams(){
        this.beta=0.3;
        this.sigma=0.2;
        this.gamma=0.1;
        this.propagationRate=0.05;
        this.mortalityRate=0.02;
    }

    /**
     * constructor for custom propagation
     * @param beta transmission rate
     * @param sigma incubation rate
     * @param gamma recovery rate
     * @param propagationRate mobility rate between cells
     */
    public SimulationParams(double beta, double sigma, double gamma, double propagationRate, double mortalityRate){
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

}