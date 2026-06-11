package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.SEIRData;

/**
 * SEIR formules for the calculator of susceptible, exposed, infected, recovered and dead people
 * @see <a href="https://en.wikipedia.org/wiki/Compartmental_models_in_epidemiology">SEIRD Model</a>
 */

public class SEIRcalculator{

    private SEIRcalculator(){} // prevents instantiation

    /**
     * method to calculate the number of people (susceptible, exposed, infected, recovered and dead)
     * @param seirData the initial values
     * @param beta transmission rate of the infection
     * @param sigma incubation period/rate of the infection (sleeping state without symptômes of a disease)
     * @param gamma recovery rate (people not dying during the disease)
     * @param mortalityRate percentage of people dying because of the disease
     * @param propagationRate the movability of the infection
     * @param avgNeighborInfected average infected population from the neighbors
     * @param population people
     * @return new values for seirData
     */
    public static SEIRData compute(SEIRData seirData, double beta, double sigma, double gamma, double mortalityRate, double propagationRate, double avgNeighborInfected, int population,double xi){
        double s=seirData.getSusceptible();
        double e=seirData.getExposed();
        double i=seirData.getInfected();
        double r=seirData.getRecovered();
        double d=seirData.getDead();

        if(population==0){return seirData;}     //if no population nothing to do, also to not divide by 0

        //local infections, in the cell itself
        double localTransmission=(beta*s*i)/population;

        //neighbors infected (spacial propagation), infected people from neighboring cells expose our cell population
        double spatialTransmission=propagationRate*avgNeighborInfected*(s/population);

        //formules of SEIRD propagation model

        double waningImmunity = xi * r;

        double dS=-localTransmission-spatialTransmission + waningImmunity;
        double dE=localTransmission+spatialTransmission-(sigma*e);
        double dI=(sigma*e)-(gamma*i);
        double dR=(1-mortalityRate)*gamma*i - waningImmunity;
        double dD=mortalityRate*gamma*i;

        //verify that values don't become negative
        double newS = Math.max(0, s + dS);
        double newE = Math.max(0, e + dE);
        double newI = Math.max(0, i + dI);
        double newR = Math.max(0, r + dR);
        double newD = Math.max(0, d + dD);

        return new SEIRData(newS, newE, newI, newR, newD);
    }
}