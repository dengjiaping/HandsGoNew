package com.soyomaker.handsgo.ui;

import ad.soyomaker.handsgo.util.L;
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

import com.adsmogo.offers.MogoOffer;
import com.adsmogo.offers.MogoOfferPointCallBack;
import com.sina.sae.cloudservice.api.CloudClient;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileExplorerActivity;
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
        MogoOfferPointCallBack {

    private static final String TAG = "MainActivity";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private FeedbackAgent mFeedbackAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        // 芒果日志开启
        L.debug = AppConstants.DEBUG;

        // 芒果广告初始化
        MogoOffer.init(this, AppConstants.MOGO_ID);

        MogoOffer.addPointCallBack(this);

        MogoOffer.setMogoOfferScoreVisible(false);

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

    public void onResume() {
        MogoOffer.RefreshPoints(this);
        super.onResume();
    }

    public void onDestory() {
        MogoOffer.clear(this);
        super.onDestroy();
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
            MogoOffer.showOffer(this);
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
        static final int INDEX_SEARCH = 1;

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
            case INDEX_SEARCH:
                fragment = new SearchFragment();
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case INDEX_GAME:
                return getString(R.string.title_section1);
            case INDEX_SEARCH:
                return getString(R.string.title_section2);
            }
            return null;
        }
    }

    @Override
    public String getPageName() {
        return "主界面";
    }

    @Override
    public void updatePoint(final long arg0) {
        new Thread() {
            public void run() {
                LogUtil.e(TAG, "onPointBalanceChange 当前积分余额：" + arg0);
                AppPrefrence.savePoints(MainActivity.this, arg0);
            }
        }.start();
    }
}
