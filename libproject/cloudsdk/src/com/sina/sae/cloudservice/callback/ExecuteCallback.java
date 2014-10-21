package com.sina.sae.cloudservice.callback;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 执行云端SQL的回调
 * 
 * @author zhiyun
 */
public abstract class ExecuteCallback implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        int row = -1;
        if (e == null) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (0 == code && "success".equalsIgnoreCase(message)) {
                JsonObject data = returnValue.get("data").getAsJsonObject();
                row = data.get("rows").getAsInt();
            } else {
                String errorMessage = "ExecuteCallback.done(" + returnValue + "," + e
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(row, e);
    }

    /**
     * 回调方法，通过Exception判断是否成功，而row为此次操作影响云端数据库的行数
     * 
     * @param row
     *            影响云端数据库的行数
     * @param e
     *            执行过程中出现的异常
     */
    public abstract void handle(int row, CloudServiceException e);

}
