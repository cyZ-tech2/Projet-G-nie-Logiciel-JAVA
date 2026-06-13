package com.groupg.cells2d.model.enums;

/**
 * Epidemic state of a simulation cell, derived from its infection rate.
 */
public enum CellState {
    /** Infection rate below 10% and fewer than half the population recovered. */
    HEALTHY,
    /** Infection rate between 10% and 30%. */
    PARTIAL,
    /** Infection rate between 30% and 60%. */
    INFECTED,
    /** Infection rate above 60%. */
    CRITICAL,
    /** Infection rate below 10% and at least half the population recovered. */
    RECOVERED
}