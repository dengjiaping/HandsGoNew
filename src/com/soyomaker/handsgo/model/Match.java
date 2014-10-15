package com.soyomaker.handsgo.model;

import java.util.Vector;

import android.util.Log;

import com.soyomaker.handsgo.core.sgf.Node;
import com.soyomaker.handsgo.core.sgf.SGFTree;
import com.soyomaker.handsgo.core.sgf.TreeNode;

/**
 * 比赛模型类
 * 
 * @author like
 * 
 */
public class Match {

    private static final String TAG = "Match";

    private Vector<SGFTree> mSGFTrees;

    private MatchInfo mMatchInfo = new MatchInfo();

    /** 棋谱内容. */
    private String mSgfSource = "";

    public String getSgfSource() {
        return mSgfSource;
    }

    public void setSgfSource(String mSgfSource) {
        this.mSgfSource = mSgfSource;
    }

    public MatchInfo getMatchInfo() {
        return mMatchInfo;
    }

    public void setMatchInfo(MatchInfo info) {
        mMatchInfo = info;
    }

    public Vector<SGFTree> getSGFTrees() {
        return mSGFTrees;
    }

    public void setSGFTrees(Vector<SGFTree> sgfTrees) {
        this.mSGFTrees = sgfTrees;
        if (mSGFTrees.isEmpty()) {
            return;
        }
        SGFTree sgfTree = (SGFTree) mSGFTrees.elementAt(0);
        TreeNode treeNode = sgfTree.top();
        Node n = treeNode.getNode();
        mMatchInfo.mGameName = n.getAction("GN");
        mMatchInfo.mGameComment = n.getAction("GC");
        try {
            mMatchInfo.mBoardSize = Integer.parseInt(n.getAction("SZ"));
        } catch (Exception e) {
            mMatchInfo.mBoardSize = 19;
        }
        mMatchInfo.mKomi = n.getAction("KM");
        mMatchInfo.mHandicap = n.getAction("HA");
        mMatchInfo.mDate = n.getAction("DT");
        mMatchInfo.mMatchName = n.getAction("EV");
        mMatchInfo.mPlace = n.getAction("PC");
        mMatchInfo.mResult = n.getAction("RE");
        mMatchInfo.mTime = n.getAction("TM");
        mMatchInfo.mWhiteName = n.getAction("PW");
        mMatchInfo.mWhiteTeam = n.getAction("WT");
        mMatchInfo.mWhiteRank = n.getAction("WR");
        mMatchInfo.mBlackName = n.getAction("PB");
        mMatchInfo.mBlackTeam = n.getAction("BT");
        mMatchInfo.mBlackRank = n.getAction("BR");
        mMatchInfo.mSource = n.getAction("SO");

        Log.e(TAG, "mGameName:" + mMatchInfo.mGameName);
        Log.e(TAG, "mGameComment:" + mMatchInfo.mGameComment);
        Log.e(TAG, "mBoardSize:" + mMatchInfo.mBoardSize);
        Log.e(TAG, "mKomi:" + mMatchInfo.mKomi);
        Log.e(TAG, "mHandicap:" + mMatchInfo.mHandicap);
        Log.e(TAG, "mDate:" + mMatchInfo.mDate);
        Log.e(TAG, "mMatchName:" + mMatchInfo.mMatchName);
        Log.e(TAG, "mPlace:" + mMatchInfo.mPlace);
        Log.e(TAG, "mResult:" + mMatchInfo.mResult);
        Log.e(TAG, "mTime:" + mMatchInfo.mTime);
        Log.e(TAG, "mWhiteName:" + mMatchInfo.mWhiteName);
        Log.e(TAG, "mWhiteTeam:" + mMatchInfo.mWhiteTeam);
        Log.e(TAG, "mWhiteRank:" + mMatchInfo.mWhiteRank);
        Log.e(TAG, "mBlackName:" + mMatchInfo.mBlackName);
        Log.e(TAG, "mBlackTeam:" + mMatchInfo.mBlackTeam);
        Log.e(TAG, "mBlackRank:" + mMatchInfo.mBlackRank);
        Log.e(TAG, "mSource:" + mMatchInfo.mSource);
    }
}
