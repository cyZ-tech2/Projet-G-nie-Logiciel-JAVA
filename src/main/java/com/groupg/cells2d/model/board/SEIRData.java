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

    /**
     * Computes the total population represented by
     * this SEIR state (excluding deceased individuals).
     *
     * @return total living population
     */
    public double getTotalPopulation() {
        return susceptible + exposed + infected + recovered;
    } 


        /**
     * Computes the next SEIR step without spatial propagation.
     *
     * @param beta transmission rate
     * @param sigma incubation rate
     * @param gamma recovery rate
     * @param mortalityRate mortality rate
     * @param population total population of the cell
     */
    public void computeNextStep(double beta,double sigma,double gamma,double mortalityRate,int population) {
                                                            
        if (population == 0) {
            return;
        }

        double newExposed = (beta * susceptible * infected) / population;
        double newInfected = sigma * exposed;
        double recoveredPeople = (1 - mortalityRate) * gamma * infected;
        double deadPeople = mortalityRate * gamma * infected;

        susceptible -= newExposed;
        exposed += newExposed - newInfected;
        infected += newInfected - recoveredPeople - deadPeople;
        recovered += recoveredPeople;
        dead += deadPeople;
        validate();

    }

    /**
    * Prevents negative values in SEIR compartments.
     */
    private void validate() {
        susceptible = Math.max(0, susceptible);
        exposed = Math.max(0, exposed);
        infected = Math.max(0, infected);
        recovered = Math.max(0, recovered);
        dead = Math.max(0, dead);
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
