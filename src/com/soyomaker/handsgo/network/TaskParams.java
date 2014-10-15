package com.soyomaker.handsgo.network;

import java.util.HashMap;

/**
 * 调用Task可添加的参数
 * 
 * @author Tsimle
 */
public class TaskParams {

    private HashMap<String, Object> params = null;

    public TaskParams() {
        params = new HashMap<String, Object>();
    }

    public TaskParams(String key, Object value) {
        this();
        put(key, value);
    }

    public void put(String key, Object value) {
        params.put(key, value);
    }

    public Object get(String key) {
        return params.get(key);
    }

    public String getString(String key) {
        Object object = get(key);
        return object == null ? null : object.toString();
    }

    public boolean has(String key) {
        return this.params.containsKey(key);
    }
}
