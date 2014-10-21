package com.sina.sae.cloudservice.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sina.sae.cloudservice.exception.CloudServiceException;

public abstract class QueryCallback implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        if (e == null) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (0 == code && "success".equalsIgnoreCase(message)) {
                JsonElement data = returnValue.get("data");
                Gson gson = new Gson();
                list = gson.fromJson(data, new TypeToken<List<Map<String, String>>>() {
                }.getType());
            } else {
                String errorMessage = "QueryCallback.done(" + returnValue + "," + e
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(list, e);
    }

    /**
     * 从云端数据库查询数据的回调方法，从云端数据库查询到相应数据后会自动将数据转换成对象T
     * 
     * @param list
     *            云端数据库查询到的数据并转换成相应对象list
     * @param e
     *            操作过程中出现的异常
     */
    public abstract void handle(List<Map<String, String>> list, CloudServiceException e);

}
