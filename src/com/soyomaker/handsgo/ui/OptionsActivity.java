package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.ui.view.CheckSwitchButton;
import com.soyomaker.handsgo.ui.view.ColorPickerDialog;
import com.soyomaker.handsgo.ui.view.ColorPickerDialog.OnColorChangedListener;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;

/**
 * 应用设置界面
 * 
 * @author like
 * 
 */
public class OptionsActivity extends BaseActivity {

    private ColorPickerDialog mChessBoardColorPickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_settings);

        findViewById(R.id.chess_source).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showChooseChessSourceDialog();
            }
        });
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
                showChooseChessBoardColorDialog();
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

    private void showChooseChessSourceDialog() {
        new AlertDialog.Builder(OptionsActivity.this)
                .setTitle(R.string.options_chess_source)
                .setIcon(R.drawable.ic_launcher)
                .setSingleChoiceItems(R.array.chess_source,
                        AppPrefrence.getChessSource(OptionsActivity.this),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                AppPrefrence.saveChessSource(OptionsActivity.this, which);
                                ChessManualServerManager.getInstance().updateChessManualServer();
                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
    }

    private void showChooseAutoPlayInterval() {
        int chooseItem = 0;
        if (AppPrefrence.getAutoNext(OptionsActivity.this)) {
            String interval = AppPrefrence.getAutoNextInterval(OptionsActivity.this);
            if ("2000".equals(interval)) {
                chooseItem = 1;
            } else if ("4000".equals(interval)) {
                chooseItem = 2;
            } else if ("8000".equals(interval)) {
                chooseItem = 3;
            } else if ("16000".equals(interval)) {
                chooseItem = 4;
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
                                                "2000");
                                    } else if (which == 2) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "4000");
                                    } else if (which == 3) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "8000");
                                    } else if (which == 4) {
                                        AppPrefrence.saveAutoNextInterval(OptionsActivity.this,
                                                "16000");
                                    }
                                }

                                dialog.dismiss();
                            }
                        }).setNegativeButton(R.string.cancel, null).show();
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
        return "应用设置界面";
    }
}
