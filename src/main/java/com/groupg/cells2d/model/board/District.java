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

    /**
     * Getters and setters
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public boolean isQuarantine() {
        return quarantine;
    }

    public void setQuarantine(boolean quarantine) {
        this.quarantine = quarantine;
    }


}