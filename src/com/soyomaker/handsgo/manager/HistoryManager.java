package com.soyomaker.handsgo.manager;

import com.soyomaker.handsgo.model.ChessManual;

public class HistoryManager {

	private static HistoryManager mInstance = new HistoryManager();

	private HistoryManager() {
	}

	public static HistoryManager getInstance() {
		return mInstance;
	}

	public boolean isHistory(ChessManual chessManual) {
		return ChessManualServerManager.getHistoryServer().isHistory(chessManual);
	}
}
