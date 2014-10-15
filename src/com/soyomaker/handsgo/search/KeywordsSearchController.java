package com.soyomaker.handsgo.search;

import java.util.ArrayList;

import android.text.TextUtils;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.reader.XGOOReader;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.server.XGOOServer;

/**
 * 关键字搜索控制器
 * <p/>
 * 负责XGOO服务器的棋谱搜索
 * 
 * @author like
 * 
 */
public class KeywordsSearchController {

    private static final KeywordsSearchController mInstance = new KeywordsSearchController();

    private XGOOServer mXgooServer = new XGOOServer();

    private KeywordsSearchController() {
    }

    public static KeywordsSearchController getInstance() {
        return mInstance;
    }

    public boolean isSearching() {
        return mXgooServer.isRefreshing();
    }

    public boolean isLoadingMore() {
        return mXgooServer.isLoadingMore();
    }

    /**
     * 查找搜索结果
     * 
     * @param key
     * @param listener
     */
    public boolean find(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        String search = key.replaceAll("\\s{1,}", "+");
        XGOOReader reader = (XGOOReader) mXgooServer.getReader();
        reader.setSearchString(search);
        mXgooServer.refresh();
        return true;
    }

    public void loadMore() {
        mXgooServer.loadMore();
    }

    public ArrayList<ChessManual> getChessManuals() {
        return mXgooServer.getChessManuals();
    }

    public IChessManualServer getServer() {
        return mXgooServer;
    }
}
