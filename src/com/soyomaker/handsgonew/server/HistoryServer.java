package com.soyomaker.handsgonew.server;

import java.util.ArrayList;

import com.soyomaker.handsgonew.HandsGoApplication;
import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.db.DBService;
import com.soyomaker.handsgonew.manager.ChessManualServerManager;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.util.AppConstants;

public class HistoryServer implements IChessManualServer {

	private ArrayList<ChessManual> mChessManuals;

	public HistoryServer() {
		mChessManuals = DBService.getAllHistoryChessManual();
	}

	@Override
	public ArrayList<ChessManual> getChessManuals() {
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
}
