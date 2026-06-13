package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.AppConfig;
import com.groupg.cells2d.data.JsonRepository;

import java.io.IOException;
import java.util.List;

/**
 * Manual smoke test for the {@link User} login flow and JSON repository.
 * Not part of the automated test suite.
 */
public class UserTest {


    /**
     * Runs the manual user-login smoke test.
     * @param args unused
     * @throws Exception if any step of the login or repository flow fails
     * @throws IOException if the user repository cannot be read
     */
    public static void main(String[] args) throws Exception, IOException {
//        JsonRepository<User> userRepo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class, "src/main/java/com/groupg/cells2d/model/user/data/users.json");
//        userRepo.add(new Doctor(1, "Jean", "bruh123", "paris", "generaliste"));
//        userRepo.save();
        //System.out.println(User.login("Jean","bruh123"));
        //JsonRepository<User> userRepo = new JsonRepository<>(AppConfig.GSON_MANAGER, User.class, "src/main/java/com/groupg/cells2d/model/user/data/users.json");
        //userRepo.add(new Doctor(1, "Docteur", "test1", "paris", "generaliste"));
        //userRepo.add(new Researcher(2,"Chercheur","test2","Cochin"));
        // userRepo.save();




    }
}

