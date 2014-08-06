package com.soyomaker.handsgonew.model;

import java.io.Serializable;

/**
 * 棋谱模型类
 * 
 * @author like
 * 
 */
public class ChessManual implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int ONLINE_CHESS_MANUAL = 0;
	public static final int LOCAL_CHESS_MANUAL = 1;

	private int mId = -1;
	private int mType = ONLINE_CHESS_MANUAL;
	private String mSgfUrl;
	private String mSgfContent;
	private String mBlackName;
	private String mWhiteName;
	private String mMatchName;
	private String mMatchResult;
	private String mMatchTime;
	private String mMatchInfo;
	private String mCharset;// 如果是网页棋谱，则表示url页面的字符集，如果是本地棋谱，则表示本地文件的字符集.
	private int mGroupId = 1;

	public int getGroupId() {
		return mGroupId;
	}

	public void setGroupId(int groupId) {
		this.mGroupId = groupId;
	}

	public String getMatchInfo() {
		return mMatchInfo;
	}

	public void setMatchInfo(String matchInfo) {
		this.mMatchInfo = matchInfo;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public String getSgfContent() {
		return mSgfContent;
	}

	public void setSgfContent(String sgfContent) {
		this.mSgfContent = sgfContent;
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getCharset() {
		return mCharset;
	}

	public void setCharset(String charset) {
		this.mCharset = charset;
	}

	public String getBlackName() {
		return mBlackName;
	}

	public void setBlackName(String blackName) {
		this.mBlackName = blackName;
	}

	public String getWhiteName() {
		return mWhiteName;
	}

	public void setWhiteName(String whiteName) {
		this.mWhiteName = whiteName;
	}

	public String getMatchName() {
		return mMatchName;
	}

	public void setMatchName(String matchName) {
		this.mMatchName = matchName;
	}

	public String getMatchResult() {
		return mMatchResult;
	}

	public void setMatchResult(String matchResult) {
		this.mMatchResult = matchResult;
	}

	public String getMatchTime() {
		return mMatchTime;
	}

	public void setMatchTime(String matchTime) {
		this.mMatchTime = matchTime;
	}

	public String getSgfUrl() {
		return mSgfUrl;
	}

	public void setSgfUrl(String sgfUrl) {
		this.mSgfUrl = sgfUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mSgfUrl == null) ? 0 : mSgfUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChessManual other = (ChessManual) obj;
		if (mSgfUrl == null) {
			if (other.mSgfUrl != null)
				return false;
		} else if (!mSgfUrl.equals(other.mSgfUrl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "时间 " + this.mMatchTime + " 比赛 " + this.mMatchName + " 黑方 " + this.mBlackName
				+ " 白方 " + this.mWhiteName + " 结果 " + this.mMatchResult;
	}
}
