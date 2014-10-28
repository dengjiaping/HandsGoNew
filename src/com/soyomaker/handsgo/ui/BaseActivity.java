package com.soyomaker.handsgo.ui;

import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockActivity;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends SherlockActivity implements IPageName {

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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
