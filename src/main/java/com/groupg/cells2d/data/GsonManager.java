package com.groupg.cells2d.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Handles Serialization and deserialization of each class
 */
public class GsonManager {

    private final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    private Gson gson;

    /**
     * Registers Hierarchy between a class and its childs
     * @param baseClass highest class in the hierarchy
     * @param subtypes Maps a name with a class inheriting from T
     * @return
     * @param <T>
     *
     */
    public <T> GsonManager registerHierarchy(Class<T> baseClass, Map<String, Class<? extends T>> subtypes) {
        RuntimeTypeAdapterFactory<T> factory = RuntimeTypeAdapterFactory.of(baseClass, "type"); //creates an adapter for the base class
        subtypes.forEach((label, type) -> factory.registerSubtype(type, label)); //takes all the subtypes and adds it to the factory
        builder.registerTypeAdapterFactory(factory); // registers the factory to the builder, so it can be used to serialize or deserialize
        this.gson = null; //  resets gson cache
        return this;
    }

    public Gson get() {
        if (gson == null) gson = builder.create();
        return gson;
    }

    /**
     * Serializes a Set to Json
     * @param set
     * @param baseClass
     * @return
     * @param <T>
     */
    public <T> String setToJson(Set<T> set, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType(); //Saves the type of the set
        return get().toJson(set, type);
    }

    /**
     * Deserializes a json to a Hashsetx
     * @param json
     * @param baseClass
     * @return
     * @param <T>
     */
    public <T> Set<T> jsonToSet(String json, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType();
        return get().fromJson(json, type);
    }

}