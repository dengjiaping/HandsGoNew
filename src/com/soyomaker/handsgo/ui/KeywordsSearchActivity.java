package com.soyomaker.handsgo.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.search.KeywordsSearchController;

public class KeywordsSearchActivity extends BaseActivity {

	private Button mSearchButton;

	private EditText mSearchEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keywords_search);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.keywords_search);

		mSearchEditText = (EditText) findViewById(R.id.search_edit);

		mSearchButton = (Button) findViewById(R.id.search);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (KeywordsSearchController.getInstance().isSearching()) {
					return;
				}
				final String key = mSearchEditText.getText().toString().trim();
				new Thread() {

					public void run() {
						boolean find = KeywordsSearchController.getInstance().find(key);
						if (!find) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Toast.makeText(KeywordsSearchActivity.this,
											R.string.toast_invalid_keywords_search,
											Toast.LENGTH_LONG).show();
								}
							});
						} else {
							ArrayList<ChessManual> chessManuals = KeywordsSearchController
									.getInstance().getChessManuals();
							if (!chessManuals.isEmpty()) {
								Intent intent = new Intent(KeywordsSearchActivity.this,
										KeywordsResultActivity.class);
								intent.putExtra(KeywordsResultActivity.EXTRA_SEARCH_RESULT,
										KeywordsSearchController.getInstance().getServer());
								startActivity(intent);
							} else {
								// TODO 搜索结果为空进行提示
							}
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								mSearchButton.clearAnimation();
							}
						});
					}
				}.start();
				mSearchButton.startAnimation(AnimationUtils.loadAnimation(
						KeywordsSearchActivity.this, R.anim.anim_searching));
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
		return "关键字搜索界面";
	}
}
