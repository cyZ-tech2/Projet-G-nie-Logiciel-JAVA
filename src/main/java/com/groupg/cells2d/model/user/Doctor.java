package com.groupg.cells2d.model.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Represents a doctor user.
 * In addition to standard user credentials, a doctor has a hospital location
 * and a medical speciality used to contextualise patient cases.
 */
public class Doctor extends User {

    private String location;
    private String speciality;

    /**
     * Creates a new doctor account.
     * @param id         numeric user identifier
     * @param username   login name
     * @param password   plain-text password (hashed immediately)
     * @param location   hospital or practice location
     * @param speciality medical speciality
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public Doctor(double id, String username, String password, String location, String speciality)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(id, username, password);
        this.location   = location;
        this.speciality = speciality;
    }

    /** Returns the doctor's practice location. */
    public String getLocation()              { return location; }
    /** Sets the doctor's practice location. */
    public void   setLocation(String l)      { this.location = l; }
    /** Returns the doctor's medical speciality. */
    public String getSpeciality()            { return speciality; }
    /** Sets the doctor's medical speciality. */
    public void   setSpeciality(String s)    { this.speciality = s; }
}
