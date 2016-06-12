package com.binhnv.hdsplash.other;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by sev_user on 1/27/2016.
 */
public class MyDeserializer<T> implements JsonDeserializer<T> {
    /*@Override
    public Link deserialize(JsonElement je, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonElement link = je.getAsJsonObject().get("links");
        return new Gson().fromJson(link, Link.class);

    }*/
    @Override
    public T deserialize(JsonElement je, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonElement content = je.getAsJsonObject();
        return new Gson().fromJson(content, type);

    }
}