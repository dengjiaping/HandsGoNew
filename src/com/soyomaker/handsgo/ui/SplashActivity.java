package com.soyomaker.handsgo.ui;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.AppPrefrence;

public class SplashActivity extends BaseActivity {

	private static final String TAG = "SplashActivity";

	private static final String SHORTCUT_INTENT = "com.android.launcher.action.INSTALL_SHORTCUT";

	private static final int MIN_DELAY_TIME = 2000;

	private static final int MAX_DELAY_TIME = 2000;

	private Handler mMainHandler = new Handler();
	private long mWaitTime = 0;

	private boolean mMainHasLaunch = false;

	private Runnable mToMainRunnable = new Runnable() {

		@Override
		public void run() {
			waitToMain();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 不显示系统的标题栏
		setContentView(R.layout.activity_splash);

		mWaitTime = System.currentTimeMillis();
		initData();
		// 启动页面+广告显示 最多 MAX_DELAY_TIME 时间
		mMainHandler.postDelayed(mToMainRunnable, MAX_DELAY_TIME);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true; // 返回true表示执行结束不需继续执行父类按键响应
		}
		return super.onKeyDown(keyCode, event);
	}

	private void waitToMain() {
		// 启动界面+广告显示 最少2s钟
		long useTime = System.currentTimeMillis() - mWaitTime;
		if (useTime < MIN_DELAY_TIME) {
			mMainHandler.removeCallbacks(mToMainRunnable);
			mMainHandler.postDelayed(mToMainRunnable, MIN_DELAY_TIME - useTime);
		} else {
			mMainHandler.removeCallbacks(mToMainRunnable);
			launchMainActivity();
		}
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
		// TODO
	}

	private void launchMainActivity() {
		if (!mMainHasLaunch) {
			mMainHasLaunch = true;
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
		}
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
