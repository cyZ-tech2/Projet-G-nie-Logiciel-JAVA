package com.groupg.cells2d.model.exceptions;
/**
 * Thrown when a negative population value is provided.
 *
 * A cell or SEIR model cannot contain
 * a population lower than zero.
 */
public class InvalidPopulationException extends RuntimeException {
    public InvalidPopulationException (int population){
        super("Population cannot be negative : " + population);
    }

}