package com.groupg.cells2d.engine;

import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import com.groupg.cells2d.model.board.SimulationParams;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    @Test
    void testCellNeighbors() {
        Cell[][] cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell("cell_" + i + "_" + j, 100, i, j);
            }
        }
        CellNeighborhood nh = new CellNeighborhood(cells);

        assertEquals(3, nh.getNeighbors(cells[0][0]).size(), "COIN HAUT-GAUCHE");
        assertEquals(5, nh.getNeighbors(cells[0][1]).size(), "BORD HAUT");
        assertEquals(8, nh.getNeighbors(cells[1][1]).size(), "CENTRE");
        assertEquals(3, nh.getNeighbors(cells[2][2]).size(), "COIN BAS-DROIT");
    }

    @Test
    void testSEIRcalculator() {
        SEIRData data = new SEIRData(90, 5, 5, 0, 0);
        SEIRData out = SEIRcalculator.compute(data, 0.3, 0.2, 0.1, 0.02, 0.05, 2, 100);

        double[] before = { data.getSusceptible(), data.getExposed(), data.getInfected(), data.getRecovered(), data.getDead() };
        double[] after  = { out.getSusceptible(),  out.getExposed(),  out.getInfected(),  out.getRecovered(),  out.getDead() };

        // valeurs finies et non négatives
        for (double v : after) assertTrue(Double.isFinite(v) && v >= 0);

        // conservation totale (vivants + morts)
        assertEquals(Arrays.stream(before).sum(), Arrays.stream(after).sum(), 1e-9);

        assertNotSame(data, out);

        SEIRData empty = new SEIRData(0,0,0,0,0);
        assertSame(empty, SEIRcalculator.compute(empty, 0.3, 0.2, 0.1, 0.02, 0.05, 0, 0));
    }

    @Test
    void testSimulationEngine() {
        Grid grid = new Grid(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = new Cell("cell_" + i + "_" + j, 100, i, j);
                cell.setSeirData(new SEIRData(100, 0, 0, 0, 0));
                grid.setCell(i, j, cell);
            }
        }

        Cell center = grid.getCell(1, 1);
        center.getSeirData().setSusceptible(50);
        center.getSeirData().setInfected(50);

        Propagation propagation = new Propagation();
        SimulationEngine engine = new SimulationEngine(grid, propagation);

        double beforeSus = center.getSeirData().getSusceptible();
        engine.step();
        assertEquals(1, engine.getStepCount(), "Après un step le compteur doit être incrémenté");

        double afterSus = center.getSeirData().getSusceptible();
        assertNotEquals(beforeSus, afterSus, "La valeur de susceptible de la cellule centre doit changer après un step");
    }

    // beta 0.5 sigma 0.3 gamma 0.05, propagationRate 1, mortalityRate 0.1
    @Test
    void testHighPropagationRate() {
        Grid grid = new Grid(3, 3);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Cell cell = new Cell("c_" + i + "_" + j, 100, i, j);
                cell.setSeirData(new SEIRData(100, 0, 0, 0, 0));
                grid.setCell(i, j, cell);
            }
        }

        Cell center = grid.getCell(1, 1);
        center.getSeirData().setSusceptible(50);
        center.getSeirData().setInfected(50);

        SimulationParams params = new SimulationParams(0.5, 0.3, 0.05, 1.0, 0.1);
        Propagation prop = new Propagation(params);
        SimulationEngine engine = new SimulationEngine(grid, prop);

        // Avancer 5 étapes pour permettre la transition E -> I (sigma > 0)
        for (int step = 0; step < 5; step++) {
            engine.step();
        }

        List<Cell> neighbors = new CellNeighborhood(grid.getMap()).getNeighbors(center);

        boolean someNeighborInfected = neighbors.stream()
                .anyMatch(n -> n.getSeirData().getInfected() > 1e-6);

        assertTrue(someNeighborInfected, "Avec propagationRate 1 et 5 step, au moins un voisin doit avoir des infectés");
    }
}