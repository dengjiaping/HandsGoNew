package com.soyomaker.handsgo.server;

import java.util.ArrayList;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.reader.IChessManualReader;
import com.soyomaker.handsgo.util.AppConstants;

public class HistoryServer implements IChessManualServer {

    private static final long serialVersionUID = 1L;

    public HistoryServer() {
    }

    @Override
    public ArrayList<ChessManual> getChessManuals() {
        return DBService.getAllHistoryChessManual();
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
        ArrayList<ChessManual> chessManuals = DBService.getAllHistoryChessManual();
        if (chessManuals.contains(chessManual)) {
            DBService.deleteHistoryChessManual(chessManual);
        }
        return chessManuals.remove(chessManual);
    }

    @Override
    public boolean canCollect() {
        return true;
    }

    @Override
    public boolean collect(ChessManual chessManual) {
        ArrayList<ChessManual> chessManuals = ChessManualServerManager.getInstance()
                .getCollectServer().getChessManuals();
        if (!chessManuals.contains(chessManual)) {
            DBService.saveFavoriteChessManual(chessManual);
            chessManuals.add(chessManual);
        }
        return true;
    }

    @Override
    public IChessManualReader getReader() {
        return null;
    }
}
