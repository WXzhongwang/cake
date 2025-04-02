package com.rany.framework.config.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author zhongshengwang
 * @version 1.0
 * @date 2025/2/5 17:01
 * @slogon 找到银弹
 */
public class PatchUtils {
    public static JsonObject getJson(String key, JsonElement value) {
        JsonObject jo = new JsonObject();
        jo.addProperty("op", "replace");
        jo.addProperty("path", key);
        jo.add("value", value);
        return jo;
    }

    public static JsonObject getAddJson(String key, JsonElement value) {
        JsonObject jo = new JsonObject();
        jo.addProperty("op", "add");
        jo.addProperty("path", key);
        jo.add("value", value);
        return jo;
    }

    public static JsonObject getDelJson(String key) {
        JsonObject jo = new JsonObject();
        jo.addProperty("op", "remove");
        jo.addProperty("path", key);
        return jo;
    }
}
