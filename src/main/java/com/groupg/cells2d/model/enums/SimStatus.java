package com.groupg.cells2d.model.enums;

/**
 * Represents the lifecycle state of a simulation run.
 */
public enum SimStatus {
    /** Engine is initialised but the simulation has not started yet. */
    IDLE,
    /** Simulation is actively advancing steps. */
    RUNNING,
    /** Simulation has been paused; it can be resumed with play(). */
    PAUSED,
    /** Simulation has been stopped and the step counter reset. */
    FINISHED
}