package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.SEIRData;

/**
 * Stateless utility class that computes one SEIRD propagation step.
 * Implements the compartmental epidemic model (S, E, I, R, D).
 * @see <a href="https://en.wikipedia.org/wiki/Compartmental_models_in_epidemiology">SEIRD Model</a>
 */

public class SEIRcalculator{

    private SEIRcalculator(){} // utility class — not instantiable

    /**
     * Computes the next SEIRD state for a single cell.
     * @param seirData           current compartment values
     * @param beta               transmission rate
     * @param sigma              incubation rate (latent → infectious)
     * @param gamma              recovery rate
     * @param mortalityRate      fraction of infectious individuals who die
     * @param propagationRate    spatial spread factor between neighbouring cells
     * @param avgNeighborInfected cumulative infected count from neighbouring cells
     * @param population         total cell population
     * @param xi                 waning immunity rate (SEIRS extension)
     * @return updated SEIR data for the next step
     */
    public static SEIRData compute(SEIRData seirData, double beta, double sigma, double gamma, double mortalityRate, double propagationRate, double avgNeighborInfected, int population,double xi){
        double s=seirData.getSusceptible();
        double e=seirData.getExposed();
        double i=seirData.getInfected();
        double r=seirData.getRecovered();
        double d=seirData.getDead();

        if(population==0){return seirData;}     // nothing to compute for empty cells; also avoids division by zero

        // Local transmission within the cell
        double localTransmission=(beta*s*i)/population;

        // Spatial transmission from neighbouring cells
        double spatialTransmission=propagationRate*avgNeighborInfected*(s/population);

        // SEIRD differential equations

        double waningImmunity = xi * r;

        double dS=-localTransmission-spatialTransmission + waningImmunity;
        double dE=localTransmission+spatialTransmission-(sigma*e);
        double dI=(sigma*e)-(gamma*i);
        double dR=(1-mortalityRate)*gamma*i - waningImmunity;
        double dD=mortalityRate*gamma*i;

        // Clamp all compartments to zero to prevent negative populations
        double newS = Math.max(0, s + dS);
        double newE = Math.max(0, e + dE);
        double newI = Math.max(0, i + dI);
        double newR = Math.max(0, r + dR);
        double newD = Math.max(0, d + dD);

        return new SEIRData(newS, newE, newI, newR, newD);
    }
}