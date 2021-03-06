package com.soyomaker.handsgo.server;

import java.io.IOException;
import java.util.ArrayList;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.reader.IChessManualReader;
import com.soyomaker.handsgo.reader.SinaReader;
import com.soyomaker.handsgo.util.AppConstants;

public class SINAServer implements IChessManualServer {

    private static final long serialVersionUID = 1L;
    private static final int FIRST_PAGE = 0;
    private int mPage = FIRST_PAGE;
    private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();
    private SinaReader mSinaReader = new SinaReader();
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;

    @Override
    public String getName() {
        return HandsGoApplication.getAppContext().getString(R.string.sina_server);
    }

    @Override
    public ArrayList<ChessManual> getChessManuals() {
        return mChessManuals;
    }

    @Override
    public boolean refresh() {
        boolean success = false;
        if (!mIsRefreshing) {
            mIsRefreshing = true;

            try {
                ArrayList<ChessManual> chessManuals = mSinaReader.readChessManuals(
                        HandsGoApplication.getAppContext(), FIRST_PAGE);
                if (!chessManuals.isEmpty()) {
                    mPage = FIRST_PAGE;
                    mChessManuals.clear();
                    mChessManuals.addAll(chessManuals);
                    success = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mIsRefreshing = false;
            return success;
        }
        return success;
    }

    @Override
    public boolean loadMore() {
        boolean success = false;
        if (!mIsLoadingMore) {
            mIsLoadingMore = true;

            try {
                ArrayList<ChessManual> chessManuals = mSinaReader.readChessManuals(
                        HandsGoApplication.getAppContext(), mPage + 1);
                if (!chessManuals.isEmpty()) {
                    mPage++;// 读取成功之后再+1
                    mChessManuals.addAll(chessManuals);
                    success = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mIsLoadingMore = false;
            return success;
        }
        return success;
    }

    @Override
    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    @Override
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    @Override
    public int getTag() {
        return AppConstants.SINA;
    }

    @Override
    public boolean canRefresh() {
        return true;
    }

    @Override
    public boolean canLoadMore() {
        return true;
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean delete(ChessManual chessManual) {
        return false;
    }

    @Override
    public IChessManualReader getReader() {
        return mSinaReader;
    }

    @Override
    public boolean canCollect() {
        return true;
    }
}
