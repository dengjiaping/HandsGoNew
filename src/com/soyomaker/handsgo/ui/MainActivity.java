package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sina.sae.cloudservice.api.CloudClient;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.ui.fileexplorer.FileExplorerActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 主界面
 * 
 * @author like
 * 
 */
public class MainActivity extends BaseFragmentActivity implements ActionBar.TabListener {
	private static final String TAG = "MainActivity";

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private FeedbackAgent mFeedbackAgent;

	private long mExitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		// 友盟意见反馈初始化
		mFeedbackAgent = new FeedbackAgent(MainActivity.this);

		// 首先检测网络是否通畅
		if (CloudClient.checkNetwork(MainActivity.this)) {
			// 获取意见反馈数据
			mFeedbackAgent.sync();

			// 友盟自动检测更新
			UmengUpdateAgent.update(MainActivity.this);

			new Thread() {
				public void run() {
					// 友盟在线参数更新
					MobclickAgent.updateOnlineConfig(MainActivity.this);
				}
			}.start();
		}
	}

	public void onDestory() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(getApplicationContext(), R.string.toast_re_press_to_exit,
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true; // 返回true表示执行结束不需继续执行父类按键响应
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount() - 1);// 缓存全部页数，防止左右切换时Fragment被销毁

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_collect: {
			Intent intent = new Intent(this, CollectActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_history: {
			Intent intent = new Intent(this, HistoryActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_local: {
			Intent intent = new Intent(this, FileExplorerActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_search: {
			Intent intent = new Intent(this, SearchActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_settings: {
			Intent intent = new Intent(this, OptionsActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_feedback: {
			mFeedbackAgent.startFeedbackActivity();
		}
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		static final int INDEX_SINA = 0;
		static final int INDEX_XGOO = 1;
		static final int INDEX_TOM = 2;

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case INDEX_SINA: {
				fragment = new GameFragment();
				Bundle args = new Bundle();
				args.putSerializable(GameFragment.EXTRA_SERVER,
						ChessManualServerManager.getSinaServer());
				fragment.setArguments(args);
			}
				break;
			case INDEX_XGOO: {
				fragment = new GameFragment();
				Bundle args = new Bundle();
				args.putSerializable(GameFragment.EXTRA_SERVER,
						ChessManualServerManager.getXgooServer());
				fragment.setArguments(args);
			}
				break;
			case INDEX_TOM: {
				fragment = new GameFragment();
				Bundle args = new Bundle();
				args.putSerializable(GameFragment.EXTRA_SERVER,
						ChessManualServerManager.getTomServer());
				fragment.setArguments(args);
			}
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case INDEX_SINA:
				return getString(R.string.title_section1);
			case INDEX_XGOO:
				return getString(R.string.title_section2);
			case INDEX_TOM:
				return getString(R.string.title_section3);
			}
			return null;
		}
	}

	@Override
	public String getPageName() {
		return "主界面";
	}
}
