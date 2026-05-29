package com.groupg.cells2d.model.board;


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
public class SEIRData {

    private double susceptible;
    private double exposed;
    private double infected;
    private double recovered;
    private double dead;

    /* Initialize the seed */
    public SEIRData(double totalPopulation) {
        this.susceptible = totalPopulation;
        this.exposed     = 0;
        this.infected    = 0;
        this.recovered   = 0;
        this.dead        = 0;
    }

    public SEIRData(double susceptible, double exposed, double infected, double recovered, double dead) {
        this.susceptible = susceptible;
        this.exposed     = exposed;
        this.infected    = infected;
        this.recovered   = recovered;
        this.dead        = dead;
    }

    /* I create this one so the Cell file works but it's temporary */
    public SEIRData(){
        this(0);
    }


    //exemple de simulation ---> A remplacer avec les parametres de SimulationParam 
    public void computeNextStep() {

        double newExposed = susceptible * 0.05;
        double newInfected = exposed * 0.10;
        double newRecovered = infected * 0.10;

        susceptible -= newExposed;
        exposed += newExposed - newInfected;
        infected += newInfected - newRecovered;
        recovered += newRecovered;

    }

    public double getSusceptible() {
        return susceptible;
    }

    public void setSusceptible(double susceptible) {
        this.susceptible = susceptible;
    }

    public double getExposed() {
        return exposed;
    }

    public void setExposed(double exposed) {
        this.exposed = exposed;
    }

    public double getInfected() {
        return infected;
    }

    public void setInfected(double infected) {
        this.infected = infected;
    }

    public double getRecovered() {
        return recovered;
    }

    public void setRecovered(double recovered) {
        this.recovered = recovered;
    }

    public double getDead() {
        return dead;
    }

    public void setDead(double dead) {
        this.dead = dead;
    }
}
