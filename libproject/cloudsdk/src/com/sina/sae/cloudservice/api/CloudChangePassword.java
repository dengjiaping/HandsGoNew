package com.sina.sae.cloudservice.api;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.google.gson.JsonObject;
import com.sina.sae.cloudservice.callback.ExecuteCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;

public class CloudChangePassword {

	public static int changePassword(String name, String oldpassword, String newpassword)
			throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("oldpassword", oldpassword);
		params.put("newpassword", newpassword);
		CommonParams.addCommonParams(params);

		JsonObject json = CloudClient.post(CloudClient.REST_CHANGE_PASSWORD, params, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				return code;
			} else {
				String errorMessage = "CloudChangePassword.changePassword(" + name + ","
						+ oldpassword + "," + newpassword + ") Error!Code: " + code + " message:"
						+ message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, code);
			}
		} else {
			return -1;
		}
	}

	public static void changePassword(String name, String oldpassword, String newpassword,
			ExecuteCallback callback) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("oldpassword", oldpassword);
		params.put("newpassword", newpassword);
		CommonParams.addCommonParams(params);

		CloudClient.post(CloudClient.REST_CHANGE_PASSWORD, params, callback);
	}
}
