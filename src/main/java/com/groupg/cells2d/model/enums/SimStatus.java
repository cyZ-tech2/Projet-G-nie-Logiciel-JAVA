package com.groupg.cells2d.model.enums;

/**
 * état de la simulation
 */

public enum SimStatus {
    IDLE, //simu initialisée mais pas lancé
    RUNNING, //en cours
    PAUSED,  //pause
    FINISHED  //simu finie
}