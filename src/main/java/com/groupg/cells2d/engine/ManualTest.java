package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.SimulationParams;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;

import java.util.List;

/**
 * cette classe là est que pour le test je l'ai généré par l'ia avec les classes de engine
 */

public class ManualTest {

    public static void main(String[] args) {
        System.out.println("=== TEST MANUEL DU MOTEUR DE SIMULATION ===\n");

        // 1. TEST DE CELLNEIGHBORHOOD AVEC VISUALISATION
        System.out.println("1. TEST DE CELLNEIGHBORHOOD AVEC VISUALISATION");
        System.out.println("-----------------------------------------------");

        Cell[][] cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell("cell_" + i + "_" + j, 100, i, j);
            }
        }

        CellNeighborhood neighborhood = new CellNeighborhood(cells);

        visualizeNeighbors(neighborhood, cells, 0, 0, "COIN HAUT-GAUCHE");
        visualizeNeighbors(neighborhood, cells, 0, 1, "BORD HAUT");
        visualizeNeighbors(neighborhood, cells, 1, 1, "CENTRE");
        visualizeNeighbors(neighborhood, cells, 2, 2, "COIN BAS-DROIT");

        System.out.println();

        // 2. TEST DE SEIRCALCULATOR
        System.out.println("2. TEST DE SEIRCALCULATOR");
        System.out.println("--------------------------");

        SEIRData data = new SEIRData(90, 5, 5, 0, 0);
        System.out.println("Avant calcul: S=" + data.getSusceptible() +
                " E=" + data.getExposed() +
                " I=" + data.getInfected() +
                " R=" + data.getRecovered() +
                " D=" + data.getDead());

        SEIRData newData = SEIRcalculator.compute(data, 0.3, 0.2, 0.1, 0.02, 0.05, 2, 100,0.02);
        System.out.println("Après calcul: S=" + String.format("%.2f", newData.getSusceptible()) +
                " E=" + String.format("%.2f", newData.getExposed()) +
                " I=" + String.format("%.2f", newData.getInfected()) +
                " R=" + String.format("%.2f", newData.getRecovered()) +
                " D=" + String.format("%.2f", newData.getDead()));

        SEIRData emptyData = new SEIRData(0, 0, 0, 0, 0);
        SEIRData resultEmpty = SEIRcalculator.compute(emptyData, 0.3, 0.2, 0.1, 0.02, 0.05, 0, 0,0.02);
        System.out.println("Test population 0: " +
                (resultEmpty.getSusceptible() == 0 ? "OK ✓" : "ERREUR ✗"));

        boolean valuesChanged = (data.getSusceptible() != newData.getSusceptible());
        System.out.println("Les valeurs changent après calcul: " + (valuesChanged ? "OK ✓" : "ERREUR ✗"));

        System.out.println();

        // 3. TEST DE SIMULATIONENGINE (paramètres standards)
        System.out.println("3. TEST DE SIMULATIONENGINE (paramètres standards)");
        System.out.println("--------------------------------------------------");

        Grid grid = new Grid(3, 3);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = new Cell("cell_" + i + "_" + j, 100, i, j);
                cell.setSeirData(new SEIRData(100, 0, 0, 0, 0));
                grid.setCell(i, j, cell);
            }
        }

        Cell centerCell = grid.getCell(1, 1);
        centerCell.getSeirData().setSusceptible(50);  // Plus de susceptibles pour voir l'effet
        centerCell.getSeirData().setInfected(50);     // BEAUCOUP plus d'infectés

        Propagation propagation = new Propagation();
        SimulationEngine engine = new SimulationEngine(grid, propagation);

        System.out.println("État initial (étape " + engine.getStepCount() + "):");
        printDetailedGrid(grid);

        for (int step = 1; step <= 5; step++) {
            engine.step();
            System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Après l'étape " + engine.getStepCount() + ":");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            printDetailedGrid(grid);
        }

        // 4. TEST AVEC propagationRate ÉLEVÉ
        System.out.println("\n4. TEST AVEC propagationRate = 1.0 (MAXIMUM)");
        System.out.println("-----------------------------------------------");

        Grid grid2 = new Grid(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = new Cell("c_" + i + "_" + j, 100, i, j);
                cell.setSeirData(new SEIRData(100, 0, 0, 0, 0));
                grid2.setCell(i, j, cell);
            }
        }

        Cell center2 = grid2.getCell(1, 1);
        center2.getSeirData().setSusceptible(50);
        center2.getSeirData().setInfected(50);

        SimulationParams params = new SimulationParams(0.5, 0.3, 0.05, 1.0, 0.1);
        Propagation prop2 = new Propagation(params);
        SimulationEngine engine2 = new SimulationEngine(grid2, prop2);

        System.out.println("Paramètres: propagationRate=" + params.getPropagationRate());
        System.out.println("\nÉtat initial:");
        printDetailedGrid(grid2);

        for (int step = 1; step <= 5; step++) {
            engine2.step();
            System.out.println("\nAprès l'étape " + engine2.getStepCount() + ":");
            printDetailedGrid(grid2);
        }

        // 5. VISUALISATION FINALE
        System.out.println("\n5. VISUALISATION DES VOISINS DE (1,1)");
        System.out.println("---------------------------------------");

        Cell target = grid2.getCell(1, 1);
        CellNeighborhood nh = new CellNeighborhood(grid2.getMap());
        List<Cell> neighbors = nh.getNeighbors(target);

        System.out.println("Nombre de voisins: " + neighbors.size());
        printGridWithHighlight(grid2, target, neighbors);

        System.out.println("\n=== TEST TERMINÉ ===");
    }

    private static void visualizeNeighbors(CellNeighborhood nh, Cell[][] cells,
                                           int row, int col, String position) {
        System.out.println("\n--- " + position + " [" + row + "," + col + "] ---");
        Cell target = cells[row][col];
        List<Cell> neighbors = nh.getNeighbors(target);
        System.out.println("Nombre de voisins: " + neighbors.size());
        System.out.println("Grille (X = cible, V = voisin, · = autre):");
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (i == row && j == col) {
                    System.out.print("[  X  ] ");
                } else if (neighbors.contains(cells[i][j])) {
                    System.out.print("[  V  ] ");
                } else {
                    System.out.print("[  ·  ] ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void printDetailedGrid(Grid grid) {
        System.out.println("┌─────────────┬─────────────┬─────────────┐");
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null) {
                    SEIRData data = cell.getSeirData();
                    System.out.printf("│ S:%3.0f      ", data.getSusceptible());
                }
            }
            System.out.println("│");
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null) {
                    SEIRData data = cell.getSeirData();
                    System.out.printf("│ E:%3.0f      ", data.getExposed());
                }
            }
            System.out.println("│");
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null) {
                    SEIRData data = cell.getSeirData();
                    System.out.printf("│ I:%3.0f      ", data.getInfected());
                }
            }
            System.out.println("│");
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null) {
                    SEIRData data = cell.getSeirData();
                    System.out.printf("│ R:%3.0f D:%2.0f ", data.getRecovered(), data.getDead());
                }
            }
            System.out.println("│");
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell != null) {
                    System.out.printf("│ %-11s ", cell.getState().toString());
                }
            }
            System.out.println("│");
            if (i < grid.getRows() - 1) {
                System.out.println("├─────────────┼─────────────┼─────────────┤");
            }
        }
        System.out.println("└─────────────┴─────────────┴─────────────┘");
    }

    private static void printGridWithHighlight(Grid grid, Cell target, List<Cell> neighbors) {
        System.out.println("Légende: [T] = Cible, [V] = Voisin, [ ] = Autre\n");
        for (int i = 0; i < grid.getRows(); i++) {
            for (int j = 0; j < grid.getCols(); j++) {
                Cell cell = grid.getCell(i, j);
                if (cell == null) continue;
                String marker;
                if (cell == target) {
                    marker = "T";
                } else if (neighbors.contains(cell)) {
                    marker = "V";
                } else {
                    marker = " ";
                }
                SEIRData data = cell.getSeirData();
                System.out.printf("[%s S:%3.0f I:%2.0f %-7s] ",
                        marker, data.getSusceptible(), data.getInfected(),
                        cell.getState());
            }
            System.out.println("\n");
        }
    }
}