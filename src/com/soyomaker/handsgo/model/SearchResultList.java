package com.soyomaker.handsgo.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SearchResultList implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<SearchResult> mSearchResults = new ArrayList<SearchResult>();

    public ArrayList<SearchResult> getSearchResults() {
        return mSearchResults;
    }

    public void setResults(ArrayList<SearchResult> mSearchResults) {
        this.mSearchResults = mSearchResults;
    }
}
