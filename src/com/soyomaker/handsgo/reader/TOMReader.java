package com.soyomaker.handsgo.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.util.WebUtil;

public class TOMReader implements IChessManualReader {

    private static final long serialVersionUID = 1L;

    @Override
    public ArrayList<ChessManual> readChessManuals(Context context, int page) throws IOException {
        ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
        String url = "http://weiqi.sports.tom.com/php/listqipu"
                + ((page == 0) ? "" : (page < 10) ? "_0" + (page + 1) : "_" + (page + 1)) + ".html";
        String s = WebUtil.getHttpGet(context, url, "gb2312");
        String regex = "<li class=\"a\"><a href=\"javascript:newwindow\\('http://weiqi.sports.tom.com/.*?'\\)\" >(.*?)</a></li>\\s*?"
                + "<li class=\"b\">(.*?)</li>\\s*?"
                + "<li class=\"c\"><a href=\"../../(.*?)\">下载</a></li>";
        final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher mt = pt.matcher(s);
        while (mt.find()) {
            ChessManual chessManual = new ChessManual();
            chessManual.setCharset("gb2312");
            String matchName = mt.group(1).replaceAll("<font color=red>", "")
                    .replaceAll("</font>", "").trim();

            String regex2 = ".*? (.*?)执(.*?)胜(.*?)(（主）){0,1}(（快）){0,1}$";
            final Pattern pt2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
            final Matcher mt2 = pt2.matcher(matchName);
            while (mt2.find()) {
                String group1 = mt2.group(1);
                String group2 = mt2.group(2);
                String group3 = mt2.group(3);
                if (!TextUtils.isEmpty(group2)) {
                    if (group2.contains("黑")) {
                        chessManual.setBlackName(group1);
                        chessManual.setWhiteName(group3);
                    } else if (group2.contains("白")) {
                        chessManual.setBlackName(group3);
                        chessManual.setWhiteName(group1);
                    }
                }
                break;
            }

            chessManual.setMatchName(matchName);
            chessManual.setMatchTime(mt.group(2).trim());
            chessManual.setSgfUrl(("http://weiqi.tom.com/" + mt.group(3)).trim());
            chessManuals.add(chessManual);
        }
        return chessManuals;
    }
}
