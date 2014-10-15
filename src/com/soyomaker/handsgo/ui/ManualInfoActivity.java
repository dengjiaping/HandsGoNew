package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.MatchInfo;

/**
 * 棋谱信息界面
 * 
 * @author like
 * 
 */
public class ManualInfoActivity extends BaseActivity {

    public static final String EXTRA_MATCH_INFO = "extra_match_info";
    private MatchInfo mMatchInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_info);

        initData();
        initView();
    }

    private void initView() {
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_manual_info);

        if (mMatchInfo != null) {
            ((TextView) findViewById(R.id.gameName)).setText(getResources().getString(
                    R.string.info_game_name, mMatchInfo.getGameName()));
            ((TextView) findViewById(R.id.gameComment)).setText(getResources().getString(
                    R.string.info_game_comment, mMatchInfo.getGameComment()));
            ((TextView) findViewById(R.id.boardSize)).setText(getResources().getString(
                    R.string.info_board_size, mMatchInfo.getBoardSize()));
            ((TextView) findViewById(R.id.komi)).setText(getResources().getString(
                    R.string.info_komi, mMatchInfo.getKomi()));
            ((TextView) findViewById(R.id.handicap)).setText(getResources().getString(
                    R.string.info_handicap, mMatchInfo.getHandicap()));
            ((TextView) findViewById(R.id.date)).setText(getResources().getString(
                    R.string.info_date, mMatchInfo.getDate()));
            ((TextView) findViewById(R.id.matchName)).setText(getResources().getString(
                    R.string.info_match_name, mMatchInfo.getMatchName()));
            ((TextView) findViewById(R.id.place)).setText(getResources().getString(
                    R.string.info_place, mMatchInfo.getPlace()));
            ((TextView) findViewById(R.id.result)).setText(getResources().getString(
                    R.string.info_result, mMatchInfo.getResult()));
            ((TextView) findViewById(R.id.time)).setText(getResources().getString(
                    R.string.info_time, mMatchInfo.getTime()));
            ((TextView) findViewById(R.id.whiteName)).setText(getResources().getString(
                    R.string.info_white_name, mMatchInfo.getWhiteName()));
            ((TextView) findViewById(R.id.whiteTeam)).setText(getResources().getString(
                    R.string.info_white_team, mMatchInfo.getWhiteTeam()));
            ((TextView) findViewById(R.id.whiteRank)).setText(getResources().getString(
                    R.string.info_white_rank, mMatchInfo.getWhiteRank()));
            ((TextView) findViewById(R.id.blackName)).setText(getResources().getString(
                    R.string.info_black_name, mMatchInfo.getBlackName()));
            ((TextView) findViewById(R.id.blackTeam)).setText(getResources().getString(
                    R.string.info_black_team, mMatchInfo.getBlackTeam()));
            ((TextView) findViewById(R.id.blackRank)).setText(getResources().getString(
                    R.string.info_black_rank, mMatchInfo.getBlackRank()));
            ((TextView) findViewById(R.id.source)).setText(getResources().getString(
                    R.string.info_source, mMatchInfo.getSource()));
        }
    }

    private void initData() {
        Intent intent = getIntent();
        mMatchInfo = (MatchInfo) intent.getSerializableExtra(EXTRA_MATCH_INFO);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public String getPageName() {
        return "棋谱信息界面";
    }
}
