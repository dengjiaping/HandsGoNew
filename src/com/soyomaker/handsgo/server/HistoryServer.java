package com.soyomaker.handsgo.server;

import java.util.ArrayList;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.reader.IChessManualReader;
import com.soyomaker.handsgo.util.AppConstants;

public class HistoryServer implements IChessManualServer {

    private static final long serialVersionUID = 1L;
    private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();

    public HistoryServer() {
        getChessManuals();
    }

    public boolean isHistory(ChessManual chessManual) {
        return mChessManuals.contains(chessManual);
    }

    public void addHistory(ChessManual chessManual) {
        if (!mChessManuals.contains(chessManual)) {
            DBService.saveHistoryChessManual(chessManual);
            mChessManuals.add(chessManual);
        }
    }

    @Override
    public ArrayList<ChessManual> getChessManuals() {
        mChessManuals.clear();
        mChessManuals.addAll(DBService.getHistoryChessManualCaches());
        return mChessManuals;
    }

    @Override
    public int getTag() {
        return AppConstants.HISTORY;
    }

    @Override
    public String getName() {
        return HandsGoApplication.getAppContext().getString(R.string.title_history);
    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public boolean isLoadingMore() {
        return false;
    }

    @Override
    public boolean refresh() {
        return false;
    }

    @Override
    public boolean loadMore() {
        return false;
    }

    @Override
    public boolean canRefresh() {
        return false;
    }

    @Override
    public boolean canLoadMore() {
        return false;
    }

    @Override
    public boolean canDelete() {
        return true;
    }

    @Override
    public boolean delete(ChessManual chessManual) {
        if (mChessManuals.contains(chessManual)) {
            DBService.deleteHistoryChessManual(chessManual);
        }
        return mChessManuals.remove(chessManual);
    }

    @Override
    public IChessManualReader getReader() {
        return null;
    }

    @Override
    public boolean canCollect() {
        return true;
    }
}
