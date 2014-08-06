package com.soyomaker.handsgonew.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.core.DefaultBoardModel;
import com.soyomaker.handsgonew.core.GoBoard;
import com.soyomaker.handsgonew.core.IGridListener;
import com.soyomaker.handsgonew.core.io.SGFReader;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.model.Match;
import com.soyomaker.handsgonew.util.LogUtil;

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

	private ChessManual mChessManual;
	private Match mMatch;
	private GoBoard mGoBoard;
	private DefaultBoardModel mBoardModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual);

		initData();
		initView();
	}

	private void initData() {
		mChessManual = (ChessManual) getIntent().getSerializableExtra(EXTRA_CHESSMANUAL);
		if (mChessManual == null) {
			return;
		}
		new Thread() {

			public void run() {
				mMatch = SGFReader.read(ManualActivity.this, mChessManual);
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
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int boardSize = Math.min(dm.widthPixels, dm.heightPixels);
		int cubicSize = Math.round(boardSize / (mMatch.getBoardSize() + 1));

		mBoardModel = new DefaultBoardModel(mMatch.getBoardSize());
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
	}

	private void initView() {
		if (mChessManual == null) {
			return;
		}
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mChessManual.getMatchName());

		mBoardLayout = (RelativeLayout) this.findViewById(R.id.board_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.manual, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_manual_match_info: {
			Intent intent = new Intent(this, ManualInfoActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_manual_settings: {
			Intent intent = new Intent(this, OptionsActivity.class);
			startActivity(intent);
		}
			break;
		case R.id.action_manual_auto: {
			LogUtil.e(TAG, "自动打谱");
		}
			break;
		case R.id.action_manual_collect: {
			LogUtil.e(TAG, "收藏棋谱");
		}
			break;
		case R.id.action_manual_share: {
			LogUtil.e(TAG, "分享棋谱");
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
	}

	@Override
	public void touchReleased(int col, int row) {
		LogUtil.e(TAG, "touchReleased:" + col + "x" + row);
	}

	@Override
	public void touchMoved(int col, int row) {
		LogUtil.e(TAG, "touchMoved:" + col + "x" + row);
	}
}
