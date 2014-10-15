package com.soyomaker.handsgo.model;

import java.io.Serializable;

public class MatchInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 比赛名称. */
    String mGameName = "";

    /** 比赛备注. */
    String mGameComment = "";

    /** 棋盘大小. */
    int mBoardSize = 19;

    /** 贴目. */
    String mKomi = "7.5";

    /** 让子数. */
    String mHandicap = "0";

    /** 比赛日期. */
    String mDate = "";

    /** 比赛名称. */
    String mMatchName = "";

    /** 比赛地点. */
    String mPlace = "";

    /** 比赛结果. */
    String mResult = "";

    /** 比赛时间. */
    String mTime = "";

    String mWhiteName = "";

    String mWhiteTeam = "";

    String mWhiteRank = "";

    String mBlackName = "";

    String mBlackTeam = "";

    String mBlackRank = "";

    /** 棋谱来源. */
    String mSource = "";

    public String getGameName() {
        return mGameName;
    }

    public void setGameName(String mGameName) {
        this.mGameName = mGameName;
    }

    public String getGameComment() {
        return mGameComment;
    }

    public void setGameComment(String mGameComment) {
        this.mGameComment = mGameComment;
    }

    public int getBoardSize() {
        return mBoardSize;
    }

    public void setBoardSize(int mBoardSize) {
        this.mBoardSize = mBoardSize;
    }

    public String getKomi() {
        return mKomi;
    }

    public void setKomi(String mKomi) {
        this.mKomi = mKomi;
    }

    public String getHandicap() {
        return mHandicap;
    }

    public void setHandicap(String mHandicap) {
        this.mHandicap = mHandicap;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getMatchName() {
        return mMatchName;
    }

    public void setMatchName(String mMatchName) {
        this.mMatchName = mMatchName;
    }

    public String getPlace() {
        return mPlace;
    }

    public void setPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String mResult) {
        this.mResult = mResult;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getWhiteName() {
        return mWhiteName;
    }

    public void setWhiteName(String mWhiteName) {
        this.mWhiteName = mWhiteName;
    }

    public String getWhiteTeam() {
        return mWhiteTeam;
    }

    public void setWhiteTeam(String mWhiteTeam) {
        this.mWhiteTeam = mWhiteTeam;
    }

    public String getWhiteRank() {
        return mWhiteRank;
    }

    public void setWhiteRank(String mWhiteRank) {
        this.mWhiteRank = mWhiteRank;
    }

    public String getBlackName() {
        return mBlackName;
    }

    public void setBlackName(String mBlackName) {
        this.mBlackName = mBlackName;
    }

    public String getBlackTeam() {
        return mBlackTeam;
    }

    public void setBlackTeam(String mBlackTeam) {
        this.mBlackTeam = mBlackTeam;
    }

    public String getBlackRank() {
        return mBlackRank;
    }

    public void setBlackRank(String mBlackRank) {
        this.mBlackRank = mBlackRank;
    }

    public String getSource() {
        return mSource;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }
}
