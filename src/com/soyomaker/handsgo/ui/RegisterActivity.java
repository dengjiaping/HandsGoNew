package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.util.AppUtil;

public class RegisterActivity extends BaseActivity {

    private Button mRegisterButton;

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

        mRegisterButton = (Button) findViewById(R.id.register_btn);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {

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
                    Toast.makeText(RegisterActivity.this, R.string.toast_user_name_null,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_user_email_null,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_user_password_null,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(reEnterPassword)) {
                    Toast.makeText(RegisterActivity.this,
                            R.string.toast_user_re_enter_password_null, Toast.LENGTH_LONG).show();
                    return;
                }
                if (!edtPassword.getText().toString()
                        .equals(edtReEnterPassword.getText().toString())) {
                    Toast.makeText(RegisterActivity.this, R.string.toast_user_password_not_equal,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                mRegisterButton.setText(R.string.btn_registering);
                new Thread() {

                    public void run() {
                        final boolean success = CloudManager.getInstance().register(
                                RegisterActivity.this, name, password,
                                AppUtil.getDeviceId(RegisterActivity.this), email, gender);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                mRegisterButton.setText(R.string.btn_register);
                                if (success) {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.toast_register_success, Toast.LENGTH_LONG)
                                            .show();
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.toast_register_fail, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
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
