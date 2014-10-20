package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.server.HistoryServer;
import com.soyomaker.handsgo.util.LogUtil;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

/**
 * 历史记录界面
 * 
 * @author like
 * 
 */
public class HistoryActivity extends BaseActivity {

	private ChessManualListViewAdapter mAdapter;
	private ActionSlideExpandableListView mChessManualListView;
	private HistoryServer mHistoryServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_history);

		mChessManualListView = (ActionSlideExpandableListView) findViewById(R.id.listview_history);
		mChessManualListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview, int position) {
						int id = buttonview.getId();
						if (id == R.id.buttonCollect) {
							LogUtil.e("HistoryActivity", "收藏棋谱");
							mHistoryServer.collect(mAdapter.getItem(position));
							Toast.makeText(HistoryActivity.this, R.string.toast_collect_success,
									Toast.LENGTH_LONG).show();
						} else if (id == R.id.buttonDelete) {
							LogUtil.e("HistoryActivity", "删除棋谱");
							boolean result = mHistoryServer.delete(mAdapter.getItem(position));
							if (result) {
								mAdapter.notifyDataSetChanged();
							}
						}
					}
				}, R.id.buttonCollect, R.id.buttonDelete);

		mHistoryServer = ChessManualServerManager.getInstance().getHistoryServer();
		mAdapter = new ChessManualListViewAdapter(this, mHistoryServer);
		mChessManualListView.setAdapter(mAdapter);
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
		return "历史记录界面";
	}
}
