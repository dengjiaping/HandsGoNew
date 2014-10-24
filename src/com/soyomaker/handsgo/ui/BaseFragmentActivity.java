package com.soyomaker.handsgo.ui;

import android.support.v4.app.FragmentActivity;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragmentActivity extends FragmentActivity implements IPageName {

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
