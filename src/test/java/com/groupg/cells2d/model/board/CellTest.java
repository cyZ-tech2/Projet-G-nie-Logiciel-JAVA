package com.groupg.cells2d.model.board;

import com.groupg.cells2d.model.enums.CellState;
import com.groupg.cells2d.model.exceptions.InvalidPopulationException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void constructorShouldInitializeCell() {
        Cell cell = new Cell("C1", 100, 2, 3);

        assertEquals("C1", cell.getCellId());
        assertEquals(100, cell.getPopulation());
        assertEquals(2, cell.getRow());
        assertEquals(3, cell.getCol());
        assertEquals(CellState.HEALTHY, cell.getState());
        assertNotNull(cell.getSeirData());
    }

    @Test
    void addCaseShouldMakeCellInfected() {
        Cell cell = new Cell("C1", 100, 0, 0);

        cell.addCase(new PatientCase());

        assertEquals(CellState.INFECTED, cell.getState());
    }

    @Test
    void infectionRateShouldReturnInfectedDividedByPopulation() {
        SEIRData seir = new SEIRData(80, 0, 20, 0, 0);
        Cell cell = new Cell("C1", 100, CellState.INFECTED, seir, 0, 0);

        assertEquals(0.2, cell.getInfectionRate());
    }

    @Test
    void mortalityRateShouldReturnDeadDividedByPopulation() {
        SEIRData seir = new SEIRData(80, 0, 0, 10, 10);
        Cell cell = new Cell("C1", 100, CellState.CRITICAL, seir, 0, 0);

        assertEquals(0.1, cell.getMortalityRate());
    }
    @Test
    void constructorShouldThrowExceptionForNegativePopulation() {

        assertThrows(
                InvalidPopulationException.class,
                () -> new Cell("C1",-10,0,0)
        );
    }
}