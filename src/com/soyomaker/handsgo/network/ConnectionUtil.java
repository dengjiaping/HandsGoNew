package com.soyomaker.handsgo.network;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.text.TextUtils;

import com.soyomaker.handsgo.HandsGoApplication;

/**
 * 网络连接状态管理
 * 
 * @author MaXingliang
 */
public class ConnectionUtil {

    private static ConnectivityManager sManager = (ConnectivityManager) HandsGoApplication
            .getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

    /**
     * 是否已连接上网络
     * 
     * @return
     */
    public static boolean hasInternet() {
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 使用的网络类型（wifi或mobile）
     * 
     * @return 使用的网络类型：ConnectivityManager.TYPE_*
     */
    public static int getConnectionType() {
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null) {
            return info.getType();
        }
        return -1;
    }

    /**
     * 获取网络状态
     * 
     * @return 网络状态：State.*
     */
    public static State getConnectionState() {
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null) {
            return info.getState();
        }
        return State.UNKNOWN;
    }

    public static boolean isWifiActive() {
        boolean flag = false;
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null) {
            flag = (info.getType() == ConnectivityManager.TYPE_WIFI);
        }
        return flag;
    }

    public static boolean isWifiConnect() {
        boolean flag = false;
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            flag = (info.getType() == ConnectivityManager.TYPE_WIFI);
        }
        return flag;
    }

    public static boolean isMobileActive() {
        boolean flag = false;
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null) {
            flag = (info.getType() == ConnectivityManager.TYPE_MOBILE);
        }
        return flag;
    }

    public static boolean isMobileConnect() {
        boolean flag = false;
        NetworkInfo info = sManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            flag = (info.getType() == ConnectivityManager.TYPE_MOBILE);
        }
        return flag;
    }

    /**
     * 网络连接是否已连接好
     * 
     * @return
     */
    public static boolean isConnected() {
        return State.CONNECTED.equals(getConnectionState());
    }

    /**
     * 网络连接是否正在准备
     * 
     * @return
     */
    public static boolean isPreparing() {
        return State.CONNECTING.equals(getConnectionState());
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
