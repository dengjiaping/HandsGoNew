package com.soyomaker.handsgo.ui;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.soyomaker.handsgo.R;

public class GameHallActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_hall);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_game_hall);
	}

	@Override
	public String getPageName() {
		return "游戏大厅界面";
	}
}
