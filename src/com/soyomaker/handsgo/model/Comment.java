package com.soyomaker.handsgo.model;

public class Comment {

    private int mId;
    private int mUserId;
    private int mUserName;
    private String mComment;
    private String mCommentSgf;
    private long mInsertTime;

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

    public int getUserName() {
        return mUserName;
    }

    public void setUserName(int mUserName) {
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

    public long getInsertTime() {
        return mInsertTime;
    }

    public void setInsertTime(long mInsertTime) {
        this.mInsertTime = mInsertTime;
    }
}
