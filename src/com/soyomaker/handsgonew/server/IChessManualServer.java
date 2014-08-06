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
	 * 获取第一页的棋谱数据
	 * 
	 * @return
	 */
	public boolean refresh();

	/**
	 * 获取下一页的棋谱数据
	 * 
	 * @return
	 */
	public boolean loadMore();
}
