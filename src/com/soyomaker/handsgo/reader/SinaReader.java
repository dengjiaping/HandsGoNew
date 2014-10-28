package com.soyomaker.handsgo.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * Sina的棋谱读取器
 * 
 * @author like
 * 
 */
public class SinaReader implements IChessManualReader {

    private static final long serialVersionUID = 1L;

    @Override
    public ArrayList<ChessManual> readChessManuals(Context context, int page) throws IOException {
        ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();

        String url = "http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=" + page;
        String s = WebUtil.getHttpGet(context, url, "gbk");
        // 匹配棋谱除sgf之外的其他信息
        {
            String regex2 = "<div align=.*?</div>";
            final Pattern pt2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
            final Matcher mt2 = pt2.matcher(s);
            int index = 0;
            ChessManual chessManual = null;
            while (mt2.find()) {
                String str = mt2.group();
                if (str.startsWith("<div align=") && str.endsWith("</div>")) {
                    String string = str.substring("<div align='center'>".length(), str.length()
                            - "</div>".length());
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
                            chessManual.setMatchName(string.trim());
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
                        chessManual.setCharset("gb2312");
                        chessManuals.add(chessManual);
                        index = 0;
                    }
                }
            }
        }
        // 匹配棋谱sgf信息
        {
            String regex = "http://duiyi\\.sina\\.com\\.cn/cgibo/.*?\\.sgf";
            final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            final Matcher mt = pt.matcher(s);
            int count = 0;
            int i = 0;
            while (mt.find()) {
                i++;
                String sgf = mt.group();
                if (i == 3) {
                    chessManuals.get(count).setSgfUrl(sgf);
                    count++;
                    i = 0;
                }
            }
        }
        return chessManuals;
    }
}
