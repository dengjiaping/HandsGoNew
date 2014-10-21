package com.sina.sae.cloudservice.callback;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.exception.CloudServiceException;

/**
 * 所有回调实现的接口
 * 
 * @author zhiyun
 */
public interface ActionCallback {

    public void done(JsonObject returnValue, CloudServiceException e);

}
