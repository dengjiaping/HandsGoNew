package com.sina.sae.cloudservice.callback;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 发送邮件的回调
 * 
 * @author zhiyun
 */
public abstract class SendCallback implements ActionCallback {

    @Override
    public void done(JsonObject returnValue, CloudServiceException e) {
        if (e == null) {
            int code = returnValue.get("code").getAsInt();
            String message = returnValue.get("message").getAsString();
            if (code != 0 || !"success".equalsIgnoreCase(message)) {
                String errorMessage = "SendCallback.done(" + returnValue + "," + e
                        + ") Error!Code: " + code + " message:" + message;
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
            }
        }
        handle(e);
    }

    /**
     * 发送邮件的回调方法
     * 
     * @param e
     *            操作过程中出现的异常
     */
    public abstract void handle(CloudServiceException e);

}
