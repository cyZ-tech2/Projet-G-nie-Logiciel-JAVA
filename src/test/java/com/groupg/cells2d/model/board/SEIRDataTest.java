package com.groupg.cells2d.model.board;

import org.junit.jupiter.api.Test;

import com.groupg.cells2d.model.exceptions.InvalidSEIRStateException;

import static org.junit.jupiter.api.Assertions.*;

class SEIRDataTest {

    @Test
    void constructorWithPopulationShouldInitializeSusceptibleOnly() {
        
        SEIRData seir = new SEIRData(100);

        assertEquals(100, seir.getSusceptible());
        assertEquals(0, seir.getExposed());
        assertEquals(0, seir.getInfected());
        assertEquals(0, seir.getRecovered());
        assertEquals(0, seir.getDead());
    }

    @Test
    void constructorWithValuesShouldInitializeAllFields() {
        SEIRData seir = new SEIRData(80, 5, 10, 3, 2);

        assertEquals(80, seir.getSusceptible());
        assertEquals(5, seir.getExposed());
        assertEquals(10, seir.getInfected());
        assertEquals(3, seir.getRecovered());
        assertEquals(2, seir.getDead());
    }

    @Test
    void getTotalPopulationShouldExcludeDeadPeople() {
        SEIRData seir = new SEIRData(80, 5, 10, 3, 2);

        assertEquals(98, seir.getTotalPopulation());
    }

    @Test
    void settersShouldModifyValues() {
        SEIRData seir = new SEIRData(100);

        seir.setSusceptible(70);
        seir.setExposed(10);
        seir.setInfected(15);
        seir.setRecovered(4);
        seir.setDead(1);

        assertEquals(70, seir.getSusceptible());
        assertEquals(10, seir.getExposed());
        assertEquals(15, seir.getInfected());
        assertEquals(4, seir.getRecovered());
        assertEquals(1, seir.getDead());
    }
    @Test
    void constructorShouldThrowExceptionForNegativeValues() {

        assertThrows(
                InvalidSEIRStateException.class,
                () -> new SEIRData(-1,0,0,0,0)
        );
    }
}
