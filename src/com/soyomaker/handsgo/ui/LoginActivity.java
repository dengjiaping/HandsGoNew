package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.model.User;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_login);

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText edtName = (EditText) findViewById(R.id.edt_name);
                EditText edtPassword = (EditText) findViewById(R.id.edt_password);
                final String name = edtName.getText().toString();
                final String password = edtPassword.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    // TODO toast
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    // TODO toast
                    return;
                }
                new Thread() {

                    public void run() {
                        User user = CloudManager.getInstance().login(LoginActivity.this, name,
                                password);
                        if (user != null) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this,
                                            R.string.toast_login_success, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, R.string.toast_login_fail,
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }.start();
            }
        });
        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
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
        return "登录界面";
    }
}
