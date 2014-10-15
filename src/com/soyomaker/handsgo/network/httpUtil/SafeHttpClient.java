package com.soyomaker.handsgo.network.httpUtil;

import java.security.KeyStore;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.network.ConnectionUtil;
import com.soyomaker.handsgo.network.ConnectionUtil.APNWrapper;
import com.soyomaker.handsgo.network.ConnectionUtil.NetworkState;

/**
 * @author Li Wen
 * @ClassName: SafeHttpClient
 * @Description: HTTPclient初始化设置
 * @date 2014-1-2
 */
public class SafeHttpClient extends DefaultHttpClient {

    private static final int TIME_MAX_WAIT_OUT_CONNECTION = 8000;
    private static final int TIME_OUT_CONNECTION = 20000;
    private static final int TIME_OUT_SOCKET = 20000;
    private static final int DEFAULT_MAX_CONNECTIONS = 1000;

    private static final String TAG = "SafeHttpClient";

    public static SafeHttpClient createHttpClient() {
        // sets up parameters
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "utf-8");
        params.setBooleanParameter("http.protocol.expect-continue", false);

        // 从这里开始是进行下载，使用了多线程执行请求
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(100));// 设置并发数

        ConnManagerParams.setMaxTotalConnections(params, DEFAULT_MAX_CONNECTIONS);

        ConnManagerParams.setTimeout(params, TIME_MAX_WAIT_OUT_CONNECTION);
        /* 连接超时 */
        HttpConnectionParams.setConnectionTimeout(params, TIME_OUT_CONNECTION);
        /* 请求超时 */
        HttpConnectionParams.setSoTimeout(params, TIME_OUT_SOCKET);

        // 设置代理
        Context context = HandsGoApplication.getAppContext();

        NetworkState state = ConnectionUtil.getNetworkState(context);

        if (state == NetworkState.MOBILE) {
            APNWrapper wrapper = null;
            wrapper = ConnectionUtil.getAPN(context);
            if (!TextUtils.isEmpty(wrapper.proxy)) {
                ConnRouteParams.setDefaultProxy(params, new HttpHost(wrapper.proxy, wrapper.port));
            }
        }

        // registers schemes for both http and https
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            EasySSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            registry.register(new Scheme("https", sf, 443));
        } catch (Exception e) {
            Log.e(TAG, "https:", e);
        }
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);

        return new SafeHttpClient(manager, params);
    }

    public SafeHttpClient(ThreadSafeClientConnManager manager, HttpParams params) {
        super(manager, params);
    }

}
