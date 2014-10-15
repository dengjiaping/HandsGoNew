package com.soyomaker.handsgo.ui;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseFragment extends Fragment implements IPageName {

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getPageName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getPageName());
    }
}
