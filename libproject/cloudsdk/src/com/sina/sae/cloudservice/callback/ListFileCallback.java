package com.sina.sae.cloudservice.callback;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sina.sae.cloudservice.api.CloudFile;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 从云端获取多个对象(前缀查找)的回调
 * 
 * @author zhiyun
 * @param <T>
 *            云端获取对象的类型(用于转换)
 */
public abstract class ListFileCallback implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        List<CloudFile> list = new ArrayList<CloudFile>();
        if (null == e) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (0 == code && "success".equalsIgnoreCase(message)) {
                JsonElement data = returnValue.get("data");
                Gson gson = new Gson();
                list = gson.fromJson(data, new TypeToken<List<CloudFile>>() {
                }.getType());
            } else {
                String errorMessage = "ListCallback.done(" + returnValue + "," + e
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(list, e);
    }

    /**
     * 从云端获取多个对象(前缀查找)的回调方法，参数中list为从云端获取的对象list，e为操作过程中出现的异常
     * 
     * @param list
     *            云端获取的对象list
     * @param e
     *            操作过程中出现的异常
     */
    public abstract void handle(List<CloudFile> list, CloudServiceException e);

}
