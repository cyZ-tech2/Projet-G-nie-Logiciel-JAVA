package com.groupg.cells2d.model.board;

import java.io.Serializable;

import com.groupg.cells2d.model.exceptions.InvalidPopulationException;
import com.groupg.cells2d.model.exceptions.InvalidSEIRStateException;

/**
 * Represents epidemiological SEIR data for a cell.
 *
 * The SEIR model is composed of:
 * - Susceptible: healthy individuals who can be infected
 * - Exposed: infected individuals not yet contagious
 * - Infected: contagious individuals
 * - Recovered: recovered individuals with immunity
 * - Dead: deceased individuals
 *
 * This class is used to simulate the evolution
 * of a disease inside a population cell.
 */
public class SEIRData implements Serializable {

    private double susceptible;
    private double exposed;
    private double infected;
    private double recovered;
    private double dead;
    private static final long serialVersionUID = 1L;

    // Creates a fully susceptible population (all individuals in S compartment)
    public SEIRData(double totalPopulation) {
        if (totalPopulation < 0) {
            throw new InvalidPopulationException((int)totalPopulation);
        }
        this.susceptible = totalPopulation;
        this.exposed     = 0;
        this.infected    = 0;
        this.recovered   = 0;
        this.dead        = 0;
    }

    /**
     * Creates a fully specified SEIR state.
     * All values must be non-negative.
     * @param susceptible  number of susceptible individuals
     * @param exposed      number of exposed (latent) individuals
     * @param infected     number of infectious individuals
     * @param recovered    number of recovered (immune) individuals
     * @param dead         cumulative number of deaths
     */
    public SEIRData(double susceptible, double exposed, double infected, double recovered, double dead) {
        if (susceptible < 0 || exposed < 0 || infected < 0 || recovered < 0 || dead < 0) {
            throw new InvalidSEIRStateException();
        }
        this.susceptible = susceptible;
        this.exposed     = exposed;
        this.infected    = infected;
        this.recovered   = recovered;
        this.dead        = dead;
    }

    /**
     * Computes the total population represented by
     * this SEIR state (excluding deceased individuals).
     *
     * @return total living population
     */
    public double getTotalPopulation() {
        return susceptible + exposed + infected + recovered;
    }


    /** Returns the number of susceptible individuals. */
    public double getSusceptible() {
        return susceptible;
    }

    /** Sets the number of susceptible individuals. */
    public void setSusceptible(double susceptible) {
        this.susceptible = susceptible;
    }

    /** Returns the number of exposed (latent) individuals. */
    public double getExposed() {
        return exposed;
    }

    /** Sets the number of exposed (latent) individuals. */
    public void setExposed(double exposed) {
        this.exposed = exposed;
    }

    /** Returns the number of infectious individuals. */
    public double getInfected() {
        return infected;
    }

    /** Sets the number of infectious individuals. */
    public void setInfected(double infected) {
        this.infected = infected;
    }

    /** Returns the number of recovered (immune) individuals. */
    public double getRecovered() {
        return recovered;
    }

    /** Sets the number of recovered individuals. */
    public void setRecovered(double recovered) {
        this.recovered = recovered;
    }

    /** Returns the cumulative number of deaths. */
    public double getDead() {
        return dead;
    }

    /** Sets the cumulative number of deaths. */
    public void setDead(double dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        return "SEIRData{" +
                "S=" + susceptible +
                ", E=" + exposed +
                ", I=" + infected +
                ", R=" + recovered +
                ", D=" + dead +
                '}';
    }
}