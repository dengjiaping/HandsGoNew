package com.soyomaker.handsgo.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.CommentListViewAdapter;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Comment;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.util.LogUtil;

public class CommentsActivity extends BaseActivity {

    private static final String TAG = "CommentsActivity";
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
        if (mSwipeRefreshLayout.isRefreshing()) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        new Thread() {

            public void run() {
                final ArrayList<Comment> comments = CloudManager.getInstance().requestComments(
                        CommentsActivity.this, mChessManual.getSgfUrl());
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (comments != null) {
                            mAdapter.updateComments(comments);
                        } else {
                            // TODO toast
                        }
                    }
                });
            }
        }.start();
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
        findViewById(R.id.btn_comment).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CloudManager.getInstance().hasLogin()) {
                    final View view = LayoutInflater.from(CommentsActivity.this).inflate(
                            R.layout.vw_dialog_edittext, null);
                    final EditText editText = (EditText) view.findViewById(R.id.editText);
                    new AlertDialog.Builder(CommentsActivity.this)
                            .setTitle(R.string.comment_dialog_title)
                            .setIcon(R.drawable.ic_launcher).setView(view)
                            .setPositiveButton(R.string.comment_dialog_ok, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LogUtil.e(TAG, "发表评论");
                                    new Thread() {
                                        public void run() {
                                            User user = CloudManager.getInstance().getLoginUser();
                                            Comment comment = new Comment();
                                            comment.setComment(editText.getText().toString());
                                            comment.setCommentSgf(mChessManual.getSgfUrl());
                                            comment.setUserId(user.getId());
                                            comment.setUserName(user.getName());
                                            boolean success = CloudManager.getInstance()
                                                    .sendComment(CommentsActivity.this, comment);
                                            if (success) {
                                                runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(CommentsActivity.this,
                                                                R.string.toast_comment_success,
                                                                Toast.LENGTH_LONG).show();
                                                        refreshComments();
                                                    }
                                                });
                                            } else {
                                                runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(CommentsActivity.this,
                                                                R.string.toast_comment_fail,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }.start();
                                }
                            }).setNegativeButton(R.string.comment_dialog_cancel, null).show();
                } else {
                    Intent intent = new Intent(CommentsActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
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
