package com.groupg.cells2d;

import com.groupg.cells2d.data.SaveManager;
import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;

import java.util.Scanner;

/**
 * Console launcher used to demonstrate the simulation without JavaFX.
 * Each cell is displayed with a letter according to its SEIR state.
 */
public class ConsoleLauncher {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== Cells2D Console Simulation ===");

            int rows = readInt(scanner, "Rows", 5, 30);
            int cols = readInt(scanner, "Columns", 5, 30);
            int steps = readInt(scanner, "Number of steps", 1, 100);

            double beta = readDouble(scanner, "Beta / transmission rate", 0.0, 1.0);
            double sigma = readDouble(scanner, "Sigma / incubation rate", 0.0, 1.0);
            double gamma = readDouble(scanner, "Gamma / recovery rate", 0.0, 1.0);
            double propagationRate = readDouble(scanner, "Propagation rate", 0.0, 1.0);
            double mortalityRate = readDouble(scanner, "Mortality rate", 0.0, 1.0);
            double xi = readDouble(scanner, "Xi / waning immunity", 0.0, 1.0);

            Grid grid = createGrid(rows, cols);

            infectInitialCells(grid, rows, cols);

            SimulationParams params =
                    new SimulationParams(beta, sigma, gamma, propagationRate, mortalityRate);
            params.setXi(xi);

            SimulationEngine engine =
                    new SimulationEngine(grid, new Propagation(params));

            printLegend();

            System.out.println("\n=== Step 0 ===");
            printGrid(grid);
            printTotals(grid, engine.getStepCount());

            for (int i = 0; i < steps; i++) {
                engine.step();
                spreadToNeighbors(grid);
                
                System.out.println("\n=== Step " + engine.getStepCount() + " ===");
                printGrid(grid);
                printTotals(grid, engine.getStepCount());

                Thread.sleep(300);
            }

            SaveManager.save(engine, "console-save.dat");

            System.out.println("\nSimulation saved in console-save.dat");

            SimulationEngine loaded = SaveManager.load("console-save.dat");
            System.out.println("Simulation loaded successfully");
            System.out.println("Loaded step: " + loaded.getStepCount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a grid filled with healthy cells.
     */
    private static Grid createGrid(int rows, int cols) {
        Grid grid = new Grid(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell cell = new Cell("cell_" + i + "_" + j, 100, i, j);
                cell.setSeirData(new SEIRData(100, 0, 0, 0, 0));
                grid.setCell(i, j, cell);
            }
        }

        return grid;
    }

    /**
     * Infects several cells at the center of the grid to make propagation visible.
     */
    private static void infectInitialCells(Grid grid, int rows, int cols) {
        int centerRow = rows / 2;
        int centerCol = cols / 2;

        infectCell(grid, centerRow, centerCol, 40, 60);

        if (centerCol + 1 < cols) {
            infectCell(grid, centerRow, centerCol + 1, 50, 50);
        }

        if (centerRow + 1 < rows) {
            infectCell(grid, centerRow + 1, centerCol, 50, 50);
        }

        if (centerRow - 1 >= 0) {
            infectCell(grid, centerRow - 1, centerCol, 70, 30);
        }

        if (centerCol - 1 >= 0) {
            infectCell(grid, centerRow, centerCol - 1, 70, 30);
        }
    }

    /**
     * Infects a single cell with custom SEIR values.
     */
    private static void infectCell(
            Grid grid,
            int row,
            int col,
            double susceptible,
            double infected
    ) {
        grid.getCell(row, col).setSeirData(
                new SEIRData(susceptible, 0, infected, 0, 0)
        );
    }

    /**
     * Displays the legend used in the console.
     */
    private static void printLegend() {
        System.out.println();
        System.out.println("Legend:");
        System.out.println(". = Healthy / Susceptible");
        System.out.println("E = Exposed");
        System.out.println("I = Infected");
        System.out.println("R = Recovered");
        System.out.println("D = Dead");
    }

    /**
     * Displays the current grid using one character per cell.
     */
    private static void printGrid(Grid grid) {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                SEIRData data = grid.getCell(i, j).getSeirData();
                char state = getCellStateLetter(data);

                System.out.print(state + " ");
            }

            System.out.println();
        }
    }

    /**
     * Converts SEIR values into a visible state.
     * Disease states have priority so propagation appears clearly.
     */
    private static char getCellStateLetter(SEIRData data) {
        if (data.getInfected() > 0.1) {
            return 'I';
        }

        if (data.getExposed() > 0.1) {
            return 'E';
        }

        if (data.getDead() > 0.1) {
            return 'D';
        }

        if (data.getRecovered() > 0.1) {
            return 'R';
        }

        return '.';
    }

    /**
     * Displays global SEIR totals for the whole grid.
     */
    private static void printTotals(Grid grid, int step) {
        double susceptible = 0;
        double exposed = 0;
        double infected = 0;
        double recovered = 0;
        double dead = 0;

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                SEIRData data = grid.getCell(i, j).getSeirData();

                susceptible += data.getSusceptible();
                exposed += data.getExposed();
                infected += data.getInfected();
                recovered += data.getRecovered();
                dead += data.getDead();
            }
        }

        System.out.printf(
                "Step %d | S=%.2f | E=%.2f | I=%.2f | R=%.2f | D=%.2f%n",
                step,
                susceptible,
                exposed,
                infected,
                recovered,
                dead
        );
    }

    /**
     * Reads an integer value between min and max.
     */
    private static int readInt(Scanner scanner, String label, int min, int max) {
        while (true) {
            System.out.print(label + " (" + min + " - " + max + "): ");

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();

                if (value >= min && value <= max) {
                    return value;
                }
            } else {
                scanner.next();
            }

            System.out.println("Invalid value. Enter a number between " + min + " and " + max + ".");
        }
    }

        /**
         * Reads a decimal value between min and max.
         */
        private static double readDouble(Scanner scanner, String label, double min, double max) {
            while (true) {
                System.out.print(label + " (" + min + " - " + max + "): ");

                if (scanner.hasNextDouble()) {
                    double value = scanner.nextDouble();

                    if (value >= min && value <= max) {
                        return value;
                    }
                } else {
                    scanner.next();
                }

                System.out.println("Invalid value. Enter a number between " + min + " and " + max + ".");
            }
        }
        private static void spreadToNeighbors(Grid grid) {
        SEIRData[][] next = new SEIRData[grid.getRows()][grid.getCols()];

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                SEIRData data = grid.getCell(i, j).getSeirData();
                next[i][j] = new SEIRData(
                        data.getSusceptible(),
                        data.getExposed(),
                        data.getInfected(),
                        data.getRecovered(),
                        data.getDead()
                );
            }
        }

        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                SEIRData data = grid.getCell(i, j).getSeirData();

                if (data.getInfected() > 1) {
                    for (int k = 0; k < 4; k++) {
                        int ni = i + dx[k];
                        int nj = j + dy[k];

                        if (ni >= 0 && ni < grid.getRows()
                                && nj >= 0 && nj < grid.getCols()) {

                            SEIRData neighbor = next[ni][nj];

                            if (neighbor.getSusceptible() > 5) {
                                neighbor.setSusceptible(neighbor.getSusceptible() - 5);
                                neighbor.setExposed(neighbor.getExposed() + 5);
                            }
                        }
                    }
                }
            }
        }

        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                grid.getCell(i, j).setSeirData(next[i][j]);
            }
        }
    }
}