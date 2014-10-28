package com.soyomaker.handsgo.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.text.Html;

import com.soyomaker.handsgo.core.DefaultBoardModel;
import com.soyomaker.handsgo.core.GoPoint;
import com.soyomaker.handsgo.core.io.SGFWriter;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.network.ITaskFinishListener;
import com.soyomaker.handsgo.network.RequestTask;
import com.soyomaker.handsgo.network.TaskParams;
import com.soyomaker.handsgo.network.TaskResult;
import com.soyomaker.handsgo.network.parser.IParser;
import com.soyomaker.handsgo.network.parser.SearchResultParser;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * 棋型搜索控制器
 * <p/>
 * 负责101围棋网的棋型搜索
 * 
 * @author like
 * 
 */
public class ShapeSearchController {

    private static final ShapeSearchController mInstance = new ShapeSearchController();

    private boolean mIsSearching;

    private ShapeSearchController() {
    }

    public static ShapeSearchController getInstance() {
        return mInstance;
    }

    public boolean isSearching() {
        return mIsSearching;
    }

    /**
     * 查找搜索结果
     * 
     * @param boardModel
     * @param listener
     */
    public boolean find(DefaultBoardModel boardModel, final ITaskFinishListener listener) {
        StringBuffer black = new StringBuffer();
        StringBuffer white = new StringBuffer();
        int size = boardModel.getBoardSize();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                GoPoint goPoint = boardModel.getPoint(i, j);
                if (goPoint.getPlayer() == GoPoint.PLAYER_BLACK) {
                    char col = (char) ('a' + i);
                    char row = (char) ('a' + j);
                    if (black.length() != 0) {
                        black.append("*");
                    }
                    black.append(col);
                    black.append(row);
                } else if (goPoint.getPlayer() == GoPoint.PLAYER_WHITE) {
                    char col = (char) ('a' + i);
                    char row = (char) ('a' + j);
                    if (white.length() != 0) {
                        white.append("*");
                    }
                    white.append(col);
                    white.append(row);
                }
            }
        }

        String posstr = black.toString() + "$$$" + white.toString();
        if (posstr.length() < 16) {
            return false;
        }

        mIsSearching = true;

        IParser parser = new SearchResultParser();
        RequestTask requestTask = new RequestTask(parser);
        requestTask.setTaskFinishListener(new ITaskFinishListener() {

            @Override
            public void onTaskFinished(TaskResult taskResult) {
                mIsSearching = false;

                if (listener != null) {
                    listener.onTaskFinished(taskResult);
                }
            }
        });

        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("posstr", posstr));
        requestTask.setPostParams(pairs);

        TaskParams params = new TaskParams();
        params.put(RequestTask.PARAM_URL, "http://www.101weiqi.com/search_qipu");
        params.put(RequestTask.PARAM_HTTP_METHOD, RequestTask.HTTP_POST);

        requestTask.execute(params);

        return true;
    }

    /**
     * 根据id，获取对应棋谱
     * 
     * @param context
     * @param id
     * @return
     */
    public ChessManual getChessManual(Context context, int id) {
        ChessManual chessManual = new ChessManual();
        String str = WebUtil.getHttpGet(context, "http://www.101weiqi.com/chessbook/chess/" + id
                + "/");
        String regex1 = "<div align=\"center\"><a href=\".*?\">(.*?)</a> <img src=\"/static/images/black1.gif\"> VS <img src=\"/static/images/white1.gif\"> <a href=\".*?\">(.*?)</a></div><div class=\"gamedesc\">比赛名称：(.*?)<br/>对弈时间:(.*?)<br/>(.*?)共.*?手，(.*?)<";
        final Pattern pt1 = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);
        final Matcher mt1 = pt1.matcher(str);
        while (mt1.find()) {
            String s1 = mt1.group(1);
            chessManual.setBlackName(s1);
            String s2 = mt1.group(2);
            chessManual.setWhiteName(s2);
            String s3 = mt1.group(3);
            chessManual.setMatchName(s3);
            String s4 = mt1.group(4);
            chessManual.setMatchTime(s4);
            String s5 = mt1.group(5);
            chessManual.setMatchInfo(Html.fromHtml(s5).toString());
            String s6 = mt1.group(6);
            chessManual.setMatchResult(s6);
        }
        String regex2 = "possteps\\.push\\(\"(.*?)\"\\);";
        final Pattern pt2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
        final Matcher mt2 = pt2.matcher(str);
        ArrayList<String> points = new ArrayList<String>();
        while (mt2.find()) {
            String s = mt2.group(1);
            points.add(s);
        }
        chessManual.setSgfContent(new SGFWriter().buildSGF(chessManual, points));
        return chessManual;
    }
}
