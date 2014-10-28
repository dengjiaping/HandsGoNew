package com.soyomaker.handsgo.manager;

import com.soyomaker.handsgo.server.CollectServer;
import com.soyomaker.handsgo.server.HistoryServer;
import com.soyomaker.handsgo.server.SINAServer;
import com.soyomaker.handsgo.server.TOMServer;
import com.soyomaker.handsgo.server.XGOOServer;

/**
 * 棋谱服务器管理器
 * 
 * @author like
 * 
 */
public class ChessManualServerManager {

    private static SINAServer mSinaServer = new SINAServer();

    private static XGOOServer mXgooServer = new XGOOServer();

    private static TOMServer mTomServer = new TOMServer();

    private static HistoryServer mHistoryServer = new HistoryServer();

    private static CollectServer mCollectServer = new CollectServer();

    private ChessManualServerManager() {
    }

    public static SINAServer getSinaServer() {
        return mSinaServer;
    }

    public static TOMServer getTomServer() {
        return mTomServer;
    }

    public static XGOOServer getXgooServer() {
        return mXgooServer;
    }

    public static HistoryServer getHistoryServer() {
        return mHistoryServer;
    }

    public static CollectServer getCollectServer() {
        return mCollectServer;
    }
}
