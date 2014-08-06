package com.soyomaker.handsgonew.manager;

import com.soyomaker.handsgonew.server.IChessManualServer;
import com.soyomaker.handsgonew.server.XGOOServer;

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

	private ChessManualServerManager() {
		createDefaultChessManualServer();
	}

	private void createDefaultChessManualServer() {
		// 默认使用XGOO棋谱服务器
		mChessManualServer = new XGOOServer();
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
}
