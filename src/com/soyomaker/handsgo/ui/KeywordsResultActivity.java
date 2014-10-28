package com.soyomaker.handsgo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.server.IChessManualServer;

public class KeywordsResultActivity extends BaseActivity {

    public static final String EXTRA_SEARCH_RESULT = "extra_search_result";

    public IChessManualServer mResultServer;
    private ChessManualListViewAdapter mAdapter;
    private ListView mChessManualListView;
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

    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private void initData() {
        Intent intent = getIntent();
        mResultServer = (IChessManualServer) intent.getSerializableExtra(EXTRA_SEARCH_RESULT);
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_search_result);

        mChessManualListView = (ListView) findViewById(R.id.listview_keywords_result);
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
                        mAdapter.updateChessManuals();
                    }
                });
            }
        }.start();
        mAdapter.updateChessManuals();
    }

    @Override
    public String getPageName() {
        return "关键字搜索结果界面";
    }
}
