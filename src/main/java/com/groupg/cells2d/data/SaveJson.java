package com.groupg.cells2d.data;
import java.io.*;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.groupg.cells2d.model.user.Doctor;
import com.groupg.cells2d.model.user.User;

import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;


public class SaveJson {
    public static void save(Object obj,String path) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path filePath = Paths.get(path);
    try{
        if(filePath.getParent()!=null) {
            Files.createDirectories(filePath.getParent());
        }
        try (Writer writer = new FileWriter(path)) {
            gson.toJson(obj, writer);

        }
        System.out.println("Data successfully saved");
    }
        catch(IOException e){ //pretty sure there are some unhandled exceptions here
            System.err.println("File writing error:"+" "+e.getMessage());
        }
    }

    public static HashSet<User> loadUsers(String path) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<HashSet<User>>() {}.getType();

        try (Reader reader = Files.newBufferedReader(Paths.get(path))) {
            return gson.fromJson(reader, type);
        }
    }

        
    }

