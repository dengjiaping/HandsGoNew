package com.soyomaker.handsgo.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * XGOO棋谱读取器
 * 
 * http://qp.xgoo.org/index.php?page=2&qp_name=
 * page代表页数，qp_name代码查询的参数，支持多种查询，比如棋手，比赛，时间等
 * 
 * @author like
 */
public class XGOOReader implements IChessManualReader {

	private static final long serialVersionUID = 1L;
	private String mSearchString = "";

	public void setSearchString(String searchString) {
		this.mSearchString = searchString;
	}

	@Override
	public ArrayList<ChessManual> readChessManuals(Context context, int page) throws IOException {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();

		String url = "http://qp.xgoo.org/index.php?page=" + page + "&qp_name=" + mSearchString;
		String s = WebUtil.getHttpGet(context, url);
		String regex2 = "<td align=.*?</td>";
		final Pattern pt2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
		final Matcher mt2 = pt2.matcher(s);
		int index = 0;
		ChessManual chessManual = null;
		while (mt2.find()) {
			String str = mt2.group();
			if (str.startsWith("<td align=center>") && str.endsWith("</td>")) {
				String string = str.substring("<td align=center>".length(),
						str.length() - "</td>".length());
				string = string.replaceAll("&nbsp;", " ");
				switch (index) {
				case 0:
					chessManual = new ChessManual();
					chessManual.setMatchTime(string.trim());
					index++;
					break;
				case 1:
					if (chessManual != null) {
						chessManual.setBlackName(string.trim());
						index++;
					}
					break;
				case 2:
					if (chessManual != null) {
						chessManual.setWhiteName(string.trim());
						index++;
					}
					break;
				case 3:
					if (chessManual != null) {
						String name = string.substring(string.indexOf(">") + 1,
								string.lastIndexOf("<"));
						chessManual.setMatchName(name.trim());
						index++;
					}
					break;
				case 4:
					if (chessManual != null) {
						chessManual.setMatchResult(string.trim());
						index++;
					}
					break;
				}
				if (index == 5) {
					chessManual.setCharset("utf-8");
					chessManuals.add(chessManual);
					index = 0;
				}
			}
			if (str.startsWith("<td align=left>") && str.endsWith("</td>")) {
				String time = str.substring("<td align=left>".length(),
						str.length() - "</td>".length());
				String regex = "http://www\\.xgoo\\.org/qipu/.*?\\.sgf";
				final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				final Matcher mt = pt.matcher(time);
				while (mt.find()) {
					String sgfUrl = mt.group();
					if (chessManual != null && index == 0) {
						chessManual.setSgfUrl(sgfUrl.trim());
					}
				}
			}
		}
		return chessManuals;
	}
}
