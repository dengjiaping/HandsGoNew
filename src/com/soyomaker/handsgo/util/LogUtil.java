package com.soyomaker.handsgo.util;

import android.util.Log;

/**
 * 日志类
 * 
 * @author like
 * 
 */
public class LogUtil {

    public static final void v(String tag, String msg) {
        if (AppConstants.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static final void d(String tag, String msg) {
        if (AppConstants.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static final void i(String tag, String msg) {
        if (AppConstants.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static final void w(String tag, String msg) {
        if (AppConstants.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static final void e(String tag, String msg) {
        if (AppConstants.DEBUG) {
            Log.e(tag, msg);
        }
    }
}
