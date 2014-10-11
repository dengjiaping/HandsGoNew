package com.soyomaker.handsgo.core;

import android.os.Handler;
import android.text.TextUtils;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.core.sgf.ActionBase;
import com.soyomaker.handsgo.core.sgf.Change;
import com.soyomaker.handsgo.core.sgf.Field;
import com.soyomaker.handsgo.core.sgf.ListElement;
import com.soyomaker.handsgo.core.sgf.Node;
import com.soyomaker.handsgo.core.sgf.TreeNode;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.PlaySoundPool;

/**
 * 打谱控制器
 * 
 * TODO 快进，快退的步数支持自定义
 * 
 * @author like
 * 
 */
public class GoController {

	private static final String TAG = "GoController";
	// 看谱模式
	/**
	 * 看谱模式
	 */
	public static final int STATE_LOOK = 0;
	// 打谱模式
	/**
	 * 设置黑子
	 */
	public static final int STATE_SET_BLACK = 1;
	/**
	 * 设置白子
	 */
	public static final int STATE_SET_WHITE = 2;
	/**
	 * 设置黑白
	 */
	public static final int STATE_SET_BLACK_WHITE = 3;
	/**
	 * 清除棋子
	 */
	public static final int STATE_REMOVE_STONE = 4;
	/**
	 * 清空棋盘
	 */
	public static final int STATE_RESET = 5;

	private TreeNode mTreeNode;
	private DefaultBoardModel mBoardModel;
	private int mBoardSize;
	private String mComment;

	private int mNumber = 0;
	private int mCaptured = 0;
	private int mLasti = -1, mLastj = 0;

	private int mState = STATE_LOOK;

	private PlaySoundPool mPlaySoundPool;

	private Handler mHandler;

	private IBoardChangedListener mListener;

	public interface IBoardChangedListener {

		public void onBoardChanged();
	}

	public GoController() {
		this.mPlaySoundPool = new PlaySoundPool(HandsGoApplication.getAppContext());
		this.mPlaySoundPool.loadSfx(R.raw.stone, 1);

		this.mHandler = new Handler();
	}

	public IBoardChangedListener getBoardChangedListener() {
		return mListener;
	}

	public void setBoardChangedListener(IBoardChangedListener mListener) {
		this.mListener = mListener;
	}

	public void pauseAutoNext() {
		mHandler.removeCallbacks(doAutoNextRunnable);
	}

	public void resumeAutoNext() {
		doAutoNext();
	}

	private void doAutoNext() {
		String interval = AppPrefrence.getAutoNextInterval(HandsGoApplication.getAppContext());
		int intervalInt = 2000;
		try {
			intervalInt = Integer.valueOf(interval);
		} catch (Exception e) {
			intervalInt = 2000;
		}
		if (intervalInt <= 0) {
			intervalInt = 1;
		}
		mHandler.removeCallbacks(doAutoNextRunnable);
		mHandler.postDelayed(doAutoNextRunnable, intervalInt);
	}

	private Runnable doAutoNextRunnable = new Runnable() {

		@Override
		public void run() {
			boolean flag = AppPrefrence.getAutoNext(HandsGoApplication.getAppContext());
			if (flag) {

				next();

				doAutoNext();
			}
		}
	};

	public String getComment() {
		return mComment;
	}

	/**
	 * 下一步
	 */
	public void next() {
		goForward();
		updateComment();

		boolean flag = AppPrefrence.getLazySound(HandsGoApplication.getAppContext());
		if (flag) {
			mPlaySoundPool.play(1, 0);
		}

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 上一步
	 */
	public void prev() {
		goBack();
		updateComment();

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 快进
	 */
	public void fastNext() {
		for (int i = 0; i < 10; i++) {
			goForward();
		}
		updateComment();

		boolean flag = AppPrefrence.getLazySound(HandsGoApplication.getAppContext());
		if (flag) {
			mPlaySoundPool.play(1, 0);
		}

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 快退
	 */
	public void fastPrev() {
		for (int i = 0; i < 10; i++) {
			goBack();
		}
		updateComment();

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 后退到开局
	 */
	public void first() {
		while (mTreeNode.getParentPos() != null) {
			goBack();
		}
		updateComment();

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 快进到终局
	 */
	public void last() {
		while (mTreeNode.hasChildren()) {
			goForward();
		}
		updateComment();

		boolean flag = AppPrefrence.getLazySound(HandsGoApplication.getAppContext());
		if (flag) {
			mPlaySoundPool.play(1, 0);
		}

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 切换分支
	 */
	public void changeVar() {
		ListElement l = mTreeNode.getListElement();
		if (l == null) {
			return;
		}
		if (l.getNext() == null) {
			// 没有下一分支，则移到第一分支
			firstVar();
		} else {
			// 如有下一分支，则移到下一分支
			nextVar();
		}
		setNode();
		updateComment();

		if (mListener != null) {
			mListener.onBoardChanged();
		}
	}

	/**
	 * 设置打谱模式
	 * 
	 * @param state
	 */
	public void setState(int state) {
		mState = state;
	}

	/**
	 * 获取打谱模式
	 * 
	 * @return
	 */
	public int getState() {
		return mState;
	}

	public void touch(int i, int j) {
		switch (mState) {
		case STATE_REMOVE_STONE:
			deleteStone(i, j);
			break;
		case STATE_SET_BLACK:
			addBlack(i, j);
			break;
		case STATE_SET_WHITE:
			addWhite(i, j);
			break;
		case STATE_SET_BLACK_WHITE:
			if (mBoardModel.getNextPlayer() == GoPoint.PLAYER_BLACK) {
				addBlack(i, j);
			} else {
				addWhite(i, j);
			}
			break;
		}
	}

	/**
	 * 添加黑子
	 * 
	 * @param i
	 * @param j
	 */
	public void addBlack(int i, int j) {
		GoPoint goPoint = mBoardModel.getPoint(i, j);
		if (goPoint.getPlayer() == GoPoint.PLAYER_EMPTY) {
			goPoint = new GoPoint();
			goPoint.setPlayer(GoPoint.PLAYER_BLACK);
			mBoardModel.forcePut(i, j, goPoint);

			mBoardModel.setNextPlayer(GoPoint.PLAYER_WHITE);
		}
	}

	/**
	 * 添加白子
	 * 
	 * @param i
	 * @param j
	 */
	public void addWhite(int i, int j) {
		GoPoint goPoint = mBoardModel.getPoint(i, j);
		if (goPoint.getPlayer() == GoPoint.PLAYER_EMPTY) {
			goPoint = new GoPoint();
			goPoint.setPlayer(GoPoint.PLAYER_WHITE);
			mBoardModel.forcePut(i, j, goPoint);

			mBoardModel.setNextPlayer(GoPoint.PLAYER_BLACK);
		}
	}

	/**
	 * 删除棋子
	 * 
	 * @param i
	 * @param j
	 */
	public void deleteStone(int i, int j) {
		GoPoint goPoint = mBoardModel.getPoint(i, j);
		if (goPoint.getPlayer() != GoPoint.PLAYER_EMPTY) {
			mBoardModel.forceRemove(i, j);
		}
	}

	/**
	 * 重置棋盘
	 */
	public void reset() {
		mBoardModel.reset(this.mBoardSize);
	}

	/**
	 * 第一分支
	 */
	public void firstVar() {
		ListElement l = mTreeNode.getListElement();
		if (l == null) {
			return;
		}
		goBack();
		mTreeNode = mTreeNode.getFirstChildPos();
	}

	/**
	 * 上一分支
	 */
	public void lastVar() {
		ListElement l = mTreeNode.getListElement();
		if (l == null) {
			return;
		}
		goBack();
		mTreeNode = mTreeNode.getLastChildPos();
	}

	/**
	 * 下一分支
	 */
	public void nextVar() {
		ListElement l = mTreeNode.getListElement();
		if (l == null) {
			return;
		}
		if (l.getNext() == null) {
			return;
		}
		TreeNode newpos = (TreeNode) l.getNext().getContent();
		goBack();
		mTreeNode = newpos;
	}

	/**
	 * 前一分支
	 */
	public void prevVar() {
		ListElement l = mTreeNode.getListElement();
		if (l == null) {
			return;
		}
		if (l.getPrevious() == null) {
			return;
		}
		TreeNode newpos = (TreeNode) l.getPrevious().getContent();
		goBack();
		mTreeNode = newpos;
	}

	public DefaultBoardModel getBoardModel() {
		return mBoardModel;
	}

	public void setBoardModel(DefaultBoardModel boardModel) {
		mBoardModel = boardModel;
	}

	public TreeNode getTreeNode() {
		return mTreeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		mTreeNode = treeNode;
	}

	public int getBoardSize() {
		return mBoardSize;
	}

	public void setBoardSize(int mBoardSize) {
		this.mBoardSize = mBoardSize;
	}

	public void init() {
		setNode();
		updateComment();
	}

	private void setNode() {
		Node n = mTreeNode.getNode();
		ListElement p = n.getActions();
		n.clearChanges();
		n.mPw = n.mPb = 0;
		p = n.getActions();
		while (p != null) {
			ActionBase a = (ActionBase) p.getContent();
			String type = a.getType();
			if (type.equals("B")) {
				setAction(n, a, GoPoint.PLAYER_BLACK);
			} else if (type.equals("W")) {
				setAction(n, a, GoPoint.PLAYER_WHITE);
			} else if (type.equals("AB")) {
				placeAction(n, a, GoPoint.PLAYER_BLACK);
			} else if (type.equals("AW")) {
				placeAction(n, a, GoPoint.PLAYER_WHITE);
			} else if (type.equals("AE")) {
				emptyAction(n, a);
			}
			p = p.getNext();
		}
	}

	private void undoNode() {
		Node n = mTreeNode.getNode();
		ListElement p = n.getLastChange();
		while (p != null) {
			Change change = (Change) p.getContent();
			GoPoint goPoint = new GoPoint();
			goPoint.setPlayer(change.color);
			goPoint.setNumber(change.number);
			if (change.color == GoPoint.PLAYER_EMPTY) {
				mBoardModel.forceRemove(change.x, change.y);
			} else {
				mBoardModel.forcePut(change.x, change.y, goPoint);
			}
			p = p.getPrevious();
		}
		n.clearChanges();
	}

	private void setAction(Node n, ActionBase a, int c) {
		String s = (String) a.getArguments().getContent();
		int i = Field.i(s);
		int j = Field.j(s);
		if (!valid(i, j)) {
			return;
		}
		GoPoint goPoint = mBoardModel.getPoint(i, j);
		n.addChange(new Change(i, j, goPoint.getPlayer(), goPoint.getNumber()));

		if (c == GoPoint.PLAYER_EMPTY) {
			try {
				mBoardModel.performRemove(i, j);
			} catch (GoException e) {
				e.printStackTrace();
			}
		} else {
			goPoint = new GoPoint();
			goPoint.setPlayer(c);
			goPoint.setNumber(n.getNumber() - 1);
			try {
				mBoardModel.performPut(i, j, goPoint);
			} catch (GoException e) {
				e.printStackTrace();
			}
		}

		mLasti = i;
		mLastj = j;

		mBoardModel.setNextPlayer(-c);

		capture(i, j, n);
	}

	private void placeAction(Node n, ActionBase a, int c) {
		int i, j;
		ListElement larg = a.getArguments();
		while (larg != null) {
			String s = (String) larg.getContent();
			i = Field.i(s);
			j = Field.j(s);
			if (valid(i, j)) {
				GoPoint goPoint = mBoardModel.getPoint(i, j);
				n.addChange(new Change(i, j, goPoint.getPlayer(), goPoint.getNumber()));

				goPoint = new GoPoint();
				goPoint.setPlayer(c);
				mBoardModel.forcePut(i, j, goPoint);
			}
			larg = larg.getNext();
		}
	}

	private void emptyAction(Node n, ActionBase a) {
		int i, j;
		ListElement larg = a.getArguments();
		while (larg != null) {
			String s = (String) larg.getContent();
			i = Field.i(s);
			j = Field.j(s);
			if (valid(i, j)) {
				GoPoint goPoint = mBoardModel.getPoint(i, j);
				n.addChange(new Change(i, j, goPoint.getPlayer(), goPoint.getNumber()));
				if (goPoint.getPlayer() < 0) {
					n.mPw++;
				} else if (goPoint.getPlayer() > 0) {
					n.mPb++;
				}

				mBoardModel.forceRemove(i, j);
			}
			larg = larg.getNext();
		}
	}

	private void capture(int i, int j, Node n) {
		GoPoint goPoint = mBoardModel.getPoint(i, j);
		int c = -goPoint.getPlayer();
		mCaptured = 0;
		if (i > 0) {
			captureGroup(i - 1, j, c, n);
		}
		if (j > 0) {
			captureGroup(i, j - 1, c, n);
		}
		if (i < mBoardSize - 1) {
			captureGroup(i + 1, j, c, n);
		}
		if (j < mBoardSize - 1) {
			captureGroup(i, j + 1, c, n);
		}
		if (goPoint.getPlayer() == -c) {
			captureGroup(i, j, -c, n);
		}
		if (mCaptured == 1 && mBoardModel.count(i, j) != 1) {
			mCaptured = 0;
		}
	}

	private void captureGroup(int i, int j, int c, Node n) {
		int ii, jj;
		if (mBoardModel.getPoint(i, j).getPlayer() != c) {
			return;
		}
		if (!mBoardModel.markGroupTest(i, j, 0)) {
			for (ii = 0; ii < mBoardSize; ii++)
				for (jj = 0; jj < mBoardSize; jj++) {
					GoPoint goPoint = mBoardModel.getPoint(ii, jj);
					if (goPoint.isMarked()) {
						n.addChange(new Change(ii, jj, goPoint.getPlayer(), goPoint.getNumber()));
						if (goPoint.getPlayer() > 0) {
							n.mPb++;
						} else {
							n.mPw++;
						}

						mBoardModel.forceRemove(ii, jj);
						mCaptured++;
					}
				}
		}
	}

	private boolean valid(int i, int j) {
		return i >= 0 && i < mBoardSize && j >= 0 && j < mBoardSize;
	}

	private void setLast() {
		Node n = mTreeNode.getNode();
		ListElement l = n.getActions();
		ActionBase a;
		String s;
		int i = mLasti, j = mLastj;
		mLasti = -1;
		mLastj = -1;
		while (l != null) {
			a = (ActionBase) l.getContent();
			if (a.getType().equals("B") || a.getType().equals("W")) {
				s = (String) a.getArguments().getContent();
				i = Field.i(s);
				j = Field.j(s);
				if (valid(i, j)) {
					mLasti = i;
					mLastj = j;
					mBoardModel.setNextPlayer(-mBoardModel.getPoint(i, j).getPlayer());
				}
			}
			l = l.getNext();
		}
		mNumber = n.getNumber();
	}

	private void goForward() {
		if (!mTreeNode.hasChildren()) {
			return;
		}
		mTreeNode = mTreeNode.getFirstChildPos();
		setNode();
		setLast();
	}

	private void goBack() {
		if (mTreeNode.getParentPos() == null) {
			return;
		}
		undoNode();
		mTreeNode = mTreeNode.getParentPos();
		setLast();
	}

	private void updateComment() {
		Node n = mTreeNode.getNode();
		int i, j;
		// delete all marks and variations
		for (i = 0; i < mBoardSize; i++) {
			for (j = 0; j < mBoardSize; j++) {
				GoPoint goPoint = mBoardModel.getPoint(i, j);
				if (goPoint.getTreeNode() != null) {
					mBoardModel.getPoint(i, j).setTreeNode(null);
				}
				if (goPoint.getMark() != GoPoint.NONE) {
					mBoardModel.getPoint(i, j).setMark(GoPoint.NONE);
				}
				if (goPoint.getLetter() != GoPoint.NONE) {
					mBoardModel.getPoint(i, j).setLetter(0);
				}
				if (!TextUtils.isEmpty(goPoint.getLabel())) {
					mBoardModel.getPoint(i, j).setLabel("");
				}
			}
		}
		ListElement la = n.getActions();
		ActionBase a;
		String s;
		String sc = "";
		int let = 1;
		// setup the marks and letters
		while (la != null) {
			a = (ActionBase) la.getContent();
			String type = a.getType();
			if (type.equals("C")) {
				sc = (String) a.getArguments().getContent();
			} else if (type.equals("SQ") || type.equals("SL")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						mBoardModel.getPoint(i, j).setMark(GoPoint.SQUARE);
					}
					larg = larg.getNext();
				}
			} else if (type.equals("MA") || type.equals("M") || type.equals("TW")
					|| type.equals("TB")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						mBoardModel.getPoint(i, j).setMark(GoPoint.CROSS);
					}
					larg = larg.getNext();
				}
			} else if (type.equals("TR")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						mBoardModel.getPoint(i, j).setMark(GoPoint.TRIANGLE);
					}
					larg = larg.getNext();
				}
			} else if (type.equals("CR")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						mBoardModel.getPoint(i, j).setMark(GoPoint.CIRCLE);
					}
					larg = larg.getNext();
				}
			} else if (type.equals("L")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						mBoardModel.getPoint(i, j).setLetter(let);
						let++;
					}
					larg = larg.getNext();
				}
			} else if (type.equals("LB")) {
				ListElement larg = a.getArguments();
				while (larg != null) {
					s = (String) larg.getContent();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j) && s.length() >= 4 && s.charAt(2) == ':') {
						mBoardModel.getPoint(i, j).setLabel(s.substring(3));
					}
					larg = larg.getNext();
				}
			}
			la = la.getNext();
		}
		TreeNode p;
		ListElement l = null;
		p = mTreeNode.getParentPos();
		if (p != null) {
			l = p.getFirstChildPos().getListElement();
		}
		while (l != null) {
			p = (TreeNode) l.getContent();
			if (p != mTreeNode) {
				la = p.getNode().getActions();
				while (la != null) {
					a = (ActionBase) la.getContent();
					String type = a.getType();
					if (type.equals("W") || type.equals("B")) {
						s = (String) a.getArguments().getContent();
						i = Field.i(s);
						j = Field.j(s);
						if (valid(i, j)) {
							mBoardModel.getPoint(i, j).setTreeNode(p);
						}
						break;
					}
					la = la.getNext();
				}
			}
			l = l.getNext();
		}
		mComment = sc;
	}
}
