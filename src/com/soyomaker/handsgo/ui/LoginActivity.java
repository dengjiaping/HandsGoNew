package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.util.AppPrefrence;

public class LoginActivity extends BaseActivity {

    private EditText mEdtName;
    private EditText mEdtPassword;
    private Button mLoginButton;

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

        mEdtName = (EditText) findViewById(R.id.edt_name);
        String name = AppPrefrence.getUserName(this);
        if (TextUtils.isEmpty(name)) {
            mEdtName.setText(name);
        }
        mEdtPassword = (EditText) findViewById(R.id.edt_password);
        String password = AppPrefrence.getUserPassword(this);
        if (TextUtils.isEmpty(password)) {
            mEdtPassword.setText(password);
        }
        mLoginButton = (Button) findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String name = mEdtName.getText().toString();
                final String password = mEdtPassword.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(LoginActivity.this, R.string.toast_user_name_null,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, R.string.toast_user_password_null,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mLoginButton.setText(R.string.btn_logining);
                new Thread() {

                    public void run() {
                        final User user = CloudManager.getInstance().login(LoginActivity.this,
                                name, password);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mLoginButton.setText(R.string.btn_login);
                                if (user != null) {
                                    Toast.makeText(LoginActivity.this,
                                            R.string.toast_login_success, Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, R.string.toast_login_fail,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
