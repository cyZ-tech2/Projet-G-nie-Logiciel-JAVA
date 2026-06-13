package com.groupg.cells2d.model.board;

import java.io.Serializable;

/**
 * Holds all numeric parameters used by the SEIRD propagation model.
 */

public class SimulationParams implements Serializable {
    private double beta;    // transmission rate
    private double sigma;   // incubation rate (exposed → infectious)
    private double gamma;   // recovery rate
    private double propagationRate;     // spatial spread rate between cells
    private double mortalityRate;   // fraction of infected individuals who die
    private double xi; // waning immunity rate (SEIRS extension)
    private static final long serialVersionUID = 1L;
    /** Creates a parameter set with default COVID-like values. */
    public SimulationParams(){
        this.beta=0.3;
        this.sigma=0.2;
        this.gamma=0.3;
        this.propagationRate=0.10;
        this.mortalityRate=0.02;
        this.xi = 0.03;
    }

    /**
     * Creates a custom parameter set.
     * @param beta            transmission rate
     * @param sigma           incubation rate
     * @param gamma           recovery rate
     * @param propagationRate spatial spread rate between cells
     * @param mortalityRate   mortality rate
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

    /**
     * Returns the waning immunity rate (SEIRS extension).
     * @return xi
     */
    public double getXi() {
        return xi;
    }

    /**
     * Sets the waning immunity rate.
     * @param xi new waning immunity rate
     */
    public void setXi(double xi) {
        this.xi = xi;
    }
}