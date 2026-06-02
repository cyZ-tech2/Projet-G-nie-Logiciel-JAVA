package com.groupg.cells2d.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.*;

public class SaveJson {
    private final GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    private Gson gson;
    public <T> SaveJson registerHierarchy(Class<T> baseClass, Map<String, Class<? extends T>> subtypes) {
        RuntimeTypeAdapterFactory<T> factory = RuntimeTypeAdapterFactory.of(baseClass, "type");
        subtypes.forEach((label, type) -> factory.registerSubtype(type, label));
        builder.registerTypeAdapterFactory(factory);
        gson = null; // invalide le cache
        return this;
    }

    public Gson get() {
        if (gson == null) gson = builder.create();
        return gson;
    }
    // Serializes a Set to Json
    public <T> String setToJson(Set<T> set, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType();
        return get().toJson(set, type);
    }

    // Deserializes a json to a Hashsetx
    public <T> Set<T> jsonToSet(String json, Class<T> baseClass) {
        Type type = TypeToken.getParameterized(HashSet.class, baseClass).getType();
        return get().fromJson(json, type);
    }

}