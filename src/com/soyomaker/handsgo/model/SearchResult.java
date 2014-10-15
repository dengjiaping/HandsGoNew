package com.soyomaker.handsgo.model;

import java.io.Serializable;

/**
 * 棋谱搜索结果模型
 * 
 * @author like
 * 
 */
public class SearchResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private int mId;
    private String mName;
    private String mImageUrl;

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

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
