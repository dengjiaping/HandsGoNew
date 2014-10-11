/*
 * 
 */
package com.soyomaker.handsgo.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParamBean;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;

/**
 * http请求相关的工具方法.
 * 
 * @author Tsmile
 */
public class HttpUtil {

	private static final int TIME_OUT_CONNECTION = 6000;

	private static final int TIME_OUT_SOCKET = 20000;

	/**
	 * 获取网络状态.
	 * 
	 * @param context
	 *            the context
	 * @return 网络状态：State.*
	 */
	public static State getConnectionState(Context context) {
		ConnectivityManager sManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = sManager.getActiveNetworkInfo();
		if (info != null) {
			return info.getState();
		}
		return State.UNKNOWN;
	}

	/**
	 * 网络连接是否已连接好.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is connected
	 */
	public static boolean isConnected(Context context) {
		return State.CONNECTED.equals(getConnectionState(context));
	}

	/**
	 * 获取httpclient进行网络请求.
	 * 
	 * @param context
	 *            the context
	 * @return the http client
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static HttpClient getHttpClient(Context context) throws IOException {
		NetworkState state = getNetworkState(context);
		HttpClient client = new DefaultHttpClient();
		if (state == NetworkState.NOTHING) {
			throw new IOException("NoSignalException");
		}

		HttpConnectionParamBean paramHelper = new HttpConnectionParamBean(client.getParams());
		paramHelper.setSoTimeout(TIME_OUT_CONNECTION);
		paramHelper.setConnectionTimeout(TIME_OUT_SOCKET);
		return client;
	}

	/**
	 * 使用httpclient进行post请求.
	 * 
	 * @param client
	 *            the client
	 * @param url
	 *            the url
	 * @param postParams
	 *            the post params
	 * @return the http response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static HttpResponse doPostRequest(HttpClient client, String url,
			List<NameValuePair> postParams) throws IOException {
		HttpPost httpPostRequest = new HttpPost(url);
		httpPostRequest.setHeader("content-type", "application/x-www-form-urlencoded");
		httpPostRequest.setHeader("charset", "UTF-8");
		if (postParams != null && postParams.size() > 0) {
			StringBuilder sbParam = new StringBuilder();
			for (int i = 0; i < postParams.size(); i++) {
				NameValuePair param = postParams.get(i);
				if (i != 0) {
					sbParam.append("&");
				}
				sbParam.append(param.getName()).append("=")
						.append(URLEncoder.encode(param.getValue(), "utf-8"));
			}
			byte[] bytes = sbParam.toString().getBytes("UTF-8");
			ByteArrayEntity formEntity = new ByteArrayEntity(bytes);
			httpPostRequest.setEntity(formEntity);
		}
		return client.execute(httpPostRequest);
	}

	/**
	 * 使用httpclient进行get请求.
	 * 
	 * @param client
	 *            the client
	 * @param url
	 *            the url
	 * @return the http response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static HttpResponse doGetRequest(HttpClient client, String url) throws IOException {
		HttpGet httpGetRequest = new HttpGet(url);
		return client.execute(httpGetRequest);
	}

	public static HttpURLConnection getHttpUrlConnection(URL url, Context context)
			throws ProtocolException, IOException {
		// lyang add
		HttpURLConnection httpConnection;
		if (isWapNet(context)) {// wap 网络
			String tempUrl = url.toString();
			int offset = tempUrl.startsWith("https") ? 8 : 7;
			if (offset == 7) {// http开头的
				int contentBeginIdx = tempUrl.indexOf('/', offset);
				StringBuffer urlStringBuffer = new StringBuffer("http://10.0.0.172");
				urlStringBuffer.append(tempUrl.substring(contentBeginIdx));
				URL urltemp = new URL(urlStringBuffer.toString());
				httpConnection = (HttpURLConnection) urltemp.openConnection();
				httpConnection.setRequestProperty("X-Online-Host",
						tempUrl.substring(offset, contentBeginIdx));
				// Log.e("net ", "wap");
			} else {// wap 网络 访问https
				httpConnection = (HttpURLConnection) url.openConnection();
			}
		} else {
			String[] hostAndPort = getProxyHostAndPort(context);
			String host = hostAndPort[0];
			int port = Integer.parseInt(hostAndPort[1]);

			if (host != null && host.length() != 0 && port != -1) {// 电信wap
																	// 普通移动net网络
				InetSocketAddress isa = new InetSocketAddress(host, port);
				java.net.Proxy proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, isa);
				httpConnection = (HttpURLConnection) url.openConnection(proxy);
			} else {// wifi 网络
				httpConnection = (HttpURLConnection) url.openConnection();
			}
		}

		httpConnection.setDoInput(true);
		httpConnection.setConnectTimeout(30000);
		httpConnection.setReadTimeout(30000);
		httpConnection.setRequestProperty("Accept", "*, */*");
		httpConnection.setRequestProperty("accept-charset", "utf-8");
		httpConnection.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:11.0) Gecko/20100101 Firefox/11.0");
		httpConnection.setRequestMethod("GET");
		return httpConnection;
	}

	public enum NetworkState {

		/** The nothing. */
		NOTHING,
		/** The mobile. */
		MOBILE,
		/** The wifi. */
		WIFI
	}

	public static NetworkState getNetworkState(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return NetworkState.NOTHING;
		} else {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				return NetworkState.MOBILE;
			} else {
				return NetworkState.WIFI;
			}
		}
	}

	public static String[] getProxyHostAndPort(Context context) {
		if (getNetworkState(context) == NetworkState.WIFI) {
			return new String[] { "", "-1" };
		} else {
			return new String[] { Proxy.getDefaultHost(), "" + Proxy.getDefaultPort() };
		}
	}

	public static boolean isWapNet(Context context) {
		String currentAPN = "";
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conManager.getActiveNetworkInfo();
		if (info == null || !info.isAvailable()) {
			return false;
		}
		if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			return false;
		}
		currentAPN = info.getExtraInfo();
		if (currentAPN == null || currentAPN.equals("")) {
			return false;
		} else {
			if (currentAPN.equals("cmwap") || currentAPN.equals("uniwap")
					|| currentAPN.equals("3gwap")) {

				return true;
			} else {
				return false;
			}
		}
	}

}
