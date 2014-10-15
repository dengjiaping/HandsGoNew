package com.soyomaker.handsgo.ui;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsChangeNotify;
import net.youmi.android.offers.PointsManager;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.sina.sae.cloudservice.api.CloudClient;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

/**
 * 主界面
 * 
 * @author like
 * 
 */
public class MainActivity extends BaseFragmentActivity implements ActionBar.TabListener,
        PointsChangeNotify {

    private static final String TAG = "MainActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private FeedbackAgent mFeedbackAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 有米广告初始化
        AdManager.getInstance(this).init(AppConstants.APP_ID, AppConstants.APP_SECRET, false);

        OffersManager.getInstance(this).onAppLaunch();

        PointsManager.getInstance(this).registerNotify(this);

        // 关闭积分到账悬浮框提示功能
        PointsManager.setEnableEarnPointsToastTips(false);

        AdManager.getInstance(this).setEnableDebugLog(AppConstants.DEBUG);

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
                    // 查询有米积分
                    int points = PointsManager.getInstance(MainActivity.this).queryPoints();
                    LogUtil.e(TAG, "onCreate 现有积分余额：" + points);
                    AppPrefrence.savePoints(MainActivity.this, points);

                    // 友盟在线参数更新
                    MobclickAgent.updateOnlineConfig(MainActivity.this);

                    // 初始化CloudClient
                    try {
                        CloudClient.init(MainActivity.this, AppConstants.CLOUD_APP_NAME,
                                AppConstants.CLOUD_APP_ACCESS_KEY,
                                AppConstants.CLOUD_APP_SECRET_KEY);
                        // 接下来就可以使用SDK各功能接口了
                        // CloudObject CloudDB CloudFile CloudMail ......
                    } catch (CloudServiceException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void onDestory() {
        super.onDestroy();

        OffersManager.getInstance(this).onAppExit();

        PointsManager.getInstance(this).unRegisterNotify(this);
    }

    private void initView() {
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);// 缓存两页，防止左右切换时Fragment被销毁

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
        case R.id.action_settings: {
            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
        }
            break;
        case R.id.action_feedback: {
            mFeedbackAgent.startFeedbackActivity();
        }
            break;
        case R.id.action_recommend: {
            // 应用推荐
            // DiyManager.showRecommendWall(this);
            // 积分墙
            OffersManager.getInstance(this).showOffersWall();
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

        static final int INDEX_GAME = 0;
        static final int INDEX_STUDY = 1;
        static final int INDEX_SEARCH = 2;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
            case INDEX_GAME:
                fragment = new GameFragment();
                break;
            case INDEX_STUDY:
                fragment = new StudyFragment();
                break;
            case INDEX_SEARCH:
                fragment = new SearchFragment();
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
            case INDEX_GAME:
                return getString(R.string.title_section1);
            case INDEX_STUDY:
                return getString(R.string.title_section2);
            case INDEX_SEARCH:
                return getString(R.string.title_section3);
            }
            return null;
        }
    }

    @Override
    public String getPageName() {
        return "主界面";
    }

    @Override
    public void onPointBalanceChange(final int arg0) {
        new Thread() {
            public void run() {
                LogUtil.e(TAG, "onPointBalanceChange 当前积分余额：" + arg0);
                AppPrefrence.savePoints(MainActivity.this, arg0);
            }
        }.start();
    }
}
