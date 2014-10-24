package com.soyomaker.handsgo.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;

import android.content.Context;
import android.util.Log;

public class WebUtil {

	public static String getHttpGet(Context context, String url, String charset) {
		Log.e("URL", url);
		String resultString = "";
		HttpClient client = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			client = HttpUtil.getHttpClient(context);
			response = HttpUtil.doGetRequest(client, url);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == HttpStatus.SC_OK || stateCode == HttpStatus.SC_PARTIAL_CONTENT) {
				entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				resultString = StringUtil.inputStream2String(inputStream, charset);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				entity = null;
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return resultString;
	}

	public static String getHttpGet(Context context, String url) {
		return getHttpGet(context, url, "utf-8");
	}
}
