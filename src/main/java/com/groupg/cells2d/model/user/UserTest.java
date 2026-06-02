package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;

import java.io.IOException;

public class UserTest {


    public static void main(String[] args) throws Exception, IOException {
       JsonRepository<User>    userRepo    = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class,    "users.json");
//        userRepo.add(new Doctor(1,  "Jean", "bruh123", "paris","generaliste"));
//        userRepo.save();
        userRepo.load();
        for(User u : userRepo.getAll()){
            System.out.println(u.toString());
        }
    }
}
