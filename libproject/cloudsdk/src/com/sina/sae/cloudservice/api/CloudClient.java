package com.sina.sae.cloudservice.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sina.sae.cloudservice.callback.ActionCallback;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.sina.sae.cloudservice.util.SSLSocketFactoryEx;

/**
 * 在使用sdk各接口前需要调用CloudClient类的init方法进行初始化
 * 方法参数包含sae平台应用对应信息，包括应用名称appname，应用ak，应用sk的md5形式。 sdk将根据这些数据作为和服务端交互的基础
 * 
 * @author zhiyun
 */
public class CloudClient {

    /**
     * 以下为各rest接口地址
     */
    public static String REST_TOKEN;
    public static String REST_OBJECT;
    public static String REST_DB;
    public static String REST_FILE;
    public static String REST_MAIL;
    public static String REST_LOGIN;
    public static String REST_REGISTER;
    public static String REST_GET_COMMENTS;
    public static String REST_COMMENT;

    /**
     * 以下为SAE应用相关信息
     */
    public static String APPNAME;
    public static String ACCESSKEY;
    public static String SECRETKEY;

    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;

    public static HttpClient httpclient;

    public volatile static String token = null;
    private static long tokenTime = 0L;// 获取token时间
    public static Context context;

    /**
     * 使用API之前需要调用此方法，网络通畅时做此操作
     * 
     * @param context
     *            安卓中的Context对象
     * @param appname
     *            对应SAE后端的应用名称
     * @param ak
     *            对应SAE后端应用的accesskey
     * @param sk
     *            对应SAE后端应用的secretkey
     * @throws CloudServiceException
     *             抛出的异常，可根据code来确定异常类型
     */
    public synchronized static void init(Context context, String appname, String ak, String sk)
            throws CloudServiceException {
        if (!checkNetwork(context)) {// 网络不通直接抛异常
            throw new CloudServiceException("Network Error", CloudServiceException.NETWORK_ERROR);
        }

        // 初始化SAE应用相关信息
        APPNAME = appname;
        ACCESSKEY = ak;
        SECRETKEY = sk;

        // 初始化rest接口URL
        REST_TOKEN = "https://" + appname + ".sinaapp.com/api/token";
        REST_OBJECT = "http://" + appname + ".sinaapp.com/api/object";
        REST_DB = "http://" + appname + ".sinaapp.com/api/db";
        REST_FILE = "http://" + appname + ".sinaapp.com/api/file";
        REST_MAIL = "http://" + appname + ".sinaapp.com/api/mail";
        REST_LOGIN = "http://" + appname + ".sinaapp.com/api/login";
        REST_REGISTER = "http://" + appname + ".sinaapp.com/api/register";
        REST_GET_COMMENTS = "http://" + appname + ".sinaapp.com/api/getComments";
        REST_COMMENT = "http://" + appname + ".sinaapp.com/api/comment";

        // 初始化context
        CloudClient.context = context;

        // 初始化HttpClient
        initHttpClient();

        // token为空则生成token
        if (null == token)
            obtainToken();
    }

    /**
     * init操作时初始化HttpClient
     * 
     * @throws CloudServiceException
     */
    private static void initHttpClient() throws CloudServiceException {
        if (httpclient == null) {
            String errorMessage = null;
            try {
                HttpParams params = new BasicHttpParams();
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
                HttpProtocolParams.setUseExpectContinue(params, true);
                HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);
                // Https 相关
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),
                        80));
                schemeRegistry.register(new Scheme("https", sf, 443));
                // 线程安全HttpClient
                ThreadSafeClientConnManager tsccm = new ThreadSafeClientConnManager(params,
                        schemeRegistry);
                httpclient = new DefaultHttpClient(tsccm, params);
            } catch (KeyManagementException e) {
                errorMessage = "CloudClient.initHttpClient() error! KeyManagementException:"
                        + e.getMessage();
            } catch (UnrecoverableKeyException e) {
                errorMessage = "CloudClient.initHttpClient() error! UnrecoverableKeyException:"
                        + e.getMessage();
            } catch (KeyStoreException e) {
                errorMessage = "CloudClient.initHttpClient() error! KeyStoreException:"
                        + e.getMessage();
            } catch (NoSuchAlgorithmException e) {
                errorMessage = "CloudClient.initHttpClient() error! NoSuchAlgorithmException:"
                        + e.getMessage();
            } catch (CertificateException e) {
                errorMessage = "CloudClient.initHttpClient() error! CertificateException:"
                        + e.getMessage();
            } catch (IOException e) {
                errorMessage = "CloudClient.initHttpClient() error! IOException:" + e.getMessage();
            }
            if (null != errorMessage) {
                Log.e("CloudService", errorMessage);
                throw new CloudServiceException(errorMessage, CloudServiceException.CLIENT_ERROR);
            }
        }
    }

    /**
     * 检测当前设备网络是否可用
     * 
     * @return 可用返回true 否则返回false
     */
    public static boolean checkNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != manager && null != manager.getActiveNetworkInfo()
                && manager.getActiveNetworkInfo().isAvailable()) {
            return true;
        }
        return false;
    }

    /**
     * 发送post请求，如果callback为null使用同步方式请求，否则使用异步方式
     * 
     * @param url
     *            请求的url
     * @param params
     *            请求的参数
     * @param callback
     *            请求后的回调
     */
    public static JsonObject post(String url, Map<String, String> params, ActionCallback callback)
            throws CloudServiceException {
        HttpPost httpPost = new HttpPost(url);
        if (null != params && params.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            Set<String> keys = params.keySet();
            for (String key : keys) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new CloudServiceException("CloudClient.post(" + url + "," + params
                        + ") Error! UnsupportedEncodingException:" + e.getMessage(),
                        CloudServiceException.CLIENT_ERROR);
            }
        }
        return service(httpPost, callback);
    }

    /**
     * 发送get请求，如果callback为null使用同步方式请求，否则使用异步方式
     * 
     * @param url
     *            请求的url
     * @param params
     *            请求的参数
     * @param callback
     *            请求的回调
     */
    public static JsonObject get(String url, Map<String, String> params, ActionCallback callback)
            throws CloudServiceException {
        if (null != params && params.size() > 0) {
            StringBuilder paramsStr = new StringBuilder("?");
            Set<String> keys = params.keySet();
            try {
                for (String key : keys) {
                    paramsStr.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), "utf-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                throw new CloudServiceException("CloudClient.get(" + url + "," + params
                        + ") Error! UnsupportedEncodingException:" + e.getMessage(),
                        CloudServiceException.CLIENT_ERROR);
            }
            paramsStr.deleteCharAt(paramsStr.length() - 1);
            url = url + paramsStr.toString();
        }
        HttpGet httpGet = new HttpGet(url);
        return service(httpGet, callback);
    }

    /**
     * 发送delete请求，如果callback为null使用同步方式请求，否则使用异步方式
     * 
     * @param url
     *            请求的url
     * @param params
     *            请求的参数
     * @param callback
     *            请求的回调
     */
    public static JsonObject delete(String url, Map<String, String> params, ActionCallback callback)
            throws CloudServiceException {
        if (null != params && params.size() > 0) {
            StringBuilder paramsStr = new StringBuilder("?");
            Set<String> keys = params.keySet();
            try {
                for (String key : keys) {
                    paramsStr.append(key).append("=")
                            .append(URLEncoder.encode(params.get(key), "utf-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                throw new CloudServiceException("CloudClient.delete(" + url + "," + params
                        + ") Error! UnsupportedEncodingException:" + e.getMessage(),
                        CloudServiceException.CLIENT_ERROR);
            }
            paramsStr.deleteCharAt(paramsStr.length() - 1);
            url = url + paramsStr.toString();
        }
        HttpDelete httpGet = new HttpDelete(url);
        return service(httpGet, callback);
    }

    /**
     * 上传文件，如果callback为null使用同步方式请求，否则使用异步方式
     * 
     * @param url
     *            对应上传文件rest的url
     * @param filepath
     *            本地文件路径
     * @param cloudpath
     *            云端文件路径
     * @param callback
     *            回调
     * @return 返回服务端数据的json对象
     */
    public static JsonObject upload(String url, String filepath, String cloudpath,
            ActionCallback callback) throws CloudServiceException {
        File file = new File(filepath);
        if (!file.exists()) {// 验证本地文件是否存在
            callback.done(null, new CloudServiceException("CloudClient.upload(" + url + ","
                    + filepath + "," + cloudpath + ") error!File  doesn't exitst!",
                    CloudServiceException.CLIENT_ERROR));
            return null;
        }
        HttpPost httppost = new HttpPost(url);
        MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart(cloudpath, cbFile); // <input type="file"
                                             // name="cloudpath" /> 对应
        httppost.setEntity(mpEntity);
        return service(httppost, callback);
    }

    /**
     * 发送HTTP请求，本方法主要区分同步和异步(根据callback是否为null来区分)
     * 
     * @param request
     *            请求
     * @param callback
     *            请求的回调
     * @return 返回服务端数据的json对象
     */
    private static JsonObject service(final HttpUriRequest request, final ActionCallback callback)
            throws CloudServiceException {
        if (callback == null) {// 回调参数为空时，采用同步方式(当前线程)
            return internalService(request, null);
        } else {// 否则采用回调方式执行(新起线程)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        internalService(request, callback);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return null;
        }
    }

    /**
     * 最终发送HTTP请求并拿将相应返回值发送到回调
     * 
     * @param request
     *            请求
     * @param callback
     *            请求回调
     * @return 返回服务端数据的json对象
     */
    private static JsonObject internalService(HttpUriRequest request, ActionCallback callback)
            throws CloudServiceException {
        if (!checkNetwork(context)) {// 网络不通直接抛异常
            throw new CloudServiceException("Network Error", CloudServiceException.NETWORK_ERROR);
        }
        request.setHeader("token", token);
        String errorMessage = null;
        JsonObject json = null;
        try {
            HttpResponse httpResponse = httpclient.execute(request);
            int httpCode = httpResponse.getStatusLine().getStatusCode();
            if (200 == httpCode) {
                HttpEntity entity = httpResponse.getEntity();
                String entityStr = EntityUtils.toString(entity);
                entity.consumeContent();
                JsonParser parser = new JsonParser();
                json = parser.parse(entityStr).getAsJsonObject();
            } else {
                errorMessage = "CloudClient.internalService(" + request + "," + callback
                        + ") Error!errorMessage:HTTP Reqeust Error!HTTP Code is " + httpCode;// http请求错误
            }
        } catch (ClientProtocolException e) {
            errorMessage = "CloudClient.internalService(" + request + "," + callback
                    + ") Error!errorMessage: ClientProtocolException " + e.getMessage();
        } catch (ParseException e) {
            errorMessage = "CloudClient.internalService(" + request + "," + callback
                    + ") Error!errorMessage:ParseException " + e.getMessage();
        } catch (IOException e) {
            errorMessage = "CloudClient.internalService(" + request + "," + callback
                    + ") Error!errorMessage:IOException " + e.getMessage();
        }
        if (null == callback) {
            return json;
        } else {
            CloudServiceException e = null;
            if (errorMessage != null) {
                Log.e("CloudService", errorMessage);
                e = new CloudServiceException(errorMessage, CloudServiceException.CLIENT_ERROR);
            }
            callback.done(json, e);
            return null;
        }
    }

    /**
     * 请求token的rest接口来取token,获取成功后将重置token获取时间
     */
    public static synchronized String obtainToken() throws CloudServiceException {
        String errorMessage = null;
        // 获取token成功后 30秒内不再获取
        if ((System.currentTimeMillis() - tokenTime) > 1000 * 30) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("accesskey", ACCESSKEY);
            params.put("secretkey", SECRETKEY);
            JsonObject ret = post(REST_TOKEN, params, null);
            if (ret != null) {
                int code = ret.get("code").getAsInt();
                JsonObject data = ret.get("data").getAsJsonObject();
                if (code == 0 && data != null) {
                    token = data.get("token").getAsString();
                    return token;
                } else {
                    errorMessage = "CloudClient.obtainToken() Error! Code is:" + code
                            + " Message is:" + ret.get("message").getAsString();// 获取token失败
                }
            } else {
                errorMessage = "Response error";
            }
        } else {
            errorMessage = "CloudClient.obtainToken() Error! Request Token Too Often!Recently Time is "
                    + new Date(tokenTime);// 获取token时间太频繁
        }
        Log.e("CloudService", errorMessage);
        throw new CloudServiceException(errorMessage, CloudServiceException.CLIENT_ERROR);
    }
}
