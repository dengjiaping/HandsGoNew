package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sina.sae.cloudservice.callback.QueryCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

public class CloudLogin {

    public static List<Map<String, String>> login(String name, String password)
            throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("password", password);
        JsonObject json = CloudClient.get(CloudClient.REST_LOGIN, params, null);
        if (json != null) {
            int code = json.get("code").getAsInt();
            String message = json.get("message").getAsString();
            if (0 == code && "success".equalsIgnoreCase(message)) {
                JsonElement data = json.get("data");
                Gson gson = new Gson();
                return gson.fromJson(data, new TypeToken<List<Map<String, String>>>() {
                }.getType());
            } else {
                String errorMessage = "CloudLogin.login(" + name + "," + password
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        } else {
            return null;
        }
    }

    public static <T> void login(String name, String password, QueryCallback callback)
            throws CloudServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", name);
        params.put("password", password);
        CloudClient.get(CloudClient.REST_LOGIN, params, callback);
    }
}
