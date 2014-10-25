package com.sina.sae.cloudservice.api;

import java.util.Map;

public class CommonParams {

	private static Map<String, String> sParams;

	public static void setCommonParams(Map<String, String> params) {
		sParams = params;
	}

	public static void addCommonParams(Map<String, String> params) {
		if (sParams != null) {
			params.putAll(sParams);
		}
	}
}
