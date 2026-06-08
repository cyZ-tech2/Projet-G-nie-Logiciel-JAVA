package com.groupg.cells2d.model.exceptions;
/**
 * Thrown when the SEIR data contains
 * an inconsistent or invalid state.
 *
 */
public class InvalidSEIRStateException extends RuntimeException {

    public InvalidSEIRStateException() {
        super("Invalid SEIR state");
    }
}