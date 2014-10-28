package com.soyomaker.handsgo.ui;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.core.DefaultBoardModel;
import com.soyomaker.handsgo.core.GoBoard;
import com.soyomaker.handsgo.core.GoController;
import com.soyomaker.handsgo.core.IGridListener;
import com.soyomaker.handsgo.model.SearchResultList;
import com.soyomaker.handsgo.network.ITaskFinishListener;
import com.soyomaker.handsgo.network.TaskResult;
import com.soyomaker.handsgo.search.ShapeSearchController;

public class ShapeSearchActivity extends BaseActivity implements IGridListener {

    private RelativeLayout mBoardLayout;
    private GoBoard mGoBoard;
    private GoController mGoController = new GoController();
    private DefaultBoardModel mBoardModel;
    private RadioGroup mStateGroup;
    private Button mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_search);

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.shape_search);

        mBoardLayout = (RelativeLayout) findViewById(R.id.board_layout);
        mSearchButton = (Button) findViewById(R.id.search);
        mSearchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ShapeSearchController.getInstance().isSearching()) {
                    return;
                }
                boolean find = ShapeSearchController.getInstance().find(mBoardModel,
                        new ITaskFinishListener() {

                            @Override
                            public void onTaskFinished(TaskResult taskResult) {
                                if (taskResult.getCode() == HttpStatus.SC_OK
                                        && taskResult.getContent() instanceof SearchResultList) {
                                    Intent intent = new Intent(ShapeSearchActivity.this,
                                            ShapeResultActivity.class);
                                    intent.putExtra(ShapeResultActivity.EXTRA_SEARCH_RESULT,
                                            (SearchResultList) taskResult.getContent());
                                    ShapeSearchActivity.this.startActivity(intent);
                                } else {
                                    // TODO 搜索结果为空进行提示
                                }
                                mSearchButton.clearAnimation();
                            }
                        });
                if (!find) {
                    Toast.makeText(ShapeSearchActivity.this, R.string.toast_invalid_shape_search,
                            Toast.LENGTH_LONG).show();
                } else {
                    mSearchButton.startAnimation(AnimationUtils.loadAnimation(
                            ShapeSearchActivity.this, R.anim.anim_searching));
                }
            }
        });
        mStateGroup = (RadioGroup) findViewById(R.id.button_layout);
        mStateGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                case R.id.add_black:
                    mGoController.setState(GoController.STATE_SET_BLACK);
                    break;
                case R.id.add_white:
                    mGoController.setState(GoController.STATE_SET_WHITE);
                    break;
                case R.id.add_black_or_white:
                    mGoController.setState(GoController.STATE_SET_BLACK_WHITE);
                    break;
                case R.id.delete_stone:
                    mGoController.setState(GoController.STATE_REMOVE_STONE);
                    break;
                case R.id.reset_board:
                    mGoController.setState(GoController.STATE_RESET);
                    mGoController.reset();
                    break;
                }
                mGoBoard.postInvalidate();
            }
        });
        int size = 19;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // 棋盘大小
        int boardSize = Math.min(dm.widthPixels, dm.heightPixels);
        // 棋子大小
        int cubicSize = Math.round(boardSize * 1.0f / (size + 1));

        mBoardModel = new DefaultBoardModel(size);
        mBoardModel.setShowHighlight(false);
        mGoBoard = new GoBoard(ShapeSearchActivity.this, mBoardModel, cubicSize, cubicSize / 2,
                cubicSize / 2);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(boardSize,
                boardSize);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGoBoard.setFocusable(true);
        mGoBoard.setClickable(true);
        mGoBoard.setFocusableInTouchMode(true);
        mGoBoard.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mGoBoard.pointerReleased(Math.round(event.getX()), Math.round(event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mGoBoard.pointerPressed(Math.round(event.getX()), Math.round(event.getY()));
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    mGoBoard.pointerMoved(Math.round(event.getX()), Math.round(event.getY()));
                }
                return false;
            }
        });
        mGoBoard.setGridListener(this);
        mBoardLayout.addView(mGoBoard, layoutParams);

        mGoController.setBoardModel(mBoardModel);
        mGoController.setBoardSize(size);
    }

    @Override
    public void touchPressed(int col, int row) {
    }

    @Override
    public void touchReleased(int col, int row) {
        mGoController.touch(col, row);
        mGoBoard.postInvalidate();
    }

    @Override
    public void touchMoved(int col, int row) {
    }

    @Override
    public String getPageName() {
        return "棋型搜索界面";
    }
}
