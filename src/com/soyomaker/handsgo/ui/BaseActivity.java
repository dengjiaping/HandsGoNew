package com.soyomaker.handsgo.ui;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity implements IPageName {

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getPageName());
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getPageName());
		MobclickAgent.onPause(this);
	}
}
