package com.soyomaker.handsgonew.core.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;

import com.soyomaker.handsgonew.core.sgf.SGFTree;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.model.Match;
import com.soyomaker.handsgonew.util.WebUtil;

/**
 * SGF读取器
 * 
 * @author like
 * 
 */
public class SGFReader {

	public static Match read(Context context, ChessManual chessManual) {
		Match match = new Match();
		BufferedReader br = null;
		try {
			String s = WebUtil.getHttpGet(context, chessManual.getSgfUrl(),
					chessManual.getCharset());
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
			match.setSGFTrees(SGFTree.load(br));
			match.setSgfSource(s);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return match;
	}
}
