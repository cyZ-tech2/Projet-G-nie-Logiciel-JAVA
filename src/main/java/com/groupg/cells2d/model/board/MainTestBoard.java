package com.groupg.cells2d.model.board;

import com.groupg.cells2d.model.enums.CellState;

public class MainTestBoard {

    public static void main(String[] args) {

        SEIRData seir1 = new SEIRData(900, 50, 40, 10, 0);
        SEIRData seir2 = new SEIRData(800, 100, 80, 20, 0);

        Cell cell1 = new Cell("C1", 1000, CellState.CRITICAL, seir1, 0, 0);
        Cell cell2 = new Cell("C2", 1000, CellState.INFECTED, seir2, 0, 1);

        System.out.println("=== CELL TEST ===");
        System.out.println("Cell 1 id: " + cell1.getCellId());
        System.out.println("Cell 1 infection rate: " + cell1.getInfectionRate());

        cell1.evolve(0.3, 0.2, 0.1, 0.02);

        System.out.println("After evolve:");
        System.out.println("Susceptible: " + cell1.getSeirData().getSusceptible());
        System.out.println("Exposed: " + cell1.getSeirData().getExposed());
        System.out.println("Infected: " + cell1.getSeirData().getInfected());
        System.out.println("Recovered: " + cell1.getSeirData().getRecovered());
        System.out.println("Dead: " + cell1.getSeirData().getDead());

        System.out.println("\n=== GRID TEST ===");

        Grid grid = new Grid(2, 2);
        grid.setCell(0, 0, cell1);
        grid.setCell(0, 1, cell2);

        System.out.println("Cell at (0,0): " + grid.getCell(0, 0).getCellId());
        System.out.println("Neighbors of C1: " + grid.getNeighbours(cell1).size());

        System.out.println("\n=== DISTRICT TEST ===");

        District district = new District("D1", "Central District", 2, 2);
        district.setCell(0, 0, cell1);
        district.setCell(0, 1, cell2);

        System.out.println("District id: " + district.getId());
        System.out.println("District name: " + district.getName());
        System.out.println("Total infected: " + district.getTotalInfected());

        district.lockDown();
        System.out.println("Is quarantined: " + district.isQuarantine());

        district.liftLockDown();
        System.out.println("Is quarantined after lift: " + district.isQuarantine());
    }
}