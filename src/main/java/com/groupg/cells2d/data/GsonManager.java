package com.groupg.cells2d.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Configures a Gson instance with polymorphic type adapters
 * and exposes helpers for serialising and deserialising typed sets.
 */
public class GsonManager {

    private final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    private Gson gson;

    /**
     * Registers a polymorphic type hierarchy so Gson can serialise/deserialise
     * concrete sub-types by their string label.
     * @param <T>       base type
     * @param baseClass root of the hierarchy
     * @param subtypes  map of label → concrete sub-type
     * @return this instance for chaining
     */
    public <T> GsonManager registerHierarchy(Class<T> baseClass, Map<String, Class<? extends T>> subtypes) {
        RuntimeTypeAdapterFactory<T> factory = RuntimeTypeAdapterFactory.of(baseClass, "type");
        subtypes.forEach((label, type) -> factory.registerSubtype(type, label));
        builder.registerTypeAdapterFactory(factory);
        this.gson = null; // invalidate cached Gson so the new factory is picked up
        return this;
    }

//    /**
//     * Registers a class without subtypes hierarchy
//     * @param baseClass the class to register
//     * @return
//     * @param <T>
//     */
//    public <T> GsonManager registerHierarchy(Class<T> baseClass) {
//        RuntimeTypeAdapterFactory<T> factory = RuntimeTypeAdapterFactory.of(baseClass, "type");
//        builder.registerTypeAdapterFactory(factory);
//        this.gson = null;
//        return this;
//    }

    /**
     * Returns the configured {@link Gson} instance, building it lazily if needed.
     * @return the shared Gson instance
     */
    public Gson get() {
        if (gson == null) gson = builder.create();
        return gson;
    }

    /**
     * Serialises a set to a JSON string, preserving concrete type information.
     * @param <T>       element type
     * @param set       the set to serialise
     * @param baseClass element base class
     * @return JSON string
     */
    public <T> String setToJson(Set<T> set, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType();
        return get().toJson(set, type);
    }

    /**
     * Deserialises a JSON string into a {@link java.util.HashSet}.
     * @param <T>       element type
     * @param json      JSON string to parse
     * @param baseClass element base class
     * @return populated set
     */
    public <T> Set<T> jsonToSet(String json, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType();
        return get().fromJson(json, type);
    }

}