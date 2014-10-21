package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MenuItem;
import android.widget.ListView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.CommentListViewAdapter;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.model.ChessManual;

public class CommentsActivity extends BaseActivity {

    public static final String EXTRA_CHESSMANUAL = "extra_chessmanual";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;

    private CommentListViewAdapter mAdapter;
    private ChessManual mChessManual;

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            refreshComments();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initData();
        initView();
    }

    private void refreshComments() {
        // TODO
    }

    private void initData() {
        Intent intent = getIntent();
        mChessManual = (ChessManual) intent.getSerializableExtra(EXTRA_CHESSMANUAL);
        if (mChessManual == null) {
            finish();
            return;
        }
    }

    private void initView() {
        if (mChessManual == null) {
            return;
        }
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_comments);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mListView = (ListView) findViewById(R.id.listview_comment);
        mAdapter = new CommentListViewAdapter(this, CloudManager.getInstance().getComments(
                mChessManual.getSgfUrl()));
        mListView.setAdapter(mAdapter);

        refreshComments();
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
