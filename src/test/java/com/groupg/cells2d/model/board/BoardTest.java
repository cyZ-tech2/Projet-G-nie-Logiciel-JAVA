package com.groupg.cells2d.model.board;

import com.groupg.cells2d.model.enums.CellState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Cell cell1;
    private Cell cell2;

    @BeforeEach
    void setUp() {
        SEIRData seir1 = new SEIRData(900, 50, 40, 10, 0);
        SEIRData seir2 = new SEIRData(800, 100, 80, 20, 0);

        cell1 = new Cell("C1", 1000, CellState.CRITICAL, seir1, 0, 0);
        cell2 = new Cell("C2", 1000, CellState.INFECTED, seir2, 0, 1);
    }

    @Test
    void testCellProperties() {
        assertEquals("C1", cell1.getCellId(), "cellId devrait être C1");
        assertEquals(0.04, cell1.getInfectionRate(), 1e-2, "infectionRate devrait être 40/1000");
        assertEquals(0, cell1.getMortalityRate(), 1e-6, "mortalityRate devrait être 0");
    }

    @Test
    void testCellSeirData() {
        assertEquals(900, cell1.getSeirData().getSusceptible());
        assertEquals(50, cell1.getSeirData().getExposed());
        assertEquals(40, cell1.getSeirData().getInfected());
        assertEquals(10, cell1.getSeirData().getRecovered());
        assertEquals(0, cell1.getSeirData().getDead());
    }

    @Test
    void testGridCellStorage() {
        Grid grid = new Grid(2, 2);
        grid.setCell(0, 0, cell1);
        grid.setCell(0, 1, cell2);

        assertEquals("C1", grid.getCell(0, 0).getCellId());
        assertEquals("C2", grid.getCell(0, 1).getCellId());
    }

    @Test
    void testDistrict() {
        District district = new District("D1", "Centre", 2, 2);

        assertEquals("D1", district.getId());
        assertEquals("Centre", district.getName());
        assertFalse(district.isQuarantine(), "district n'est pas en quarantaine par défaut");

        district.lockDown();
        assertTrue(district.isQuarantine(), "district devrait être en quarantaine après lockDown");

        district.liftLockDown();
        assertFalse(district.isQuarantine(), "district ne devrait pas être en quarantaine après liftLockDown");

    }
}