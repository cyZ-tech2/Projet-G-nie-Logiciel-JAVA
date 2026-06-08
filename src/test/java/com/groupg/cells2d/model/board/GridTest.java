package com.groupg.cells2d.model.board;

import org.junit.jupiter.api.Test;

import com.groupg.cells2d.model.exceptions.InvalidGridCoordinatesException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GridTest {

    @Test
    void setCellAndGetCellShouldWork() {
        Grid grid = new Grid(2, 2);
        Cell cell = new Cell("C1", 100, 0, 0);

        grid.setCell(0, 0, cell);

        assertEquals(cell, grid.getCell(0, 0));
    }

   @Test
    void getCellOutsideGridShouldThrowException() {
        Grid grid = new Grid(2, 2);

        assertThrows(
            InvalidGridCoordinatesException.class,
            () -> grid.getCell(-1, 0)
        );

        assertThrows(
            InvalidGridCoordinatesException.class,
            () -> grid.getCell(5, 5)
        );
    }

    @Test
    void centerCellShouldHaveEightNeighbours() {
        Grid grid = new Grid(3, 3);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid.setCell(row, col, new Cell("C" + row + col, 100, row, col));
            }
        }

        Cell center = grid.getCell(1, 1);
        List<Cell> neighbours = grid.getNeighbours(center);

        assertEquals(8, neighbours.size());
    }

    @Test
    void cornerCellShouldHaveThreeNeighbours() {
        Grid grid = new Grid(3, 3);

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid.setCell(row, col, new Cell("C" + row + col, 100, row, col));
            }
        }

        Cell corner = grid.getCell(0, 0);
        List<Cell> neighbours = grid.getNeighbours(corner);

        assertEquals(3, neighbours.size());
    }
    @Test
    void getCellShouldThrowExceptionForInvalidCoordinates() {

        Grid grid = new Grid(2, 2);

        assertThrows(
            InvalidGridCoordinatesException.class,
            () -> grid.getCell(-1, 0)
        );
    }

}

