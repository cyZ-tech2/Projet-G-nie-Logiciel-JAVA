package com.groupg.cells2d.engine;

import com.groupg.cells2d.data.SaveManager;
import com.groupg.cells2d.model.board.Cell;
import com.groupg.cells2d.model.board.Grid;
import com.groupg.cells2d.model.board.SEIRData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    @Test
    void testSEIRcalculator() {
        SEIRData data = new SEIRData(90, 5, 5, 0, 0);

        SEIRData out = SEIRcalculator.compute(
                data,
                0.3,
                0.2,
                0.1,
                0.02,
                0.05,
                2,
                100,
                0.03
        );

        double[] before = {
                data.getSusceptible(),
                data.getExposed(),
                data.getInfected(),
                data.getRecovered(),
                data.getDead()
        };

        double[] after = {
                out.getSusceptible(),
                out.getExposed(),
                out.getInfected(),
                out.getRecovered(),
                out.getDead()
        };

        for (double v : after) {
            assertTrue(Double.isFinite(v) && v >= 0);
        }

        assertEquals(Arrays.stream(before).sum(), Arrays.stream(after).sum(), 1e-9);
        assertNotSame(data, out);

        SEIRData empty = new SEIRData(0, 0, 0, 0, 0);

        assertSame(
                empty,
                SEIRcalculator.compute(empty, 0.3, 0.2, 0.1, 0.02, 0.05, 0, 0, 0.03)
        );
    }

    @Test
    void testSaveAndLoadSimulationEngine() throws Exception {
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

        engine.step();

        String filePath = "save-test.dat";

        SaveManager.save(engine, filePath);

        SimulationEngine loadedEngine = SaveManager.load(filePath);

        assertNotNull(loadedEngine);
        assertEquals(engine.getStepCount(), loadedEngine.getStepCount());
        assertEquals(3, loadedEngine.getGrid().getRows());
        assertEquals(3, loadedEngine.getGrid().getCols());

        Cell loadedCenter = loadedEngine.getGrid().getCell(1, 1);

        assertNotNull(loadedCenter);
        assertEquals(
                center.getSeirData().getInfected(),
                loadedCenter.getSeirData().getInfected(),
                1e-9
        );

        assertTrue(new File(filePath).delete());
    }
}