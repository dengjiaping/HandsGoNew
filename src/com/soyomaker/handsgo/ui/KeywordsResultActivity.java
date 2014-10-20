package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.util.LogUtil;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

public class KeywordsResultActivity extends BaseActivity {

	public static final String EXTRA_SEARCH_RESULT = "extra_search_result";

	public IChessManualServer mResultServer;
	private ChessManualListViewAdapter mAdapter;
	private ActionSlideExpandableListView mChessManualListView;
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position >= mResultServer.getChessManuals().size()) {
				if (!toastLoading()) {
					loadMoreChessManuals();
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keywords_result);

		initData();
		initView();
	}

	private void initData() {
		Intent intent = getIntent();
		mResultServer = (IChessManualServer) intent.getSerializableExtra(EXTRA_SEARCH_RESULT);
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_search_result);

		mChessManualListView = (ActionSlideExpandableListView) findViewById(R.id.listview_keywords_result);
		mChessManualListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview, int position) {
						if (buttonview.getId() == R.id.buttonCollect) {
							LogUtil.e("GameFragment", "收藏棋谱");
							mResultServer.collect(mAdapter.getItem(position));
							Toast.makeText(KeywordsResultActivity.this,
									R.string.toast_collect_success, Toast.LENGTH_LONG).show();
						}
					}
				}, R.id.buttonCollect);
		mChessManualListView.setOnItemClickListener(mOnItemClickListener);

		mAdapter = new ChessManualListViewAdapter(this, mResultServer);
		mChessManualListView.setAdapter(mAdapter);
	}

	private boolean toastLoading() {
		if (mResultServer.isLoadingMore()) {
			Toast.makeText(this, R.string.toast_loading, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	private void loadMoreChessManuals() {
		new Thread() {

			public void run() {
				mResultServer.loadMore();
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter.notifyDataSetChanged();
					}
				});
			}
		}.start();
		mAdapter.notifyDataSetChanged();
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
		return "关键字搜索结果界面";
	}
}
