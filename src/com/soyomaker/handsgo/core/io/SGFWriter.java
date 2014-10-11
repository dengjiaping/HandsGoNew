package com.soyomaker.handsgo.core.io;

import java.util.ArrayList;

import com.soyomaker.handsgo.model.ChessManual;

/**
 * SGF写作器
 * 
 * @author like
 * 
 */
public class SGFWriter {

	public String buildSGF(ChessManual chessManual, ArrayList<String> points) {
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("(;");
		sBuilder.append("C[" + chessManual.getMatchName() + " " + chessManual.getBlackName()
				+ " VS " + chessManual.getWhiteName() + "]");
		sBuilder.append("SZ[19]");
		sBuilder.append("GN[" + chessManual.getBlackName() + " VS " + chessManual.getWhiteName()
				+ "]");
		sBuilder.append("DT[" + chessManual.getMatchTime() + "]");
		sBuilder.append("RE[" + chessManual.getMatchResult() + "]");
		sBuilder.append("PB[" + chessManual.getBlackName() + "]");
		sBuilder.append("PW[" + chessManual.getWhiteName() + "]");
		sBuilder.append("EV[" + chessManual.getMatchName() + "]");
		sBuilder.append(buildPoints(points));
		sBuilder.append(")");
		return sBuilder.toString();
	}

	private String buildPoints(ArrayList<String> points) {
		StringBuilder sBuilder = new StringBuilder();
		for (int i = 0; i < points.size(); i++) {
			String point = points.get(i);
			sBuilder.append(";");
			if (i % 2 == 0) {
				sBuilder.append("B");
			} else {
				sBuilder.append("W");
			}
			sBuilder.append("[");
			sBuilder.append(point);
			sBuilder.append("]");
		}
		return sBuilder.toString();
	}
}
