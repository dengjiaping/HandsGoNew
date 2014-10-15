package com.soyomaker.handsgo.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Proxy;
import android.net.Uri;
import android.text.TextUtils;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.util.LogUtil;

/**
 * http请求相关的工具方法
 * 
 * @author Tsmile
 */
public class HttpUtil {

    private static final String TAG = "HttpUtil";

    private static final int BUFFER_SIZE = 1024 * 1024;

    public static final String BOUNDARY = "7cd4a6d158c";
    public static final String MP_BOUNDARY = "--" + BOUNDARY;
    public static final String END_MP_BOUNDARY = "--" + BOUNDARY + "--";
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public static String inputStream2String(InputStream in) throws IOException {
        if (in == null)
            return "";

        final int size = 128;
        byte[] buffer = new byte[size];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int cnt = 0;
        while ((cnt = in.read(buffer)) > -1) {
            baos.write(buffer, 0, cnt);
        }
        baos.flush();

        in.close();
        baos.close();

        return baos.toString();
    }

    /**
     * 获取网络状态
     * 
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
     * 网络连接是否已连接好
     * 
     * @return
     */
    public static boolean isConnected(Context context) {
        if (context == null) {
            context = HandsGoApplication.getAppContext();
        }
        return State.CONNECTED.equals(getConnectionState(context));
    }

    /**
     * 上传普通的键值对
     * 
     * @param baos
     * @param key
     * @param value
     * @throws IOException
     */
    private static void paramToUpload(OutputStream baos, String key, String value)
            throws IOException {
        StringBuilder temp = new StringBuilder(10);
        temp.setLength(0);
        temp.append(MP_BOUNDARY).append("\r\n");
        temp.append("content-disposition: form-data; name=\"").append(key);
        temp.append("\"\r\n");
        temp.append("Content-Type: text").append("\r\n\r\n");
        temp.append(value).append("\r\n");
        byte[] res = temp.toString().getBytes();
        baos.write(res);
    }

    /**
     * 上传文件
     * 
     * @param out
     * @param imgpath
     * @throws IOException
     */
    private static void imageContentToUpload(OutputStream out, Bitmap imgpath, String bitmapName)
            throws IOException {
        StringBuilder temp = new StringBuilder();

        temp.append(MP_BOUNDARY).append("\r\n");
        temp.append("Content-Disposition: form-data; name=\"").append(bitmapName)
                .append("\"; filename=\"").append("uploadfile").append("\"\r\n");
        String filetype = "image/jpeg";
        temp.append("Content-Type: ").append(filetype).append("\r\n\r\n");
        byte[] res = temp.toString().getBytes();
        out.write(res);
        BufferedInputStream bis = null;
        try {
            imgpath.compress(CompressFormat.JPEG, 100, out);
            out.write("\r\n".getBytes());
        } catch (IOException e) {
            LogUtil.e(TAG, "upload fail:" + e.toString());
        } finally {
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, "upload fail:" + e.toString());
                }
            }
        }

    }

    /**
     * 上传时的结尾标记
     * 
     * @param baos
     * @throws IOException
     */
    private static void writeEndToUpload(OutputStream baos) throws IOException {
        baos.write(("\r\n" + END_MP_BOUNDARY).getBytes());
    }

    /**
     * 使用httpclient进行post请求
     */
    public static HttpResponse doFilePostRequest(HttpClient client, String url,
            List<NameValuePair> postParams, Bitmap image, String bitmapName) throws IOException {
        HttpPost httpPostRequest = new HttpPost(url);
        client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, 60000);
        httpPostRequest.setHeader("Content-Type", MULTIPART_FORM_DATA + "; boundary=" + BOUNDARY);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
        if (null != postParams) {
            for (int i = 0; i < postParams.size(); i++) {
                NameValuePair param = postParams.get(i);
                paramToUpload(baos, param.getName(), param.getValue());
            }
        }
        try {
            imageContentToUpload(baos, image, bitmapName);
        } catch (FileNotFoundException e) {
            LogUtil.e(TAG, "upload fail:" + e.toString());
        }

        writeEndToUpload(baos);
        byte[] mData = baos.toByteArray();
        baos.close();
        ByteArrayEntity formMultiEntity = new ByteArrayEntity(mData);
        httpPostRequest.setEntity(formMultiEntity);
        return client.execute(httpPostRequest);
    }

    public static HttpResponse doPostRequest(HttpClient client, String url,
            List<NameValuePair> postParams) throws IOException {
        HttpPost httpPostRequest = new HttpPost(url);
        httpPostRequest.setHeader("content-type", "application/x-www-form-urlencoded");
        httpPostRequest.setHeader("charset", "UTF-8");
        HttpEntity entity = null;
        if (postParams != null && postParams.size() > 0) {
            entity = new UrlEncodedFormEntity(postParams, HTTP.UTF_8);
            httpPostRequest.setEntity(entity);
        }
        return client.execute(httpPostRequest);
    }

    public static HttpResponse doPostRequest(HttpClient client, String url, String params)
            throws IOException {
        HttpPost httpPostRequest = new HttpPost(url);
        httpPostRequest.setHeader("content-type", "application/x-www-form-urlencoded");
        httpPostRequest.setHeader("charset", "UTF-8");
        if (params != null) {
            HttpEntity entity = new StringEntity(params);
            httpPostRequest.setEntity(entity);
        }
        return client.execute(httpPostRequest);
    }

    /**
     * 使用httpclient进行get请求
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
        httpConnection.setConnectTimeout(20000);
        httpConnection.setReadTimeout(20000);
        httpConnection.setRequestProperty("Accept", "*, */*");
        httpConnection.setRequestProperty("accept-charset", "utf-8");
        httpConnection.setRequestMethod("GET");
        return httpConnection;
    }

    public enum NetworkState {
        NOTHING, MOBILE, WIFI
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

    public static class APNWrapper {
        public String name;
        public String apn;
        public String proxy;
        public int port;

        public String getApn() {
            return apn;
        }

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }

        public String getProxy() {
            return proxy;
        }

        APNWrapper() {
        }

        public String toString() {
            return "{name=" + name + ";apn=" + apn + ";proxy=" + proxy + ";port=" + port + "}";
        }
    }

    public static APNWrapper getAPN(Context ctx) {
        APNWrapper wrapper = new APNWrapper();
        Cursor cursor = null;
        try {
            cursor = ctx.getContentResolver().query(
                    Uri.parse("content://telephony/carriers/preferapn"),
                    new String[] { "name", "apn", "proxy", "port" }, null, null, null);
        } catch (Exception e) {
            // 为了解决在4.2系统上禁止非系统进程获取apn相关信息，会抛出安全异常
            // java.lang.SecurityException: No permission to write APN settings
        }
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.isAfterLast()) {
                wrapper.name = "N/A";
                wrapper.apn = "N/A";
            } else {
                wrapper.name = cursor.getString(0) == null ? "" : cursor.getString(0).trim();
                wrapper.apn = cursor.getString(1) == null ? "" : cursor.getString(1).trim();
            }
            cursor.close();
        } else {
            wrapper.name = "N/A";
            wrapper.apn = "N/A";
        }
        wrapper.proxy = android.net.Proxy.getDefaultHost();
        wrapper.proxy = TextUtils.isEmpty(wrapper.proxy) ? "" : wrapper.proxy;
        wrapper.port = android.net.Proxy.getDefaultPort();
        wrapper.port = wrapper.port > 0 ? wrapper.port : 80;
        return wrapper;
    }
}
