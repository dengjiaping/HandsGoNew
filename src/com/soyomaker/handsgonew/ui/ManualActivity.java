package com.soyomaker.handsgonew.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.core.DefaultBoardModel;
import com.soyomaker.handsgonew.core.GoBoard;
import com.soyomaker.handsgonew.core.GoController;
import com.soyomaker.handsgonew.core.IGridListener;
import com.soyomaker.handsgonew.core.io.SGFReader;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.model.Match;
import com.soyomaker.handsgonew.model.MatchInfo;
import com.soyomaker.handsgonew.util.LogUtil;
import com.soyomaker.handsgonew.util.StorageUtil;
import com.soyomaker.handsgonew.util.StringUtil;

/**
 * 棋谱展示界面
 * 
 * @author like
 * 
 */
public class ManualActivity extends Activity implements IGridListener {

	public static final String EXTRA_CHESSMANUAL = "extra_chessmanual";

	private static final String TAG = "ManualActivity";

	private RelativeLayout mBoardLayout;
	private LinearLayout mCommentLayout;
	private TextView mCommentTextView;

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
	}

	private void initData() {
		Intent intent = getIntent();
		mChessManual = (ChessManual) intent.getSerializableExtra(EXTRA_CHESSMANUAL);
		if (mChessManual == null) {
			// 如果传入的棋谱是空的，多半是本地sgf文件打开请求，这里统一封装为一个mChessManual，进行读取
			Uri uri = intent.getData();
			if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
				File file = new File(uri.getPath());
				mChessManual = new ChessManual();
				mChessManual.setType(ChessManual.LOCAL_CHESS_MANUAL);
				mChessManual.setSgfUrl(file.getAbsolutePath());
				mChessManual.setMatchName(file.getName());
				mChessManual.setCharset(StringUtil.getCharset(file));
			}
		}
		// 如果mChessManual依然为空，说明不合法，直接返回，不进行读取
		if (mChessManual == null) {
			return;
		}
		new Thread() {

			public void run() {
				long time = System.currentTimeMillis();
				mMatch = SGFReader.read(ManualActivity.this, mChessManual);
				LogUtil.e(TAG, "UseTime:" + (System.currentTimeMillis() - time));
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initBoard();
					}
				});
			}
		}.start();
	}

	private void initBoard() {
		MatchInfo matchInfo = mMatch.getMatchInfo();
		if (matchInfo == null) {
			return;
		}
		int size = matchInfo.getBoardSize();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		// 棋盘大小
		int boardSize = Math.min(dm.widthPixels, dm.heightPixels);
		// 棋子大小
		int cubicSize = Math.round(boardSize / (size + 1));

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

		mGoController.setBoardModel(mBoardModel);
		mGoController.setBoardSize(size);

		mGoController.setTreeNode(mMatch.getSGFTrees().elementAt(0).top());
		mGoController.init();
		mCommentTextView.setText(mGoController.getComment());
	}

	private void initView() {
		if (mChessManual == null) {
			return;
		}

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mChessManual.getMatchName());

		mBoardLayout = (RelativeLayout) this.findViewById(R.id.board_layout);
		mCommentLayout = (LinearLayout) this.findViewById(R.id.comment_container);
		mCommentTextView = (TextView) this.findViewById(R.id.text_comment);
		findViewById(R.id.next_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.next();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.prev_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.prev();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.fast_next_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.fastNext();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.fast_prev_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.fastPrev();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.first_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.first();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.last_step).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.last();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
		findViewById(R.id.change_var).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mGoController.changeVar();
				mCommentTextView.setText(mGoController.getComment());
				mGoBoard.postInvalidate();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manual, menu);
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
				// TODO 弹框提示
			}
		}
			break;
		case R.id.action_manual_settings: {
			Intent intent = new Intent(this, OptionsActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_manual_collect: {
			LogUtil.e(TAG, "收藏棋谱");
		}
			break;
		case R.id.action_manual_share: {
			LogUtil.e(TAG, "分享棋谱");
			if (mMatch != null) {
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
			}
		}
			break;
		}
		return super.onOptionsItemSelected(item);
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
}
