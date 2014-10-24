package com.soyomaker.handsgo.server;

import java.util.ArrayList;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.reader.IChessManualReader;
import com.soyomaker.handsgo.util.AppConstants;

public class CollectServer implements IChessManualServer {

	private static final long serialVersionUID = 1L;
	private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();
	private ArrayList<Group> mGroups = new ArrayList<Group>();

	public CollectServer() {
		getChessManuals();
	}

	public boolean isCollect(ChessManual chessManual) {
		return mChessManuals.contains(chessManual);
	}

	public void collect(ChessManual chessManual) {
		if (!mChessManuals.contains(chessManual)) {
			DBService.saveFavoriteChessManual(chessManual);
			mChessManuals.add(chessManual);
		}
	}

	public void cancelCollect(ChessManual chessManual) {
		if (mChessManuals.contains(chessManual)) {
			DBService.deleteFavoriteChessManual(chessManual);
			mChessManuals.remove(chessManual);
		}
	}

	public void addGroup(Group group) {
		if (!mGroups.contains(group)) {
			DBService.saveGroup(group);
			mGroups.add(group);
		}
	}

	public void updateGroup(Group group) {
		int index = mGroups.indexOf(group);
		if (index > 0) {
			DBService.saveGroup(group);
			mGroups.set(index, group);
		}
	}

	public void deleteGroup(Group group) {
		if (mGroups.contains(group)) {
			DBService.deleteGroup(group);
			mGroups.remove(group);
		}
	}

	public ArrayList<Group> getGroups() {
		mGroups.clear();
		mGroups.addAll(DBService.getGroupCaches());
		return mGroups;
	}

	@Override
	public ArrayList<ChessManual> getChessManuals() {
		mChessManuals.clear();
		mChessManuals.addAll(DBService.getFavoriteChessManualCaches());
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
	public IChessManualReader getReader() {
		return null;
	}

	@Override
	public boolean canCollect() {
		return false;
	}
}
