package com.soyomaker.handsgonew.server;

import java.util.ArrayList;

import com.soyomaker.handsgonew.model.ChessManual;

/**
 * 棋谱服务器接口
 * 
 * @author like
 * 
 */
public interface IChessManualServer {

	/**
	 * 获取该服务器已读取到的棋谱
	 * 
	 * @return
	 */
	public ArrayList<ChessManual> getChessManuals();

	/**
	 * 获取该服务器的标识TAG
	 * 
	 * @return
	 */
	public int getTag();

	/**
	 * 获取该服务器的名称
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 是否正在刷新
	 * 
	 * @return
	 */
	public boolean isRefreshing();

	/**
	 * 是否正在加载更多
	 * 
	 * @return
	 */
	public boolean isLoadingMore();

	/**
	 * 是否能进行刷新
	 * 
	 * @return
	 */
	public boolean canRefresh();

	/**
	 * 获取第一页的棋谱数据
	 * 
	 * @return
	 */
	public boolean refresh();

	/**
	 * 是否能加载更多
	 * 
	 * @return
	 */
	public boolean canLoadMore();

	/**
	 * 获取下一页的棋谱数据
	 * 
	 * @return
	 */
	public boolean loadMore();

	/**
	 * 是否支持删除
	 * 
	 * @return
	 */
	public boolean canDelete();

	/**
	 * 删除棋谱
	 * 
	 * @param chessManual
	 * @return
	 */
	public boolean delete(ChessManual chessManual);

	/**
	 * 是否支持收藏
	 * 
	 * @return
	 */
	public boolean canCollect();

	/**
	 * 收藏棋谱
	 * 
	 * @param chessManual
	 * @return
	 */
	public boolean collect(ChessManual chessManual);
}
