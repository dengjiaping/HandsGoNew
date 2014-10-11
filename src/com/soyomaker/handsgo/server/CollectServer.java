package com.soyomaker.handsgo.server;

import java.util.ArrayList;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.util.AppConstants;

public class CollectServer implements IChessManualServer {

	private ArrayList<ChessManual> mChessManuals;

	public CollectServer() {
		mChessManuals = DBService.getAllFavoriteChessManual();
	}

	@Override
	public ArrayList<ChessManual> getChessManuals() {
		return mChessManuals;
	}

	@Override
	public int getTag() {
		return AppConstants.COLLECT;
	}

	@Override
	public String getName() {
		return HandsGoApplication.getAppContext().getString(R.string.title_collect);
	}

	@Override
	public boolean isRefreshing() {
		return false;
	}

	@Override
	public boolean canRefresh() {
		return false;
	}

	@Override
	public boolean isLoadingMore() {
		return false;
	}

	@Override
	public boolean canLoadMore() {
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
	public boolean canDelete() {
		return true;
	}

	@Override
	public boolean delete(ChessManual chessManual) {
		if (mChessManuals.contains(chessManual)) {
			DBService.deleteFavoriteChessManual(chessManual);
		}
		return mChessManuals.remove(chessManual);
	}

	@Override
	public boolean canCollect() {
		return false;
	}

	@Override
	public boolean collect(ChessManual chessManual) {
		return false;
	}
}
