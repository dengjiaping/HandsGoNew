package com.soyomaker.handsgo.model;

import java.util.ArrayList;

/**
 * 棋谱分组.
 * 
 * @author like
 * 
 */
public class Group {

    public static final int DEFAULT_GROUP = 1;

    private int mId = -1;

    private String mName;

    private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();

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

    public ArrayList<ChessManual> getChessManuals() {
        return mChessManuals;
    }

    public void setChessManuals(ArrayList<ChessManual> mChessManuals) {
        this.mChessManuals = mChessManuals;
    }

    public String toString() {
        return mName + "[" + mChessManuals.size() + "]";
    }
}
