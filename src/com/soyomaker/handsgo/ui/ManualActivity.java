package com.soyomaker.handsgo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.CommentListViewAdapter;
import com.soyomaker.handsgo.core.DefaultBoardModel;
import com.soyomaker.handsgo.core.GoBoard;
import com.soyomaker.handsgo.core.GoController;
import com.soyomaker.handsgo.core.GoController.IBoardChangedListener;
import com.soyomaker.handsgo.core.IGridListener;
import com.soyomaker.handsgo.core.io.SGFReader;
import com.soyomaker.handsgo.core.sgf.SGFTree;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.manager.CloudManager;
import com.soyomaker.handsgo.manager.CollectManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Comment;
import com.soyomaker.handsgo.model.Match;
import com.soyomaker.handsgo.model.MatchInfo;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.ui.view.ListViewForScrollView;
import com.soyomaker.handsgo.util.LogUtil;
import com.soyomaker.handsgo.util.StorageUtil;
import com.soyomaker.handsgo.util.StringUtil;

/**
 * 棋谱展示界面
 * 
 * @author like
 * 
 */
public class ManualActivity extends BaseActivity implements IGridListener {

    private static final String TAG = "ManualActivity";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommentListViewAdapter mCommentListAdapter;
    private ListViewForScrollView mCommentListView;
    private RelativeLayout mBoardLayout;

    private LinearLayout mCommentLayout;
    private LinearLayout mToolBarLayout;
    private TextView mCommentTextView;
    private TextView mStatusTextView;

    private Button mCommentButton;
    private Button mCollectButton;

    private ChessManual mChessManual;
    private Match mMatch;
    private GoBoard mGoBoard;
    private GoController mGoController = new GoController();
    private DefaultBoardModel mBoardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        initData();
        initView();
    }

    public void onResume() {
        super.onResume();

        if (mGoBoard != null) {
            mGoBoard.postInvalidate();
        }

        mGoController.resumeAutoNext();
    }

    public void onPause() {
        super.onPause();

        mGoController.pauseAutoNext();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null) {
                LogUtil.e(TAG, "onNewIntent:" + uri.getPath());
            }
        }
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            Uri uri = intent.getData();
            if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                File file = new File(uri.getPath());
                mChessManual = new ChessManual();
                mChessManual.setType(ChessManual.LOCAL_CHESS_MANUAL);
                mChessManual.setSgfUrl(file.getAbsolutePath());
                mChessManual.setMatchName(file.getName());
                mChessManual.setCharset(StringUtil.getCharset(file));
            } else {
                mChessManual = HandsGoApplication.getChessManual();
            }
        }
        // 解决已加载过的棋谱还要重新加载的问题
        if (mChessManual != null && TextUtils.isEmpty(mChessManual.getSgfContent())) {
            if (DBService.isHistoryChessManual(mChessManual)) {
                mChessManual = DBService.getHistoryChessManual(mChessManual);
            } else if (DBService.isFavoriteChessManual(mChessManual)) {
                mChessManual = DBService.getFavoriteChessManual(mChessManual);
            }
        }
    }

    private boolean initBoard() {
        MatchInfo matchInfo = mMatch.getMatchInfo();
        Vector<SGFTree> sgfTrees = mMatch.getSGFTrees();
        if (matchInfo == null || sgfTrees.isEmpty()) {
            return false;
        }
        int size = matchInfo.getBoardSize();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 棋盘大小
        int boardSize = Math.min(dm.widthPixels, dm.heightPixels);
        // 棋子大小
        int cubicSize = Math.round(boardSize * 1.0f / (size + 1));

        mBoardModel = new DefaultBoardModel(size);
        mGoBoard = new GoBoard(this, mBoardModel, cubicSize, cubicSize / 2, cubicSize / 2);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(boardSize,
                boardSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGoBoard.setFocusable(true);
        mGoBoard.setClickable(true);
        mGoBoard.setFocusableInTouchMode(true);
        mGoBoard.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mGoBoard.pointerReleased(Math.round(event.getX()), Math.round(event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mGoBoard.pointerPressed(Math.round(event.getX()), Math.round(event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mGoBoard.pointerMoved(Math.round(event.getX()), Math.round(event.getY()));
                }
                return false;
            }
        });
        mGoBoard.setGridListener(this);
        mBoardLayout.addView(mGoBoard, layoutParams);

        mCommentLayout.setVisibility(View.VISIBLE);
        mToolBarLayout.setVisibility(View.VISIBLE);

        mGoController.setBoardModel(mBoardModel);
        mGoController.setBoardSize(size);
        mGoController.setBoardChangedListener(new IBoardChangedListener() {

            @Override
            public void onBoardChanged() {
                mCommentTextView.setText(mGoController.getComment());
                mGoBoard.postInvalidate();
            }
        });

        mGoController.setTreeNode(sgfTrees.elementAt(0).top());
        mGoController.init();
        mCommentTextView.setText(mGoController.getComment());
        return true;
    }

    private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

        @Override
        public void onRefresh() {
            refreshComments();
        }
    };

    private void refreshComments() {
        // 本地棋谱不进行刷新
        if (cannotComment()) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        if (CloudManager.getInstance().isRefreshingComment(mChessManual.getSgfUrl())) {
            return;
        }
        new Thread() {

            public void run() {
                final ArrayList<Comment> comments = CloudManager.getInstance().requestComments(
                        mChessManual.getSgfUrl());
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (comments != null) {
                            mCommentListAdapter.updateComments(comments);
                        } else {
                            // TODO toast
                        }
                    }
                });
            }
        }.start();
    }

    private void initView() {
        if (mChessManual == null) {
            return;
        }

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mChessManual.getMatchName());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);

        mCommentListView = (ListViewForScrollView) findViewById(R.id.listview_comment);
        mCommentListAdapter = new CommentListViewAdapter(this, CloudManager.getInstance()
                .getComments(mChessManual.getSgfUrl()));
        mCommentListView.setAdapter(mCommentListAdapter);

        mBoardLayout = (RelativeLayout) this.findViewById(R.id.board_layout);
        mCommentLayout = (LinearLayout) this.findViewById(R.id.comment_container);
        mToolBarLayout = (LinearLayout) this.findViewById(R.id.tool_bar);
        mCommentTextView = (TextView) this.findViewById(R.id.text_comment);
        mStatusTextView = (TextView) this.findViewById(R.id.load_status);
        findViewById(R.id.next_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.next();
            }
        });
        findViewById(R.id.prev_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.prev();
            }
        });
        findViewById(R.id.fast_next_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.fastNext();
            }
        });
        findViewById(R.id.fast_prev_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.fastPrev();
            }
        });
        findViewById(R.id.first_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.first();
            }
        });
        findViewById(R.id.last_step).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.last();
            }
        });
        findViewById(R.id.change_var).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mGoController.changeVar();
            }
        });
        mCommentButton = (Button) findViewById(R.id.comment_btn);
        mCollectButton = (Button) findViewById(R.id.collect_btn);
        mCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendComment();
            }
        });
        mCollectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                collectChessManual();
            }
        });
        ((ScrollView) findViewById(R.id.scroll_manual)).scrollTo(0, 0);

        mStatusTextView.setText(R.string.status_loading);
        new Thread() {

            public void run() {
                long time = System.currentTimeMillis();
                mMatch = SGFReader.read(ManualActivity.this, mChessManual);
                LogUtil.e(TAG, "UseTime:" + (System.currentTimeMillis() - time));
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        boolean success = initBoard();
                        if (!success) {
                            mStatusTextView.setText(R.string.status_fail);
                        } else {
                            mStatusTextView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }.start();

        updateCollectBtn();

        // 本地棋谱不显示评论按钮
        if (cannotComment()) {
            mCommentButton.setVisibility(View.GONE);
        }

        refreshComments();
    }

    private boolean cannotComment() {
        return mChessManual.getType() == ChessManual.LOCAL_CHESS_MANUAL
                || TextUtils.isEmpty(mChessManual.getSgfUrl());
    }

    private void updateCollectBtn() {
        if (CollectManager.getInstance().isCollect(mChessManual)) {
            mCollectButton.setText(R.string.btn_uncollect);
        } else {
            mCollectButton.setText(R.string.btn_collect);
        }
    }

    private void sendComment() {
        if (CloudManager.getInstance().hasLogin()) {
            final View view = LayoutInflater.from(ManualActivity.this).inflate(
                    R.layout.dialog_comment_edt, null);
            final EditText editText = (EditText) view.findViewById(R.id.editText);
            new AlertDialog.Builder(ManualActivity.this).setTitle(R.string.comment_dialog_title)
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
                                    final boolean success = CloudManager.getInstance().sendComment(
                                            comment);
                                    runOnUiThread(new Runnable() {

                                        @Override
                                        public void run() {
                                            if (success) {
                                                Toast.makeText(ManualActivity.this,
                                                        R.string.toast_comment_success,
                                                        Toast.LENGTH_LONG).show();
                                                refreshComments();
                                            } else {
                                                Toast.makeText(ManualActivity.this,
                                                        R.string.toast_comment_fail,
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }.start();
                        }
                    }).setNegativeButton(R.string.comment_dialog_cancel, null).show();
        } else {
            Intent intent = new Intent(ManualActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void collectChessManual() {
        if (CollectManager.getInstance().isCollect(mChessManual)) {
            CollectManager.getInstance().cancelCollect(mChessManual);
        } else {
            CollectManager.getInstance().collect(mChessManual);
            Toast.makeText(ManualActivity.this, R.string.toast_collect_success, Toast.LENGTH_LONG)
                    .show();
        }
        updateCollectBtn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.manual, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_manual_match_info: {
            if (mMatch != null && mMatch.getMatchInfo() != null) {
                Intent intent = new Intent(this, ManualInfoActivity.class);
                intent.putExtra(ManualInfoActivity.EXTRA_MATCH_INFO, mMatch.getMatchInfo());
                startActivity(intent);
            } else {
                // TODO toast
            }
        }
            break;
        case R.id.action_manual_settings: {
            Intent intent = new Intent(this, OptionsActivity.class);
            startActivity(intent);
        }
            break;
        case R.id.action_manual_share: {
            if (mMatch != null && mMatch.getMatchInfo() != null) {
                mGoBoard.destroyDrawingCache();
                mGoBoard.setDrawingCacheEnabled(true);
                Bitmap bm = Bitmap.createBitmap(mGoBoard.getDrawingCache());
                FileOutputStream m_fileOutPutStream = null;
                File file = new File(StorageUtil.getDirByType(StorageUtil.DIR_TYPE_IMAGE),
                        System.currentTimeMillis() + ".png");
                try {
                    m_fileOutPutStream = new FileOutputStream(file);
                    bm.compress(CompressFormat.PNG, 50, m_fileOutPutStream);
                    m_fileOutPutStream.flush();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (m_fileOutPutStream != null) {
                        try {
                            m_fileOutPutStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.putExtra(Intent.EXTRA_TEXT, mMatch.getMatchInfo().getMatchName() + " "
                        + mMatch.getMatchInfo().getBlackName() + "vs"
                        + mMatch.getMatchInfo().getWhiteName()
                        + "( @掌中围棋，与您分享精彩棋谱！http://www.appchina.com/app/com.soyomaker.handsgo/ )");
                startActivity(Intent.createChooser(intent, getTitle()));
            } else {
                // TODO toast
            }
        }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void touchPressed(int col, int row) {
        LogUtil.e(TAG, "touchPressed:" + col + "x" + row);
        mGoBoard.postInvalidate();
    }

    @Override
    public void touchReleased(int col, int row) {
        LogUtil.e(TAG, "touchReleased:" + col + "x" + row);
        mGoBoard.postInvalidate();
    }

    @Override
    public void touchMoved(int col, int row) {
        LogUtil.e(TAG, "touchMoved:" + col + "x" + row);
    }

    @Override
    public String getPageName() {
        return "棋谱展示界面";
    }
}
