package com.groupg.cells2d.model.enums;

/**
 * Age group of a patient, used to classify reported cases.
 */
public enum AgeGroup {
    /** Patient is a child (under 18). */
    CHILD,
    /** Patient is an adult (18–64). */
    ADULT,
    /** Patient is elderly (65+). Note: typo retained for serialisation compatibility. */
    ELDERY
}
