package com.groupg.cells2d.data;
import java.io.IOException;
import java.io.Writer;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.io.FileWriter;


public class SaveJson {
    public static void save(Object obj,String path){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter(path)){
            gson.toJson(obj,writer);
            System.out.println("Data successfully saved");
        }
        catch(IOException e){ //pretty sure there is some unhandled exceptions here
            System.err.println("File writing error");
        }
    }
}
