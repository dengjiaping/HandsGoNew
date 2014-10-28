package com.soyomaker.handsgo.network.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.soyomaker.handsgo.model.SearchResult;
import com.soyomaker.handsgo.model.SearchResultList;
import com.soyomaker.handsgo.util.LogUtil;

public class SearchResultParser implements IParser {

    public static final String TAG = "SearchResultParser";

    protected String result = "";
    protected JSONArray data;

    public SearchResultParser() {
    }

    @Override
    public SearchResultList parse(String jsonString) throws JSONException {
        LogUtil.d(TAG, jsonString);
        SearchResultList searchResultList = new SearchResultList();
        JSONObject jsonObject = new JSONObject(jsonString);
        result = jsonObject.optString("result");
        data = jsonObject.optJSONArray("data");
        if (data != null) {
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            for (int i = 0; i < data.length(); i++) {
                JSONArray resultArray = data.optJSONArray(i);
                if (resultArray != null) {
                    SearchResult result = new SearchResult();
                    result.setId(resultArray.optInt(0));
                    result.setName(resultArray.optString(2));
                    result.setImageUrl("http://www.101weiqi.com/file/" + resultArray.optString(3));
                    results.add(result);
                }
            }
            searchResultList.setResults(results);
        }
        return searchResultList;
    }
}
