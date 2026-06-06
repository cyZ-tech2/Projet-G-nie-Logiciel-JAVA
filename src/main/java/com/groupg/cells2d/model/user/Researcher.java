package com.groupg.cells2d.model.user;

import java.util.List;

/**
 * class that describes researcher users
 */

public class Researcher extends User{
    private String institution;
    //private List<> simulationList;
    public Researcher(double id, String username, String password,String institution) throws Exception {
        super(id, username, password);
        this.institution = institution;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }
}
