package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.ChessManual;

public class CommentsActivity extends BaseActivity {

	public static final String EXTRA_CHESSMANUAL = "extra_chessmanual";

	private ChessManual mChessManual;

	// 支持下拉刷新评论 和 发表评论

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_comments);
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
		return "评论列表界面";
	}
}
