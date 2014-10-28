package com.soyomaker.handsgo.manager;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.server.IChessManualServer;

/**
 * 棋谱读取器管理器
 * 
 * @author like
 * 
 */
public class ChessManualReaderManager {

    private static ChessManualReaderManager mInstance = new ChessManualReaderManager();
    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private ChessManualReaderManager() {
    }

    public static ChessManualReaderManager getInstance() {
        return mInstance;
    }

    public boolean isRefreshing(IChessManualServer server) {
        return server.isRefreshing();
    }

    public boolean isLoadingMore(IChessManualServer server) {
        return server.isLoadingMore();
    }

    public void refreshChessManuals(final IChessManualServer server,
            final IChessManualsReaderListener listener) {
        new Thread() {

            public void run() {
                final boolean success = server.refresh();
                mMainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (success) {
                            if (listener != null) {
                                listener.readSuccess(server.getChessManuals());
                            }
                        } else {
                            if (listener != null) {
                                listener.readFail();
                            }
                        }
                    }
                });
            }
        }.start();
    }

    public void loadMoreChessManuals(final IChessManualServer server,
            final IChessManualsReaderListener listener) {
        new Thread() {

            public void run() {
                final boolean success = server.loadMore();
                mMainHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (success) {
                            if (listener != null) {
                                listener.readSuccess(server.getChessManuals());
                            }
                        } else {
                            if (listener != null) {
                                listener.readFail();
                            }
                        }
                    }
                });
            }
        }.start();
    }

    public interface IChessManualsReaderListener {

        public void readSuccess(ArrayList<ChessManual> chessManuals);

        public void readFail();
    }
}
