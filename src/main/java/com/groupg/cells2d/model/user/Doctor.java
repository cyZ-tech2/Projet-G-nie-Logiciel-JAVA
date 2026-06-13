package com.groupg.cells2d.model.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Doctor extends User {

    private String location;
    private String speciality;

    public Doctor(double id, String username, String password, String location, String speciality)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(id, username, password);
        this.location   = location;
        this.speciality = speciality;
    }

    public String getLocation()              { return location; }
    public void   setLocation(String l)      { this.location = l; }
    public String getSpeciality()            { return speciality; }
    public void   setSpeciality(String s)    { this.speciality = s; }
}
