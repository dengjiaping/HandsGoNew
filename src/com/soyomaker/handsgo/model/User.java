package com.soyomaker.handsgo.model;

public class User {

    private String mName;
    private String mPassword;
    private int mSpace;

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
}
