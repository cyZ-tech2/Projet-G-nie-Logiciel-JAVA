package com.groupg.cells2d.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Generic repository that persists a typed {@link java.util.Set} as a JSON file.
 * Delegates serialisation to {@link GsonManager} to preserve polymorphic types.
 * @param <T> element type
 */
public class JsonRepository<T> {

    private final GsonManager gsonManager;
    private final Class<T> baseClass;
    private final Path filePath;
    private final Set<T> data = new HashSet<>();

    /** Returns the path of the backing JSON file. */
    public Path getFilePath(){
        return this.filePath;
    }

    /**
     * Creates a repository backed by the given JSON file.
     * @param gsonManager the Gson manager to use for serialisation
     * @param baseClass   the element type
     * @param filePath    path to the JSON file (created on save if absent)
     */
    public JsonRepository(GsonManager gsonManager, Class<T> baseClass, String filePath) {
        this.gsonManager = gsonManager;
        this.baseClass   = baseClass;
        this.filePath    = Path.of(filePath);
    }
//    public JsonRepository(GsonManager gsonManager, Class<T> baseClass) {
//        this.gsonManager = gsonManager;
//        this.baseClass   = baseClass;
//        this.filePath    = null;
//    }



    /** Adds an item to the in-memory set. */
    public void add(T item)    { this.data.add(item); }
    /** Removes an item from the in-memory set. */
    public void remove(T item) { this.data.remove(item); }
    /** Returns an unmodifiable view of the in-memory set. */
    public Set<T> getAll()     { return Collections.unmodifiableSet(this.data); }

    /**
     * Persists the in-memory set to the configured JSON file.
     * @throws IOException if the file cannot be written
     */
    public void save() throws IOException {
        String json = this.gsonManager.setToJson(data, baseClass);
        Files.writeString(this.filePath, json);
    }

    /**
     * Serialises the current set to a JSON string without writing to disk.
     * @return JSON representation of the set
     * @throws IOException if serialisation fails
     */
    public String getJson() throws IOException {
        return this.gsonManager.setToJson(data,baseClass);

    }

    /**
     * Loads the set from the configured JSON file, replacing the current in-memory data.
     * Does nothing if the file does not exist.
     * @throws IOException if the file cannot be read
     */
    public void load() throws IOException {
        if (!Files.exists(this.filePath)) return;
        String json = Files.readString(this.filePath);
        this.data.clear();
        this.data.addAll(this.gsonManager.jsonToSet(json, this.baseClass));
    }
    /**
     * Loads the set from a raw JSON string, replacing the current in-memory data.
     * @param json the JSON string to deserialise
     * @throws IOException if deserialisation fails
     */
    public void load(String json) throws IOException {
        this.data.clear();
        this.data.addAll(this.gsonManager.jsonToSet(json, this.baseClass));
    }
}
