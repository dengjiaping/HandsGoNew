package com.soyomaker.handsgonew.model;

import java.util.Vector;

import android.util.Log;

import com.soyomaker.handsgonew.core.sgf.Node;
import com.soyomaker.handsgonew.core.sgf.SGFTree;
import com.soyomaker.handsgonew.core.sgf.TreeNode;

/**
 * 比赛模型类
 * 
 * @author like
 * 
 */
public class Match {

	private static final String TAG = "Match";

	/** 比赛名称. */
	private String mGameName = "";

	/** 比赛备注. */
	private String mGameComment = "";

	/** 棋盘大小. */
	private int mBoardSize = 19;

	/** 贴目. */
	private String mKomi = "7.5";

	/** 让子数. */
	private String mHandicap = "0";

	/** 比赛日期. */
	private String mDate = "";

	/** 比赛名称. */
	private String mMatchName = "";

	/** 比赛地点. */
	private String mPlace = "";

	/** 比赛结果. */
	private String mResult = "";

	/** 比赛时间. */
	private String mTime = "";

	private String mWhiteName = "";

	private String mWhiteTeam = "";

	private String mWhiteRank = "";

	private String mBlackName = "";

	private String mBlackTeam = "";

	private String mBlackRank = "";

	/** 棋谱内容. */
	public String mSgfSource = "";

	/** 棋谱来源. */
	public String mSource = "";

	private Vector<SGFTree> mSGFTrees;

	public String getGameName() {
		return mGameName;
	}

	public String getGameComment() {
		return mGameComment;
	}

	public String getBlackName() {
		return mBlackName;
	}

	public String getBlackTeam() {
		return mBlackTeam;
	}

	public String getBlackRank() {
		return mBlackRank;
	}

	public int getBoardSize() {
		return mBoardSize;
	}

	public String getKomi() {
		return mKomi;
	}

	public String getTime() {
		return mTime;
	}

	public String getHandicap() {
		return mHandicap;
	}

	public String getDate() {
		return mDate;
	}

	public String getMatchName() {
		return mMatchName;
	}

	public String getPlace() {
		return mPlace;
	}

	public String getResult() {
		return mResult;
	}

	public String getWhiteName() {
		return mWhiteName;
	}

	public String getWhiteTeam() {
		return mWhiteTeam;
	}

	public String getWhiteRank() {
		return mWhiteRank;
	}

	public String getSgfSource() {
		return mSgfSource;
	}

	public void setSgfSource(String sgfSource) {
		this.mSgfSource = sgfSource;
	}

	public String getSource() {
		return mSource;
	}

	public Vector<SGFTree> getSGFTrees() {
		return mSGFTrees;
	}

	public void setSGFTrees(Vector<SGFTree> sgfTrees) {
		this.mSGFTrees = sgfTrees;

		SGFTree sgfTree = (SGFTree) mSGFTrees.elementAt(0);
		TreeNode treeNode = sgfTree.top();
		Node n = treeNode.getNode();
		mGameName = n.getAction("GN");
		mGameComment = n.getAction("GC");
		try {
			mBoardSize = Integer.parseInt(n.getAction("SZ"));
		} catch (Exception e) {
			mBoardSize = 19;
		}
		mKomi = n.getAction("KM");
		mHandicap = n.getAction("HA");
		mDate = n.getAction("DT");
		mMatchName = n.getAction("EV");
		mPlace = n.getAction("PC");
		mResult = n.getAction("RE");
		mTime = n.getAction("TM");
		mWhiteName = n.getAction("PW");
		mWhiteTeam = n.getAction("WT");
		mWhiteRank = n.getAction("WR");
		mBlackName = n.getAction("PB");
		mBlackTeam = n.getAction("BT");
		mBlackRank = n.getAction("BR");
		mSource = n.getAction("SO");

		Log.e(TAG, "mGameName:" + mGameName);
		Log.e(TAG, "mGameComment:" + mGameComment);
		Log.e(TAG, "mBoardSize:" + mBoardSize);
		Log.e(TAG, "mKomi:" + mKomi);
		Log.e(TAG, "mHandicap:" + mHandicap);
		Log.e(TAG, "mDate:" + mDate);
		Log.e(TAG, "mMatchName:" + mMatchName);
		Log.e(TAG, "mPlace:" + mPlace);
		Log.e(TAG, "mResult:" + mResult);
		Log.e(TAG, "mTime:" + mTime);
		Log.e(TAG, "mWhiteName:" + mWhiteName);
		Log.e(TAG, "mWhiteTeam:" + mWhiteTeam);
		Log.e(TAG, "mWhiteRank:" + mWhiteRank);
		Log.e(TAG, "mBlackName:" + mBlackName);
		Log.e(TAG, "mBlackTeam:" + mBlackTeam);
		Log.e(TAG, "mBlackRank:" + mBlackRank);
		Log.e(TAG, "mSource:" + mSource);
	}
}
