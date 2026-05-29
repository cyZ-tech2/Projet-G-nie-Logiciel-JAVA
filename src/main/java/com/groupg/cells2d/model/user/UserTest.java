package com.groupg.cells2d.model.user;

import com.groupg.cells2d.data.SaveJson;

import javax.print.Doc;
import java.util.HashSet;
import java.util.Scanner;

public class UserTest {


    public static void main(String[] args) throws Exception {
         HashSet<User> userSet = new HashSet<User>();

         userSet =(HashSet<User>) SaveJson.loadUsers("data/users.json");
        System.out.println(userSet.toString());


    }
}
