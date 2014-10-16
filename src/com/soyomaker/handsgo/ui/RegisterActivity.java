package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.soyomaker.handsgo.R;

public class RegisterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_register);
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

    @Override
    public String getPageName() {
        return "注册界面";
    }
}
