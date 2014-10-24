package com.soyomaker.handsgo.model;

import java.io.Serializable;

public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	private int mId;
	private int mUserId;
	private String mUserName;
	private String mComment;
	private String mCommentSgf;
	private String mInsertTime;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mId;
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
		Comment other = (Comment) obj;
		if (mId != other.mId)
			return false;
		return true;
	}

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
