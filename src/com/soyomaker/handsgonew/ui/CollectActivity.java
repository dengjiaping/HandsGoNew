package com.soyomaker.handsgonew.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgonew.manager.ChessManualServerManager;
import com.soyomaker.handsgonew.server.CollectServer;
import com.soyomaker.handsgonew.util.LogUtil;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

/**
 * 我的收藏界面
 * 
 * @author like
 * 
 */
public class CollectActivity extends Activity {

	private ChessManualListViewAdapter mAdapter;
	private ActionSlideExpandableListView mChessManualListView;
	private CollectServer mCollectServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_collect);

		mChessManualListView = (ActionSlideExpandableListView) findViewById(R.id.listview_collect);
		mChessManualListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview, int position) {
						int id = buttonview.getId();
						if (id == R.id.buttonDelete) {
							LogUtil.e("HistoryActivity", "删除棋谱");
							boolean result = mCollectServer.delete(mAdapter.getItem(position));
							if (result) {
								mAdapter.notifyDataSetChanged();
							}
						}
					}
				}, R.id.buttonCollect, R.id.buttonDelete);

		mCollectServer = ChessManualServerManager.getInstance().getCollectServer();
		mAdapter = new ChessManualListViewAdapter(this, mCollectServer);
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
}
