package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.EncryptionService;
import com.groupg.cells2d.data.JsonRepository;
import com.groupg.cells2d.data.PasswordHash;

import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * classe abstraite de nos utilisateurs médecin et/ou chercheur
 */

public abstract class User {

     private final double id;
     private final String username;
     private final PasswordHash passwordHash;


    public User(double id, String username, String password) throws Exception{
        this.id = id;
        this.username = username;
        //verify if not a duplicate
        this.passwordHash = new PasswordHash(password);
    }

    public double getId() {
        return id;
    }

    public String getUsername(){
        return this.username;
    }

    public PasswordHash getPasswordHash(){
        return this.passwordHash;
    }

    public String toString(){
        return this.username +" "+this.id;
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Double.compare(id, user.id) == 0 && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * returns true if username exists and password matches
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static boolean login(String username, String password) throws Exception {
        JsonRepository<User> userRepo = new JsonRepository<User>(AppConfig.GSON_MANAGER,User.class,"src/main/java/com/groupg/cells2d/model/user/data/users.json");
        userRepo.load();
        for(User user : userRepo.getAll()){
            if(user.getUsername().equals(username)){
                return user.getPasswordHash().verifyPassword(password);
            }
        }
        return false;
    }

    public static User get(String username) throws Exception {
        JsonRepository<User> userRepo = new JsonRepository<User>(AppConfig.GSON_MANAGER,User.class,"src/main/java/com/groupg/cells2d/model/user/data/users.json");
        userRepo.load();
        for (User user : userRepo.getAll()){
            if(user.getUsername().equals(username)){
                return user;
            }

        }
        return null;
    }


}
