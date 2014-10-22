package com.soyomaker.handsgo.ui;

import ad.soyomaker.handsgo.splash.HandsgoSplash;
import ad.soyomaker.handsgo.splash.HandsgoSplashListener;
import ad.soyomaker.handsgo.util.HandsgoSplashMode;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.LogUtil;

public class SplashActivity extends BaseActivity {

	private static final String TAG = "SplashActivity";

	private static final String SHORTCUT_INTENT = "com.android.launcher.action.INSTALL_SHORTCUT";

	private static final int MIN_DELAY_TIME = 5000;

	private Handler mMainHandler = new Handler();

	private boolean mAdHasLoad = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 不显示系统的标题栏
		setContentView(R.layout.activity_splash);

		mMainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (!mAdHasLoad) {
					launchMainActivity();
				}
			}
		}, MIN_DELAY_TIME);
		initData();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true; // 返回true表示执行结束不需继续执行父类按键响应
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 首次启动添加快捷方式
		if (AppPrefrence.getIsFirstLaunch(this)) {
			AppPrefrence.saveIsFirstLaunch(this, false);
			addShortcut();
		}

		// 闪屏广告
		HandsgoSplash adsmogoSplash = new HandsgoSplash(this, AppConstants.MOGO_ID,
				HandsgoSplashMode.BOTTOM);
		adsmogoSplash.setHandsgoSplashListener(new HandsgoSplashListener() {

			@Override
			public void onSplashError(String error) {
				LogUtil.e(TAG, "onSplashError:" + error);
			}

			@Override
			public void onSplashClose() {
				LogUtil.e(TAG, "onSplashClose");
				launchMainActivity();
			}

			@Override
			public void onSplashSucceed() {
				LogUtil.e(TAG, "onSplashSucceed");
				mAdHasLoad = true;
			}

			@Override
			public void onSplashClickAd(String adName) {
			}

			@Override
			public void onSplashRealClickAd(String adName) {
			}
		});
		// TODO
	}

	private void launchMainActivity() {
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void addShortcut() {
		Intent shortCutIntent = new Intent(SHORTCUT_INTENT);
		String appName = this.getString(R.string.app_name);
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
		// 不用重复创建
		shortCutIntent.putExtra("duplicate", false);
		Intent localIntent = new Intent(Intent.ACTION_MAIN, null);
		localIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		String packageName = this.getPackageName();
		String className = packageName + "." + this.getLocalClassName();
		ComponentName localComponentName = new ComponentName(packageName, className);
		localIntent.setComponent(localComponentName);
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, localIntent);
		Intent.ShortcutIconResource localShortcutIconResource = Intent.ShortcutIconResource
				.fromContext(this, R.drawable.ic_launcher);
		shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, localShortcutIconResource);

		this.sendBroadcast(shortCutIntent);
	}

	@Override
	public String getPageName() {
		return "闪屏界面";
	}
}
