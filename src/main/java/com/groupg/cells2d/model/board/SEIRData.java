package com.groupg.cells2d.model.board;

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



    public void computeNextStep() {

    }
}
