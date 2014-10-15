package com.soyomaker.handsgo.manager;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.server.CollectServer;
import com.soyomaker.handsgo.server.HistoryServer;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.server.SinaServer;
import com.soyomaker.handsgo.server.TOMServer;
import com.soyomaker.handsgo.server.XGOOServer;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;

/**
 * 棋谱服务器管理器
 * 
 * 负责设置棋谱服务器
 * 
 * @author like
 * 
 */
public class ChessManualServerManager {

    private static ChessManualServerManager mInstance = new ChessManualServerManager();

    private IChessManualServer mChessManualServer;

    private SinaServer mSinaServer = new SinaServer();

    private XGOOServer mXgooServer = new XGOOServer();

    private TOMServer mTomServer = new TOMServer();

    private HistoryServer mHistoryServer = new HistoryServer();

    private CollectServer mCollectServer = new CollectServer();

    private ChessManualServerManager() {
        updateChessManualServer();
    }

    /**
     * 根据AppPrefrence.getChessSource选择对应的棋谱服务器
     */
    public void updateChessManualServer() {
        mChessManualServer = new SinaServer();
        switch (AppPrefrence.getChessSource(HandsGoApplication.getAppContext())) {
        case AppConstants.SINA:
            mChessManualServer = mSinaServer;
            break;
        case AppConstants.XGOO:
            mChessManualServer = mXgooServer;
            break;
        case AppConstants.TOM:
            mChessManualServer = mTomServer;
            break;
        }
    }

    public static ChessManualServerManager getInstance() {
        return mInstance;
    }

    public IChessManualServer getChessManualServer() {
        return mChessManualServer;
    }

    public void setChessManualServer(IChessManualServer chessManualServer) {
        this.mChessManualServer = chessManualServer;
    }

    public HistoryServer getHistoryServer() {
        return mHistoryServer;
    }

    public CollectServer getCollectServer() {
        return mCollectServer;
    }
}
