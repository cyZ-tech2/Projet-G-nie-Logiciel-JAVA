package com.groupg.cells2d;

import com.groupg.cells2d.data.SaveManager;
import com.groupg.cells2d.engine.Propagation;
import com.groupg.cells2d.engine.SimulationEngine;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;

import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

/**
 * Headless (command-line) simulation launcher.
 * Prompts the user for grid dimensions and SEIR parameters via stdin,
 * runs the simulation step by step, and prints a live ASCII grid to stdout.
 * Used when the {@code --console} flag is passed to {@link Launcher}.
 */
public class ConsoleLauncher {
    private int steps;
    private SimulationEngine engine;

    /** Creates a new console launcher with no engine loaded. */
    public ConsoleLauncher() {
    }

    /**
     * Loads a previously saved simulation from a file and prompts the user
     * for the number of steps to run.
     * @param loadPath path to the {@code .dat} save file
     */
    public void load(String loadPath) {
        try {
            this.engine = SaveManager.load(loadPath);
            System.out.println("\nSimulation loaded from " + loadPath);
            Scanner scanner = new Scanner(System.in);
            scanner.useLocale(Locale.US); // for display and input consistency (only use .)
            this.steps = readInt(scanner, "Number of steps", 1, 100);
            scanner.close();
        } catch (Exception e) {
            System.err.println("Error during loading: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Saves the current simulation state to a file.
     * @param savePath destination file path
     */
    public void save(String savePath) {
        try {
            SaveManager.save(this.engine, savePath);
            System.out.println("\nSimulation saved in " + savePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interactively prompts the user via stdin for grid dimensions,
     * number of steps, and all SEIR parameters, then initialises
     * a new simulation engine with a partially infected grid.
     */
    public void populate() {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US); // for display and input consistency (only use .)
        try {
            System.out.println("=== Cells2D Console Simulation ===");
            int rows = readInt(scanner, "Rows", 5, 30);
            int cols = readInt(scanner, "Columns", 5, 30);
            this.steps = readInt(scanner, "Number of steps", 1, 100);
            double beta = readDouble(scanner, "Beta / transmission", 0.0, 1.0);
            double sigma = readDouble(scanner, "Sigma / incubation", 0.0, 1.0);
            double gamma = readDouble(scanner, "Gamma / recovery", 0.0, 1.0);
            double propagationRate = readDouble(scanner, "Propagation rate", 0.0, 1.0);
            double mortalityRate = readDouble(scanner, "Mortality rate", 0.0, 1.0);
            double xi = readDouble(scanner, "Xi / waning immunity", 0.0, 1.0);
            scanner.close();
            Grid grid = createGrid(rows, cols);
            infectInitialCells(grid, rows, cols);

            SimulationParams params = new SimulationParams(beta, sigma, gamma, propagationRate, mortalityRate);
            params.setXi(xi);
            this.engine = new SimulationEngine(grid, new Propagation(params));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error during population: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }


    /**
     * Runs the simulation for the configured number of steps,
     * printing the ASCII grid and SEIR totals after each step.
     * Sleeps 300 ms between steps for readability.
     */
    public void run() {
        try {
            printLegend();
            System.out.println("\n=== Step " + engine.getStepCount() + " ===");
            printGrid(engine.getGrid());
            printTotals(engine.getGrid(), engine.getStepCount());

            for (int i = 0; i < steps; i++) {
                engine.step();
                spreadToNeighbors(engine.getGrid());
                System.out.println("\n=== Step " + engine.getStepCount() + " ===");
                printGrid(engine.getGrid());
                printTotals(engine.getGrid(), engine.getStepCount());
                Thread.sleep(300);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulation interrupted.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a uniform grid where every cell has a population of 100
     * and starts fully susceptible.
     * @param rows number of grid rows
     * @param cols number of grid columns
     * @return the initialised grid
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
     * Seeds the centre of the grid with a small cluster of infected cells
     * to start the epidemic spreading.
     * @param grid the grid to seed
     * @param rows total rows
     * @param cols total columns
     */
    private static void infectInitialCells(Grid grid, int rows, int cols) {
        int cr = rows / 2, cc = cols / 2;
        infectCell(grid, cr, cc, 40, 60);
        if (cc + 1 < cols) infectCell(grid, cr, cc + 1, 50, 50);
        if (cr + 1 < rows) infectCell(grid, cr + 1, cc, 50, 50);
        if (cr - 1 >= 0)   infectCell(grid, cr - 1, cc, 70, 30);
        if (cc - 1 >= 0)   infectCell(grid, cr, cc - 1, 70, 30);
    }

    /**
     * Sets the SEIR data of a single cell to the given susceptible and infected counts.
     * @param grid        the grid containing the cell
     * @param row         cell row
     * @param col         cell column
     * @param susceptible susceptible population
     * @param infected    infected population
     */
    private static void infectCell(Grid grid, int row, int col, double susceptible, double infected) {
        grid.getCell(row, col).setSeirData(new SEIRData(susceptible, 0, infected, 0, 0));
    }

    /**
     * Prints the ASCII character legend to stdout.
     */
    private static void printLegend() {
        System.out.println("\nLegend:\n. = Healthy\nE = Exposed\nI = Infected\nR = Recovered\nD = Dead");
    }

    /**
     * Prints the current grid state as an ASCII diagram to stdout.
     * @param grid the grid to display
     */
    private static void printGrid(Grid grid) {
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                System.out.print(getCellStateLetter(grid.getCell(i, j).getSeirData()) + " ");
            }
            System.out.println();
        }
    }

    /**
     * Returns a single character representing the dominant epidemic compartment
     * of a cell's SEIR data: 'I' (infected), 'E' (exposed), 'D' (dead),
     * 'R' (recovered), or '.' (susceptible / healthy).
     * @param data the SEIR data to evaluate
     * @return display character
     */
    private static char getCellStateLetter(SEIRData data) {
        if (data.getInfected() > 0.1)  return 'I';
        if (data.getExposed()  > 0.1)  return 'E';
        if (data.getDead()     > 0.1)  return 'D';
        if (data.getRecovered()> 0.1)  return 'R';
        return '.';
    }

    /**
     * Prints a one-line summary of total S, E, I, R, D compartment values
     * across all cells for the given step.
     * @param grid the grid to aggregate
     * @param step current simulation step
     */
    private static void printTotals(Grid grid, int step) {
        double s = 0, e = 0, i = 0, r = 0, d = 0;
        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                SEIRData data = grid.getCell(row, col).getSeirData();
                s += data.getSusceptible(); e += data.getExposed();
                i += data.getInfected();   r += data.getRecovered(); d += data.getDead();
            }
        }
        System.out.printf("Step %d | S=%.2f | E=%.2f | I=%.2f | R=%.2f | D=%.2f%n", step, s, e, i, r, d);
    }

    /**
     * Simple additional spatial spread pass: for every cell with more than 1 infected
     * individual, transfers 5 people from susceptible to exposed in each of the
     * 4 cardinal neighbours. Applied after each engine step in the console mode.
     * @param grid the grid to update
     */
    private static void spreadToNeighbors(Grid grid) {
        SEIRData[][] next = new SEIRData[grid.getRows()][grid.getCols()];
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                SEIRData d = grid.getCell(i, j).getSeirData();
                next[i][j] = new SEIRData(d.getSusceptible(), d.getExposed(),
                    d.getInfected(), d.getRecovered(), d.getDead());
            }
        }
        int[] dx = {-1, 1, 0, 0}, dy = {0, 0, -1, 1};
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                if (grid.getCell(i, j).getSeirData().getInfected() > 1) {
                    for (int k = 0; k < 4; k++) {
                        int ni = i + dx[k], nj = j + dy[k];
                        if (ni >= 0 && ni < grid.getRows() && nj >= 0 && nj < grid.getCols()) {
                            SEIRData nb = next[ni][nj];
                            if (nb.getSusceptible() > 5) {
                                nb.setSusceptible(nb.getSusceptible() - 5);
                                nb.setExposed(nb.getExposed() + 5);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < grid.getRows(); i++)
            for (int j = 0; j < grid.getCols(); j++)
                grid.getCell(i, j).setSeirData(next[i][j]);
    }

    /**
     * Prompts the user until a valid integer in [min, max] is entered.
     * @param sc    the scanner to read from
     * @param label the prompt label
     * @param min   minimum acceptable value (inclusive)
     * @param max   maximum acceptable value (inclusive)
     * @return the validated integer
     */
    private static int readInt(Scanner sc, String label, int min, int max) {
        while (true) {
            System.out.print(label + " (" + min + " - " + max + "): ");
            if (sc.hasNextInt()) {
                int v = sc.nextInt();
                if (v >= min && v <= max) return v;
            } else { sc.next(); }
            System.out.println("Invalid value. Enter a number between " + min + " and " + max + ".");
        }
    }

    /**
     * Prompts the user until a valid double in [min, max] is entered.
     * @param sc    the scanner to read from
     * @param label the prompt label
     * @param min   minimum acceptable value (inclusive)
     * @param max   maximum acceptable value (inclusive)
     * @return the validated double
     */
    private static double readDouble(Scanner sc, String label, double min, double max) {
        while (true) {
            System.out.print(label + " (" + min + " - " + max + "): ");
            if (sc.hasNextDouble()) {
                double v = sc.nextDouble();
                if (v >= min && v <= max) return v;
            } else { sc.next(); }
            System.out.println("Invalid value. Enter a number between " + min + " and " + max + ".");
        }
    }
}
