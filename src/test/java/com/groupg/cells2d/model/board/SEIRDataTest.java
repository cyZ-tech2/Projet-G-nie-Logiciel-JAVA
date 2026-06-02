package com.groupg.cells2d.model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SEIRDataTest {

    @Test
    void shouldCreateSeirData() {

        SEIRData data = new SEIRData(
                900,
                50,
                30,
                15,
                5
        );

        assertEquals(900, data.getSusceptible());
        assertEquals(50, data.getExposed());
        assertEquals(30, data.getInfected());
        assertEquals(15, data.getRecovered());
        assertEquals(5, data.getDead());
    }
}
