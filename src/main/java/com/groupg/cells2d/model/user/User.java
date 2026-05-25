package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.EncryptionService;
import com.groupg.cells2d.data.PasswordHash;

import java.util.Objects;

/**
 * classe abstraite de nos utilisateurs médecin et/ou chercheur
 */

abstract class User {

     private final double id;
     private final String username;
     private final PasswordHash passwordHash;

    public User(double id, String username, String password) throws Exception{
        this.id = id;
        this.username = username;
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


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Double.compare(id, user.id) == 0 && Objects.equals(username, user.username) && Objects.equals(passwordHash, user.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, passwordHash);
    }

//    public boolean login(String username, String password){
//        return this.username.equals(username) && this.passwordHash.equals(EncryptionService.hashPassword(password));
//
//    }
    //faudrait tirer les les logs de la base de donnée


}
