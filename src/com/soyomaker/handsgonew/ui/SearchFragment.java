package com.soyomaker.handsgonew.ui;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.core.DefaultBoardModel;
import com.soyomaker.handsgonew.core.GoBoard;
import com.soyomaker.handsgonew.core.GoController;
import com.soyomaker.handsgonew.core.IGridListener;
import com.soyomaker.handsgonew.model.SearchResultList;
import com.soyomaker.handsgonew.network.ITaskFinishListener;
import com.soyomaker.handsgonew.network.TaskResult;
import com.soyomaker.handsgonew.search.SearchController;

/**
 * 棋谱搜索界面
 * 
 * @author like
 * 
 */
public class SearchFragment extends Fragment implements IGridListener {

	private static final String TAG = "SearchFragment";

	private RelativeLayout mBoardLayout;
	private GoBoard mGoBoard;
	private GoController mGoController = new GoController();
	private DefaultBoardModel mBoardModel;
	private RadioGroup mStateGroup;
	private Button mSearchButton;

	public SearchFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_search, container, false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		mBoardLayout = (RelativeLayout) rootView.findViewById(R.id.board_layout);
		mSearchButton = (Button) rootView.findViewById(R.id.search);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (SearchController.getInstance().isSearching()) {
					return;
				}
				SearchController.getInstance().findSearchResults(mBoardModel,
						new ITaskFinishListener() {

							@Override
							public void onTaskFinished(TaskResult taskResult) {
								if (taskResult.getCode() == HttpStatus.SC_OK
										&& taskResult.getContent() instanceof SearchResultList) {
									Intent intent = new Intent(getActivity(),
											SearchResultActivity.class);
									intent.putExtra(SearchResultActivity.EXTRA_SEARCH_RESULT,
											(SearchResultList) taskResult.getContent());
									getActivity().startActivity(intent);
								}
								mSearchButton.clearAnimation();
							}
						});
				mSearchButton.startAnimation(AnimationUtils.loadAnimation(getActivity(),
						R.anim.anim_searching));
			}
		});
		mStateGroup = (RadioGroup) rootView.findViewById(R.id.button_layout);
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
		int cubicSize = Math.round(boardSize / (size + 1));

		mBoardModel = new DefaultBoardModel(size);
		mBoardModel.setShowHighlight(false);
		mGoBoard = new GoBoard(getActivity(), mBoardModel, cubicSize, cubicSize / 2, cubicSize / 2);
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

	private void initData() {
		// TODO
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
}
