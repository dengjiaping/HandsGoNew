package com.sina.sae.cloudservice.callback;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 删除数据的回调
 * 
 * @author zhiyun
 */
public abstract class DeleteCallback implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        if (e == null) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (code != 0 || !"success".equalsIgnoreCase(message)) {
                String errorMessage = "DeleteCallback.done(" + returnValue + ",e) Error!Code: "
                        + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(e);
    }

    /**
     * 方法参数的Exception表示操作过程中出现的异常，如果Exception未null则表示成功
     * 
     * @param e
     *            操作过程中出现的异常
     */
    protected abstract void handle(CloudServiceException e);
}
