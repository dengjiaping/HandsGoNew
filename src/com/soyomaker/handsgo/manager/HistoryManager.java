package com.soyomaker.handsgo.manager;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.server.HistoryServer;

public class HistoryManager {

    private static HistoryManager mInstance = new HistoryManager();
    private HistoryServer mHistoryServer;

    private HistoryManager() {
        mHistoryServer = ChessManualServerManager.getHistoryServer();
    }

    public static HistoryManager getInstance() {
        return mInstance;
    }

    public boolean isHistory(ChessManual chessManual) {
        return mHistoryServer.isHistory(chessManual);
    }

    public void addHistory(ChessManual chessManual) {
        mHistoryServer.addHistory(chessManual);
    }
}
