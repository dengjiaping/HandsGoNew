package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.callback.ExecuteCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

public class CloudRegister {

	public static int register(String name, String password, String deviceId, String email,
			String gender) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("password", password);
		params.put("deviceid", deviceId);
		params.put("email", email);
		params.put("gender", gender);
		JsonObject json = CloudClient.post(CloudClient.REST_REGISTER, params, null);
        if (json != null) {
		    int code = json.get("code").getAsInt();
	        String message = json.get("message").getAsString();
	        if (0 == code && "success".equalsIgnoreCase(message)) {
	            JsonObject data = json.get("data").getAsJsonObject();
	            return data.get("rows").getAsInt();
	        } else {
	            String errorMessage = "CloudRegister.register(" + name + "," + password + ","
	                    + deviceId + "," + email + "," + gender + ") Error!Code: " + code + " message:"
	                    + message;
	            Log.e("CloudService", errorMessage);
	            throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
	        }
		}else{
		    return -1;
		}
	}

	public static void register(String name, String password, String deviceId, String email,
			String gender, ExecuteCallback callback) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("password", password);
		params.put("deviceid", deviceId);
		params.put("email", email);
		params.put("gender", gender);
		CloudClient.post(CloudClient.REST_REGISTER, params, callback);
	}
}
