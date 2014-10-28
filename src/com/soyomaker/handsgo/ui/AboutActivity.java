package com.soyomaker.handsgo.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.soyomaker.handsgo.R;

/**
 * 关于界面
 * 
 * @author like
 * 
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_about);
    }

    @Override
    public String getPageName() {
        return "关于界面";
    }
}
