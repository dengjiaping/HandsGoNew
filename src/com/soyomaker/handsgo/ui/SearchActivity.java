package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.soyomaker.handsgo.R;

public class SearchActivity extends BaseActivity {

	private Button mShapeSearchButton;
	private Button mKeywordsSearchButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_search);

		mShapeSearchButton = (Button) findViewById(R.id.shape_search);
		mShapeSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchActivity.this, ShapeSearchActivity.class);
				startActivity(intent);
			}
		});
		mKeywordsSearchButton = (Button) findViewById(R.id.keywords_search);
		mKeywordsSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SearchActivity.this, KeywordsSearchActivity.class);
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
		return "棋谱搜索界面";
	}
}