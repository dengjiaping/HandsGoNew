package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.server.HistoryServer;

/**
 * 历史记录界面
 * 
 * @author like
 * 
 */
public class HistoryActivity extends BaseActivity {

	private ChessManualListViewAdapter mAdapter;
	private ListView mChessManualListView;
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

		mChessManualListView = (ListView) findViewById(R.id.listview_history);

		mHistoryServer = ChessManualServerManager.getHistoryServer();
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
