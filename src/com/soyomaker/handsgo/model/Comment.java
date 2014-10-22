package com.soyomaker.handsgo.model;

import java.sql.Date;

public class Comment {

	private int mId;
	private int mUserId;
	private String mUserName;
	private String mComment;
	private String mCommentSgf;
	private String mInsertTime;

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getUserId() {
		return mUserId;
	}

	public void setUserId(int mUserId) {
		this.mUserId = mUserId;
	}

	public String getUserName() {
		return mUserName;
	}

	public void setUserName(String mUserName) {
		this.mUserName = mUserName;
	}

	public String getComment() {
		return mComment;
	}

	public void setComment(String mComment) {
		this.mComment = mComment;
	}

	public String getCommentSgf() {
		return mCommentSgf;
	}

	public void setCommentSgf(String mCommentSgf) {
		this.mCommentSgf = mCommentSgf;
	}

	public String getInsertTime() {
		return mInsertTime;
	}

	public void setInsertTime(String mInsertTime) {
		this.mInsertTime = mInsertTime;
	}
}
