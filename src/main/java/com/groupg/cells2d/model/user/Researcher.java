package com.groupg.cells2d.model.user;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Represents a researcher user.
 * Researchers have access to full SEIR parameter controls and
 * advanced drawing tools not available to doctors.
 */
public class Researcher extends User {

    private String institution;

    /**
     * Creates a new researcher account.
     * @param id          numeric user identifier
     * @param username    login name
     * @param password    plain-text password (hashed immediately)
     * @param institution affiliated research institution
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public Researcher(double id, String username, String password, String institution)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        super(id, username, password);
        this.institution = institution;
    }

    /** Returns the researcher's affiliated institution. */
    public String getInstitution()          { return institution; }
    /** Sets the researcher's affiliated institution. */
    public void   setInstitution(String i)  { this.institution = i; }
}
