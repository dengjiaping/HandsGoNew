package com.soyomaker.handsgo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.app.ActionBar;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.core.DefaultBoardModel;
import com.soyomaker.handsgo.core.GoBoard;
import com.soyomaker.handsgo.core.GoController;
import com.soyomaker.handsgo.core.GoController.IBoardChangedListener;
import com.soyomaker.handsgo.core.IGridListener;
import com.soyomaker.handsgo.core.io.SGFReader;
import com.soyomaker.handsgo.core.sgf.SGFTree;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Match;
import com.soyomaker.handsgo.model.MatchInfo;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.LogUtil;
import com.soyomaker.handsgo.util.StorageUtil;
import com.soyomaker.handsgo.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 棋谱展示界面
 * 
 * @author like
 * 
 */
public class ManualActivity extends BaseActivity implements IGridListener {

	public static final String EXTRA_CHESSMANUAL = "extra_chessmanual";

	private static final String TAG = "ManualActivity";

	private RelativeLayout mBoardLayout;
	private LinearLayout mAdLayout;
	private LinearLayout mCommentLayout;
	private TextView mCommentTextView;
	private TextView mStatusTextView;

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
		if (mChessManual != null && TextUtils.isEmpty(mChessManual.getSgfContent())) {
			if (DBService.isFavoriteChessManual(mChessManual)) {
				mChessManual = DBService.getFavoriteChessManual(mChessManual);
			} else if (DBService.isHistoryChessManual(mChessManual)) {
				mChessManual = DBService.getHistoryChessManual(mChessManual);
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

		// 根据在线参数决定是否棋谱加载出来后继续显示广告条
		String adon = MobclickAgent.getConfigParams(this, AppConstants.AD_ON_STRING);
		LogUtil.i(TAG, "广告开关：" + adon);
		if ("false".equals(adon)) {
			mAdLayout.setVisibility(View.GONE);
		}

		return true;
	}

	private void initView() {
		if (mChessManual == null) {
			return;
		}

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(mChessManual.getMatchName());

		mAdLayout = (LinearLayout) this.findViewById(R.id.ad_layout);
		mBoardLayout = (RelativeLayout) this.findViewById(R.id.board_layout);
		mCommentLayout = (LinearLayout) this.findViewById(R.id.comment_container);
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
		if (mChessManual.getType() == ChessManual.ONLINE_CHESS_MANUAL) {
			Button comments = (Button) findViewById(R.id.btn_comments);
			comments.setVisibility(View.VISIBLE);
			comments.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ManualActivity.this, CommentsActivity.class);
					intent.putExtra(CommentsActivity.EXTRA_CHESSMANUAL, mChessManual);
					startActivity(intent);
				}
			});
		}

		// 有米广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		mAdLayout.addView(adView, layoutParams);

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
			if (mMatch != null && mMatch.getMatchInfo() != null) {
				DBService.saveFavoriteChessManual(mChessManual);
				Toast.makeText(ManualActivity.this, R.string.toast_collect_success,
						Toast.LENGTH_LONG).show();
			} else {
				// TODO 弹框提示
			}
		}
			break;
		case R.id.action_manual_share: {
			LogUtil.e(TAG, "分享棋谱");
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
				// TODO 弹框提示
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

	@Override
	public String getPageName() {
		return "棋谱展示界面";
	}
}
