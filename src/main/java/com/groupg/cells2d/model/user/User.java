package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.data.PasswordHash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Abstract base class for all application users.
 * Stores credentials (username + hashed password) and provides
 * static helper methods to authenticate and look up users
 * from the JSON user repository.
 */
public abstract class User {

    private final double       id;
    private final String       username;
    private final PasswordHash passwordHash;

    /**
     * Creates a new user and hashes the provided plain-text password.
     * @param id       numeric user identifier
     * @param username login name
     * @param password plain-text password (hashed immediately; not stored)
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     */
    public User(double id, String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.id           = id;
        this.username     = username;
        this.passwordHash = new PasswordHash(password);
    }

    /** Returns the numeric identifier of this user. */
    public double       getId()           { return id; }
    /** Returns the login name of this user. */
    public String       getUsername()     { return username; }
    /** Returns the hashed password object for credential verification. */
    public PasswordHash getPasswordHash() { return passwordHash; }

    @Override
    public String toString() { return username + " " + id; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Double.compare(id, user.id) == 0 && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() { return Objects.hash(username); }

    /**
     * Validates a username / password pair against the user repository.
     * @param username the login name to look up
     * @param password the plain-text password to verify
     * @return true if credentials are valid, false otherwise
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     * @throws IOException              if the repository file cannot be read
     */
    public static boolean login(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JsonRepository<User> repo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class,
            "src/main/resources/com/groupg/cells2d/model/user/users.json");
        repo.load();
        for (User user : repo.getAll()) {
            if (user.getUsername().equals(username)) {
                return user.getPasswordHash().verifyPassword(password);
            }
        }
        return false;
    }

    /**
     * Retrieves the {@link User} object for the given username from the repository.
     * @param username the login name to look up
     * @return the matching user, or null if not found
     * @throws NoSuchAlgorithmException if PBKDF2 is unavailable
     * @throws InvalidKeySpecException  if the key specification is invalid
     * @throws IOException              if the repository file cannot be read
     */
    public static User get(String username)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JsonRepository<User> repo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class,
            "src/main/resources/com/groupg/cells2d/model/user/users.json");
        repo.load();
        for (User user : repo.getAll()) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }
}
