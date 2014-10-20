package com.soyomaker.handsgo.core.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.text.TextUtils;

import com.soyomaker.handsgo.core.sgf.SGFTree;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Match;
import com.soyomaker.handsgo.model.MatchInfo;
import com.soyomaker.handsgo.util.StringUtil;
import com.soyomaker.handsgo.util.WebUtil;

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
			String sgf = null;
			if (TextUtils.isEmpty(chessManual.getSgfContent())) {
				int type = chessManual.getType();
				switch (type) {
				case ChessManual.ONLINE_CHESS_MANUAL: {
					sgf = WebUtil.getHttpGet(context, chessManual.getSgfUrl(),
							chessManual.getCharset());
				}
					break;
				case ChessManual.LOCAL_CHESS_MANUAL: {
					sgf = StringUtil.inputStream2String(
							new FileInputStream(chessManual.getSgfUrl()), chessManual.getCharset());
				}
					break;
				}
			} else {
				sgf = chessManual.getSgfContent();
			}
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sgf.getBytes())));
			match.setSGFTrees(SGFTree.load(br));
			match.setSgfSource(sgf);
			MatchInfo matchInfo = match.getMatchInfo();
			if (TextUtils.isEmpty(matchInfo.getBlackName())) {
				matchInfo.setBlackName(chessManual.getBlackName());
			}
			if (TextUtils.isEmpty(matchInfo.getWhiteName())) {
				matchInfo.setWhiteName(chessManual.getWhiteName());
			}
			if (TextUtils.isEmpty(matchInfo.getResult())) {
				matchInfo.setResult(chessManual.getMatchResult());
			}
			if (TextUtils.isEmpty(matchInfo.getDate())) {
				matchInfo.setDate(chessManual.getMatchTime());
			}
			if (TextUtils.isEmpty(matchInfo.getMatchName())) {
				matchInfo.setMatchName(chessManual.getMatchName());
			}

			if (!TextUtils.isEmpty(matchInfo.getBlackName())) {
				chessManual.setBlackName(matchInfo.getBlackName());
			}
			if (!TextUtils.isEmpty(matchInfo.getWhiteName())) {
				chessManual.setWhiteName(matchInfo.getWhiteName());
			}
			if (!TextUtils.isEmpty(matchInfo.getMatchName())) {
				chessManual.setMatchName(matchInfo.getMatchName());
			}
			if (!TextUtils.isEmpty(matchInfo.getResult())) {
				chessManual.setMatchResult(matchInfo.getResult());
			}
			if (!TextUtils.isEmpty(matchInfo.getDate())) {
				chessManual.setMatchTime(matchInfo.getDate());
			}
			if (!TextUtils.isEmpty(match.getSgfSource())) {
				chessManual.setSgfContent(match.getSgfSource());
			}
			DBService.saveHistoryChessManual(chessManual);
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
