package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.util.AppUtil;

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

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText edtName = (EditText) findViewById(R.id.edt_name);
                EditText edtEmail = (EditText) findViewById(R.id.edt_email);
                EditText edtPassword = (EditText) findViewById(R.id.edt_password);
                EditText edtReEnterPassword = (EditText) findViewById(R.id.edt_re_enter_password);
                RadioGroup genderGroup = (RadioGroup) findViewById(R.id.gender_layout);
                final String name = edtName.getText().toString();
                final String email = edtEmail.getText().toString();
                final String password = edtPassword.getText().toString();
                final String reEnterPassword = edtReEnterPassword.getText().toString();
                final String gender = (genderGroup.getCheckedRadioButtonId() == R.id.male ? "0"
                        : "1");
                if (TextUtils.isEmpty(name)) {
                    // TODO toast
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    // TODO toast
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    // TODO toast
                    return;
                }
                if (TextUtils.isEmpty(reEnterPassword)) {
                    // TODO toast
                    return;
                }
                if (!edtPassword.getText().toString()
                        .equals(edtReEnterPassword.getText().toString())) {
                    // TODO toast
                    return;
                }
                new Thread() {

                    public void run() {
                        boolean success = CloudManager.getInstance().register(
                                RegisterActivity.this, name, password,
                                AppUtil.getDeviceId(RegisterActivity.this), email, gender);
                        if (success) {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.toast_register_success, Toast.LENGTH_LONG)
                                            .show();
                                    finish();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.toast_register_fail, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }.start();
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
        return "注册界面";
    }
}
