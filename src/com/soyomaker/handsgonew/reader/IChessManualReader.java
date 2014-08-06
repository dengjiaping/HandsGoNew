package com.soyomaker.handsgonew.reader;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.soyomaker.handsgonew.model.ChessManual;

/**
 * 棋谱读取器接口
 * 
 * @author like
 * 
 */
public interface IChessManualReader {

	/**
	 * 读取指定页的棋谱
	 * 
	 * @param context
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public ArrayList<ChessManual> readChessManuals(Context context, int page) throws IOException;
}
