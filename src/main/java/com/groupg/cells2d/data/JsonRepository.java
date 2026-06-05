package com.groupg.cells2d.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Stores a set and add serialization and deserialization functionalities while keeping types
 * @param <T>
 */
public class JsonRepository<T> {

    private final GsonManager gsonManager;
    private final Class<T> baseClass;
    private final Path filePath;
    private final Set<T> data = new HashSet<>();

    public JsonRepository(GsonManager gsonManager, Class<T> baseClass, String filePath) {
        this.gsonManager = gsonManager;
        this.baseClass   = baseClass;
        this.filePath    = Path.of(filePath);
    }

    public void add(T item)    { this.data.add(item); }
    public void remove(T item) { this.data.remove(item); }
    public Set<T> getAll()     { return Collections.unmodifiableSet(this.data); }

    /**
     * Saves Set into a Json file
     * @throws IOException
     */
    public void save() throws IOException {
        String json = this.gsonManager.setToJson(data, baseClass);
        Files.writeString(this.filePath, json);
    }

    /**
     * Loads Set from a Json file
     * @throws IOException
     */
    public void load() throws IOException {
        if (!Files.exists(this.filePath)) return;
        String json = Files.readString(this.filePath);
        this.data.clear();
        this.data.addAll(this.gsonManager.jsonToSet(json, this.baseClass));
    }
}
