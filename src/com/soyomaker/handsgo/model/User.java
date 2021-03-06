package com.soyomaker.handsgo.model;

public class User {

    public static final int MALE = 0;
    public static final int FEMALE = 1;
    private int mId;
    private String mName;
    private String mPassword;
    private String mEmail;
    private int mSpace;
    private int mLevel;
    private int mScore;
    private int mGender;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public int getSpace() {
        return mSpace;
    }

    public void setSpace(int mSpace) {
        this.mSpace = mSpace;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int mLevel) {
        this.mLevel = mLevel;
    }

    public int getScore() {
        return mScore;
    }

    public void setScore(int mScore) {
        this.mScore = mScore;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int mGender) {
        this.mGender = mGender;
    }
}
