package com.soyomaker.handsgo.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.ui.view.CheckSwitchButton;
import com.soyomaker.handsgo.ui.view.ColorPickerDialog;
import com.soyomaker.handsgo.ui.view.ColorPickerDialog.OnColorChangedListener;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;
import com.soyomaker.handsgo.util.LogUtil;

/**
 * 应用设置界面
 * 
 * @author like
 * 
 */
public class OptionsActivity extends BaseActivity {

    private ColorPickerDialog mChessBoardColorPickerDialog;
    private TextView mUserNameTextView;
    private Button mSigninButton;
    private Button mPasswordButton;
    private Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_settings);

        CheckSwitchButton showNumberCheck = (CheckSwitchButton) findViewById(R.id.show_number);
        showNumberCheck.setChecked(AppPrefrence.getShowNumber(this));
        showNumberCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPrefrence.saveShowNumber(OptionsActivity.this, isChecked);
            }
        });

        CheckSwitchButton showLocationCheck = (CheckSwitchButton) findViewById(R.id.show_location);
        showLocationCheck.setChecked(AppPrefrence.getShowCoordinate(this));
        showLocationCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPrefrence.saveShowCoordinate(OptionsActivity.this, isChecked);
            }
        });

        CheckSwitchButton playSoundCheck = (CheckSwitchButton) findViewById(R.id.play_sound);
        playSoundCheck.setChecked(AppPrefrence.getLazySound(this));
        playSoundCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPrefrence.saveLazySound(OptionsActivity.this, isChecked);
            }
        });

        findViewById(R.id.choose_board_color).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooseChessBoardStyleDialog();
            }
        });

        findViewById(R.id.choose_piece_style).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooseChessPieceStyleDialog();
            }
        });

        findViewById(R.id.choose_auto_play_interval).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooseAutoPlayInterval();
            }
        });

        findViewById(R.id.about_app).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OptionsActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.about_app).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (!AppConstants.DEBUG) {
                    return false;
                }
                // TODO 进入实验室界面
                return false;
            }
        });

        mUserNameTextView = ((TextView) findViewById(R.id.txt_user_name));
        // mUserPointTextView = ((TextView) findViewById(R.id.txt_point));
        mSigninButton = (Button) findViewById(R.id.btn_signin);
        mPasswordButton = (Button) findViewById(R.id.btn_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);
    }

    private void updateLoginUI() {
        if (CloudManager.getInstance().hasLogin()) {
            mUserNameTextView.setVisibility(View.VISIBLE);
            mUserNameTextView.setText(CloudManager.getInstance().getLoginUser().getName());
            // mUserPointTextView.setVisibility(View.VISIBLE);
            // mUserPointTextView.setText(String.format(getString(R.string.local_point),
            // AppPrefrence.getPoints(this)));
            mSigninButton.setVisibility(View.VISIBLE);
            mSigninButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CloudManager.getInstance().signin(OptionsActivity.this);
                    updateLoginUI();
                }
            });
            mPasswordButton.setVisibility(View.VISIBLE);
            mPasswordButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showChangePasswordDialog();
                }
            });
            mLoginButton.setVisibility(View.GONE);
        } else {
            mUserNameTextView.setVisibility(View.GONE);
            // mUserPointTextView.setVisibility(View.GONE);
            mSigninButton.setVisibility(View.GONE);
            mPasswordButton.setVisibility(View.GONE);
            mLoginButton.setVisibility(View.VISIBLE);
            mLoginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OptionsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void onResume() {
        super.onResume();
        updateLoginUI();
    }

    private void showChangePasswordDialog() {
        if (CloudManager.getInstance().hasLogin()) {
            final View view = LayoutInflater.from(OptionsActivity.this).inflate(
                    R.layout.dialog_reset_password_edt, null);
            final EditText edtOldPassword = (EditText) view.findViewById(R.id.edt_password);
            final EditText edtNewPassword = (EditText) view.findViewById(R.id.edt_new_password);
            new AlertDialog.Builder(OptionsActivity.this)
                    .setTitle(R.string.password_dialog_title)
                    .setIcon(R.drawable.ic_launcher)
                    .setView(view)
                    .setPositiveButton(R.string.password_dialog_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String oldPassword = edtOldPassword.getText().toString();
                                    final String newPassword = edtNewPassword.getText().toString();
                                    if (TextUtils.isEmpty(oldPassword)) {
                                        Toast.makeText(OptionsActivity.this,
                                                R.string.toast_old_password_null, Toast.LENGTH_LONG)
                                                .show();
                                        return;
                                    }
                                    if (TextUtils.isEmpty(newPassword)) {
                                        Toast.makeText(OptionsActivity.this,
                                                R.string.toast_new_password_null, Toast.LENGTH_LONG)
                                                .show();
                                        return;
                                    }
                                    if (oldPassword.equals(newPassword)) {
                                        Toast.makeText(OptionsActivity.this,
                                                R.string.toast_old_new_password_equal,
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    new Thread() {
                                        public void run() {
                                            User user = CloudManager.getInstance().getLoginUser();
                                            final int code = CloudManager.getInstance()
                                                    .changePassword(OptionsActivity.this,
                                                            user.getName(), oldPassword,
                                                            newPassword);
                                            runOnUiThread(new Runnable() {

                                                @Override
                                                public void run() {
                                                    LogUtil.e("OptionsActivity", "code:" + code);
                                                    if (code == CloudServiceException.CODE_SUCCESS) {
                                                        Toast.makeText(
                                                                OptionsActivity.this,
                                                                R.string.toast_change_password_success,
                                                                Toast.LENGTH_LONG).show();
                                                    } else if (code == CloudServiceException.CODE_USERNAME_NOT_EXISTS) {
                                                        Toast.makeText(
                                                                OptionsActivity.this,
                                                                R.string.toast_change_password_user_name_not_exists,
                                                                Toast.LENGTH_LONG).show();
                                                    } else {
                                                        Toast.makeText(
                                                                OptionsActivity.this,
                                                                R.string.toast_change_password_fail,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                        }
                                    }.start();
                                }
                            }).setNegativeButton(R.string.password_dialog_cancel, null).show();
        } else {
            Intent intent = new Intent(OptionsActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void showChooseChessPieceStyleDialog() {
        DialogUtils.showSingleChoiceItemsDialog(this,
                R.string.chess_piece_style_choose_dialog_title, R.array.chess_piece_style,
                AppPrefrence.getChessPieceStyle(this), new ItemSelectedListener() {

                    @Override
                    public void onItemSelected(DialogInterface dialog, String text, int which) {
                        AppPrefrence.saveChessPieceStyle(OptionsActivity.this, which);
                    }
                });
    }

    private void showChooseChessBoardStyleDialog() {
        DialogUtils.showSingleChoiceItemsDialog(this,
                R.string.chess_board_style_choose_dialog_title, R.array.chess_board_style,
                AppPrefrence.getChessBoardStyle(this), new ItemSelectedListener() {

                    @Override
                    public void onItemSelected(DialogInterface dialog, String text, int which) {
                        AppPrefrence.saveChessBoardStyle(OptionsActivity.this, which);
                        if (which == AppConstants.CHESS_BOARD_STYLE_COLOR) {
                            showChooseChessBoardColorDialog();
                        }
                    }
                });
    }

    private void showChooseChessBoardColorDialog() {
        if (mChessBoardColorPickerDialog == null) {
            mChessBoardColorPickerDialog = new ColorPickerDialog(this,
                    getString(R.string.chess_board_color_picker_dialog_title),
                    AppPrefrence.getChessBoardColor(this), new OnColorChangedListener() {

                        @Override
                        public void colorChanged(int color) {
                            mChessBoardColorPickerDialog.cancel();
                            AppPrefrence.saveChessBoardColor(OptionsActivity.this, color);
                        }
                    });
        }
        mChessBoardColorPickerDialog.show();
    }

    private void showChooseAutoPlayInterval() {
        int chooseItem = 0;
        if (AppPrefrence.getAutoNext(OptionsActivity.this)) {
            String interval = AppPrefrence.getAutoNextInterval(OptionsActivity.this);
            if ("1000".equals(interval)) {
                chooseItem = 1;
            } else if ("2000".equals(interval)) {
                chooseItem = 2;
            } else if ("4000".equals(interval)) {
                chooseItem = 3;
            } else if ("8000".equals(interval)) {
                chooseItem = 4;
            } else if ("16000".equals(interval)) {
                chooseItem = 5;
            }
        }
        new AlertDialog.Builder(OptionsActivity.this)
                .setTitle(R.string.options_auto_play)
                .setIcon(R.drawable.ic_launcher)
                .setSingleChoiceItems(R.array.auto_play_interval, chooseItem,
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    AppPrefrence.saveAutoNext(OptionsActivity.this, false);
                                } else {
                                    AppPrefrence.saveAutoNext(OptionsActivity.this, true);
                                    if (which == 1) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "1000");
                                    } else if (which == 2) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "2000");
                                    } else if (which == 3) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "4000");
                                    } else if (which == 4) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "8000");
                                    } else if (which == 5) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "16000");
                                    }
                                }

                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
    }

    @Override
    public String getPageName() {
        return "应用设置界面";
    }
}
