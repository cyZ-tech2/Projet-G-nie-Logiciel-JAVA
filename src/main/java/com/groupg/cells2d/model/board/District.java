package com.groupg.cells2d.model.board;


/**
 * Represents a regional district inside the global grid.
 *
 * <p>
 * A district is a specialized grid containing epidemic cells
 * and additional regional management features such as
 * risk evaluation and quarantine handling.
 * </p>
 */
public class District extends Grid {

    private String id;
    private String name;
    private RiskLevel riskLevel;
    private boolean quarantine;

    /**
     * Creates a new district.
     *
     * @param id district identifier
     * @param name district name
     * @param rows number of rows
     * @param cols number of columns
     */
    public District(String id, String name, int rows, int cols) {

        super(rows, cols);

        this.id = id;
        this.name = name;
        this.quarantine = false;
        this.riskLevel = RiskLevel.LOW;
    }

    /**
     * Computes the total infected population
     * inside the district.
     *
     * @return infected population
     */
    public double getTotalInfected() {

        double infected = 0;

        for (Cell[] row : getMap()) {

            for (Cell cell : row) {

                if (cell != null) {
                    infected += cell.getSeirData().getInfected();
                }
            }
        }

        return infected;
    }

    /**
     * Evaluates the epidemic risk level.
     *
     * @return district risk level
     */
    /**public RiskLevel evaluateRisk() {

        double rate = getTotalInfected() / getTotalPopulation();

        if (rate == 0)
            riskLevel = RiskLevel.LOW;

        else if (rate < 0.25)
            riskLevel = RiskLevel.MEDIUM;

        else if (rate < 0.60)
            riskLevel = RiskLevel.HIGH;

        else
            riskLevel = RiskLevel.CRITICAL;

        return riskLevel;
    }*/

    /**
     * Activates district lockdown.
     */
    public void lockDown() {
        quarantine = true;
    }

    /**
     * Removes district lockdown.
     */
    public void liftLockDown() {
        quarantine = false;
    }
}