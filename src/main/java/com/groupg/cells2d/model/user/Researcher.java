package com.groupg.cells2d.model.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Researcher extends User {

    private String institution;

    public Researcher(double id, String username, String password, String institution)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(id, username, password);
        this.institution = institution;
    }

    public String getInstitution()          { return institution; }
    public void   setInstitution(String i)  { this.institution = i; }
}
