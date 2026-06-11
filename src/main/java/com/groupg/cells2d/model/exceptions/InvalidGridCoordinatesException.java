package com.groupg.cells2d.model.exceptions;
/**
 * Thrown when coordinates are outside
 * the boundaries of the grid.
 */
public class InvalidGridCoordinatesException extends RuntimeException {
    public InvalidGridCoordinatesException (int row, int col) {
        super("Invalid coordinates (" + row + "," + col + ")");
    }
}