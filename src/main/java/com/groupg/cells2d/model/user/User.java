package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.data.PasswordHash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

public abstract class User {

    private final double       id;
    private final String       username;
    private final PasswordHash passwordHash;

    public User(double id, String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.id           = id;
        this.username     = username;
        this.passwordHash = new PasswordHash(password);
    }

    public double       getId()           { return id; }
    public String       getUsername()     { return username; }
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

    public static boolean login(String username, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JsonRepository<User> repo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class,
            "src/main/java/com/groupg/cells2d/model/user/data/users.json");
        repo.load();
        for (User user : repo.getAll()) {
            if (user.getUsername().equals(username)) {
                return user.getPasswordHash().verifyPassword(password);
            }
        }
        return false;
    }

    public static User get(String username)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        JsonRepository<User> repo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class,
            "src/main/java/com/groupg/cells2d/model/user/data/users.json");
        repo.load();
        for (User user : repo.getAll()) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }
}
