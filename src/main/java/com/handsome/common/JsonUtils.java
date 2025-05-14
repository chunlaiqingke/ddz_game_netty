package com.handsome.common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {

	private static final Gson GSON = new Gson();

	private static final JsonParser JSON_PARSER = new JsonParser();

	public static String toJson(Object o){
		return GSON.toJson(o);
	}

	public static <T> T fromJson(String json, Class<T> clazz){
		return GSON.fromJson(json, clazz);
	}

	public static JsonObject parseString(String json){
		return JsonParser.parseString(json).getAsJsonObject();
	}
}