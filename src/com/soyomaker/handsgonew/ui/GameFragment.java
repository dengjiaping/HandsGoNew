package com.soyomaker.handsgonew.ui;

import java.util.ArrayList;

import zrc.widget.SimpleFooter;
import zrc.widget.SimpleHeader;
import zrc.widget.ZrcListView.OnStartListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soyomaker.handsgonew.R;
import com.soyomaker.handsgonew.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgonew.manager.ChessManualReaderManager;
import com.soyomaker.handsgonew.manager.ChessManualReaderManager.IChessManualsReaderListener;
import com.soyomaker.handsgonew.manager.ChessManualServerManager;
import com.soyomaker.handsgonew.model.ChessManual;
import com.soyomaker.handsgonew.util.LogUtil;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

/**
 * 精彩时局界面
 * 
 * @author like
 * 
 */
public class GameFragment extends Fragment {

	private ChessManualListViewAdapter mAdapter;
	private ActionSlideExpandableListView mChessManualListView;

	public GameFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_game, container, false);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		mChessManualListView = (ActionSlideExpandableListView) rootView
				.findViewById(R.id.listview_game);
		mChessManualListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview, int position) {
						if (buttonview.getId() == R.id.buttonCollect) {
							LogUtil.e("GameFragment", "收藏棋谱");
						}
					}
				}, R.id.buttonCollect);
		mChessManualListView.setOnRefreshStartListener(new OnStartListener() {

			@Override
			public void onStart() {
				refreshChessManuals();
			}
		});
		mChessManualListView.setOnLoadMoreStartListener(new OnStartListener() {

			@Override
			public void onStart() {
				loadMoreChessManuals();
			}
		});

		// 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
		SimpleHeader header = new SimpleHeader(getActivity());
		header.setTextColor(0xff0066aa);
		header.setCircleColor(0xff33bbee);
		mChessManualListView.setHeadable(header);

		// 设置加载更多的样式（可选）
		SimpleFooter footer = new SimpleFooter(getActivity());
		footer.setCircleColor(0xff33bbee);
		mChessManualListView.setFootable(footer);

		mAdapter = new ChessManualListViewAdapter(getActivity(), ChessManualServerManager
				.getInstance().getChessManualServer().getChessManuals());
		mChessManualListView.setAdapter(mAdapter);

		if (mAdapter.getChessManuals().isEmpty()) {
			refreshChessManuals();
		}

		LogUtil.e("GameFragment", "initView");
	}

	private void refreshChessManuals() {
		ChessManualReaderManager.getInstance().refreshChessManuals(
				new IChessManualsReaderListener() {

					@Override
					public void readSuccess(ArrayList<ChessManual> chessManuals) {
						mChessManualListView.setRefreshSuccess();
						mAdapter.notifyDataSetChanged();
						if (!chessManuals.isEmpty()) {
							mChessManualListView.startLoadMore();
						}
					}

					@Override
					public void readFail() {
						mChessManualListView.setRefreshFail();
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	private void loadMoreChessManuals() {
		ChessManualReaderManager.getInstance().loadMoreChessManuals(
				new IChessManualsReaderListener() {

					@Override
					public void readSuccess(ArrayList<ChessManual> chessManuals) {
						mChessManualListView.setLoadMoreSuccess();
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void readFail() {
						mChessManualListView.stopLoadMore();
						mAdapter.notifyDataSetChanged();
					}
				});
	}
}
