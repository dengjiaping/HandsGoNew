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

public class CloudGetComments {

	public static List<Map<String, String>> getComments(String sgfUrl) throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("sgfurl", sgfUrl);
		CommonParams.addCommonParams(params);

		JsonObject json = CloudClient.get(CloudClient.REST_GET_COMMENTS, params, null);
		if (json != null) {
			int code = json.get("code").getAsInt();
			String message = json.get("message").getAsString();
			if (0 == code && "success".equalsIgnoreCase(message)) {
				JsonElement data = json.get("data");
				Gson gson = new Gson();
				return gson.fromJson(data, new TypeToken<List<Map<String, String>>>() {
				}.getType());
			} else {
				String errorMessage = "CloudComment.getComments(" + sgfUrl + ") Error!Code: "
						+ code + " message:" + message;
				Log.e("CloudService", errorMessage);
				throw new CloudServiceException(errorMessage, CloudServiceException.SERVER_ERROR);
			}
		} else {
			return null;
		}
	}

	public static <T> void getComments(String sgfUrl, QueryCallback callback)
			throws CloudServiceException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("sgfurl", sgfUrl);
		CommonParams.addCommonParams(params);

		CloudClient.get(CloudClient.REST_GET_COMMENTS, params, callback);
	}
}
