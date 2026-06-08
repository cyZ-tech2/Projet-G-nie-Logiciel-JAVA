package com.groupg.cells2d.model.board;

import com.groupg.cells2d.model.enums.CellState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistrictTest {

    @Test
    void constructorShouldInitializeDistrict() {
        District district = new District("D1", "Paris", 2, 2);

        assertEquals("D1", district.getId());
        assertEquals("Paris", district.getName());
        assertFalse(district.isQuarantine());
        assertEquals(2, district.getRows());
        assertEquals(2, district.getCols());
    }

    @Test
    void lockDownShouldActivateQuarantine() {
        District district = new District("D1", "Paris", 2, 2);

        district.lockDown();

        assertTrue(district.isQuarantine());
    }

    @Test
    void liftLockDownShouldDisableQuarantine() {
        District district = new District("D1", "Paris", 2, 2);

        district.lockDown();
        district.liftLockDown();

        assertFalse(district.isQuarantine());
    }

    @Test
    void getTotalInfectedShouldReturnSumOfInfectedCells() {
        District district = new District("D1", "Paris", 2, 2);

        SEIRData seir1 = new SEIRData(90, 0, 10, 0, 0);
        SEIRData seir2 = new SEIRData(80, 0, 20, 0, 0);

        district.setCell(0, 0, new Cell("C1", 100, CellState.INFECTED, seir1, 0, 0));
        district.setCell(0, 1, new Cell("C2", 100, CellState.INFECTED, seir2, 0, 1));

        assertEquals(30, district.getTotalInfected());
    }
}