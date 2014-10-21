package com.sina.sae.cloudservice.callback;

import java.lang.reflect.ParameterizedType;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 从云端获取单个对象的回调
 * 
 * @author zhiyun
 * @param <T>
 *            云端获取对象的类型(用于转换)
 */
public abstract class GetCallback<T> implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        T object = null;
        if (null == e) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (0 == code && "success".equalsIgnoreCase(message)) {
                JsonElement data = returnValue.get("data");
                Gson gson = new Gson();
                // 取得T的class类型
                Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0];
                object = (T) gson.fromJson(data, entityClass);
            } else {
                String errorMessage = "GetCallback.done(" + returnValue + "," + e
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(object, e);
    }

    /**
     * 云端获取对象的回调方法，参数中object为从云端获取的对象，e为操作过程中出现的异常
     * 
     * @param object
     *            云端获取的对象
     * @param e
     *            操作过程中出现的异常
     */
    public abstract void handle(T object, CloudServiceException e);

}
