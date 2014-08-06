package com.soyomaker.handsgonew.manager;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;

import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.server.IChessManualServer;

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

	public boolean isRefreshing() {
		final IChessManualServer chessManualServer = ChessManualServerManager.getInstance()
				.getChessManualServer();
		return chessManualServer.isRefreshing();
	}

	public boolean isLoadingMore() {
		final IChessManualServer chessManualServer = ChessManualServerManager.getInstance()
				.getChessManualServer();
		return chessManualServer.isLoadingMore();
	}

	public void refreshChessManuals(final IChessManualsReaderListener listener) {
		final IChessManualServer chessManualServer = ChessManualServerManager.getInstance()
				.getChessManualServer();
		new Thread() {

			public void run() {
				boolean success = chessManualServer.refresh();
				if (success) {
					mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.readSuccess(chessManualServer.getChessManuals());
						}
					});
				} else {
					mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.readFail();
						}
					});
				}
			}
		}.start();
	}

	public void loadMoreChessManuals(final IChessManualsReaderListener listener) {
		final IChessManualServer chessManualServer = ChessManualServerManager.getInstance()
				.getChessManualServer();
		new Thread() {

			public void run() {
				boolean success = chessManualServer.loadMore();
				if (success) {
					mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.readSuccess(chessManualServer.getChessManuals());
						}
					});
				} else {
					mMainHandler.post(new Runnable() {

						@Override
						public void run() {
							listener.readFail();
						}
					});
				}
			}
		}.start();
	}

	public interface IChessManualsReaderListener {

		public void readSuccess(ArrayList<ChessManual> chessManuals);

		public void readFail();
	}
}
