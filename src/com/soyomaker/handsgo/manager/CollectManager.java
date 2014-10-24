package com.soyomaker.handsgo.manager;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.server.CollectServer;

public class CollectManager {

	private static CollectManager mInstance = new CollectManager();
	private CollectServer mCollectServer;

	private CollectManager() {
		mCollectServer = ChessManualServerManager.getCollectServer();
	}

	public static CollectManager getInstance() {
		return mInstance;
	}

	public void collect(ChessManual chessManual) {
		mCollectServer.collect(chessManual);
	}

	public boolean isCollect(ChessManual chessManual) {
		return mCollectServer.isCollect(chessManual);
	}

	public void cancelCollect(ChessManual chessManual) {
		mCollectServer.cancelCollect(chessManual);
	}

	public void addGroup(Group group) {
		mCollectServer.addGroup(group);
	}

	public void updateGroup(Group group) {
		mCollectServer.updateGroup(group);
	}

	public void deleteGroup(Group group) {
		mCollectServer.deleteGroup(group);
	}
}
