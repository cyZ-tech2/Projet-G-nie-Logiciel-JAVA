package com.groupg.cells2d.data;

import java.io.*;
import java.nio.file.*;
import java.util.*;

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

    public void add(T item)    { data.add(item); }
    public void remove(T item) { data.remove(item); }
    public Set<T> getAll()     { return Collections.unmodifiableSet(data); }

    public void save() throws IOException {
        String json = gsonManager.setToJson(data, baseClass);
        Files.writeString(filePath, json);
    }

    public void load() throws IOException {
        if (!Files.exists(filePath)) return;
        String json = Files.readString(filePath);
        data.clear();
        data.addAll(gsonManager.jsonToSet(json, baseClass));
    }
}
