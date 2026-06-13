package com.groupg.cells2d.model.board;

/**
 * Quick manual test that creates a small grid and prints it to stdout.
 * Not part of the test suite — used for ad-hoc verification during development.
 */
public class MainTestBoard {
    /**
     * Entry point for the manual board test.
     * @param args unused
     */
    public static void main(String[] args) {
        Cell cell1 = new Cell("cell1", 1000, 0, 0);
        System.out.println("Cell 1 id: "             + cell1.getCellId());
        System.out.println("Cell 1 infection rate: " + cell1.getInfectionRate());

        cell1.setSeirData(new SEIRData(800, 0, 200, 0, 0));
        System.out.println("Susceptible: " + cell1.getSeirData().getSusceptible());
        System.out.println("Infected: "    + cell1.getSeirData().getInfected());
    }
}
