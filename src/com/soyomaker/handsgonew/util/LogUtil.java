package com.soyomaker.handsgonew.util;

import android.util.Log;

/**
 * 日志类
 * 
 * @author like
 * 
 */
public class LogUtil {

	private static final boolean DEBUG = true;

	public static final void v(String tag, String msg) {
		if (DEBUG) {
			Log.v(tag, msg);
		}
	}

	public static final void d(String tag, String msg) {
		if (DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static final void i(String tag, String msg) {
		if (DEBUG) {
			Log.i(tag, msg);
		}
	}

	public static final void w(String tag, String msg) {
		if (DEBUG) {
			Log.w(tag, msg);
		}
	}

	public static final void e(String tag, String msg) {
		if (DEBUG) {
			Log.e(tag, msg);
		}
	}
}
