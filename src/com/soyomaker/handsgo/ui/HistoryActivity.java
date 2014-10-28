package com.soyomaker.handsgo.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
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

    public void onResume() {
        super.onResume();
        mAdapter.updateChessManuals();
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_history);

        mChessManualListView = (ListView) findViewById(R.id.listview_history);

        mHistoryServer = ChessManualServerManager.getHistoryServer();
        mAdapter = new ChessManualListViewAdapter(this, mHistoryServer);
        mChessManualListView.setAdapter(mAdapter);
    }

    @Override
    public String getPageName() {
        return "历史记录界面";
    }
}
