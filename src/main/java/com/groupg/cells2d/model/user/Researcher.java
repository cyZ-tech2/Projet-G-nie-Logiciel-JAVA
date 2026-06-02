package com.groupg.cells2d.model.user;

/**
 * classe de notre chercheur
 */

public class Researcher extends User{
    public String departement;

    public Researcher(double id, String username, String password) throws Exception {
        super(id, username, password);
    }
}
