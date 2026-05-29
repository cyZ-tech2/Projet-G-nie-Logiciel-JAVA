package com.groupg.cells2d.model.user;

import java.nio.file.Path;
import java.util.HashSet;

public class UserSet {
   private HashSet<User> userSet;


    public UserSet(){
        this.userSet = new HashSet<User>();
    }

    public HashSet<User> getUserSet() {
        return userSet;
    }
    public void setUserSet(HashSet<User> userSet){
        this.userSet = userSet;
    }




//    public void add(Path userData){
//
//    }
}
