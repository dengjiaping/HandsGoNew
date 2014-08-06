package com.soyomaker.handsgonew;

import android.app.Application;
import android.content.Context;

public class HandsGoApplication extends Application {

	private static Context sContext;

	@Override
	public void onCreate() {
		super.onCreate();
		HandsGoApplication.sContext = this;
	}

	public static Context getAppContext() {
		return sContext;
	}
}
