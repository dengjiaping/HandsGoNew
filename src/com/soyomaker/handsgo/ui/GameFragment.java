package com.soyomaker.handsgo.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.manager.ChessManualReaderManager;
import com.soyomaker.handsgo.manager.ChessManualReaderManager.IChessManualsReaderListener;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.server.IChessManualServer;
import com.soyomaker.handsgo.util.LogUtil;
import com.tjerkw.slideexpandable.library.ActionSlideExpandableListView;

/**
 * 精彩时局界面
 * 
 * @author like
 * 
 */
public class GameFragment extends Fragment {

	private ChessManualListViewAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ActionSlideExpandableListView mChessManualListView;

	private IChessManualServer mCurrentServer;
	private int mCurrentServerTag = -1;

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if (position >= mCurrentServer.getChessManuals().size()) {
				if (!toastRefreshingOrLoading()) {
					loadMoreChessManuals();
				}
			}
		}
	};
	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			if (!toastRefreshingOrLoading()) {
				refreshChessManuals();
			}
		}
	};

	public GameFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void onResume() {
		super.onResume();
		mCurrentServer = ChessManualServerManager.getInstance().getChessManualServer();
		int tag = mCurrentServer.getTag();
		if (mCurrentServerTag != tag) {
			mCurrentServerTag = tag;
			// 用来在设置界面更改棋谱服务器后，及时刷新棋谱列表
			mAdapter.updateChessManualServer(mCurrentServer);

			if (mCurrentServer.getChessManuals().isEmpty()) {
				mSwipeRefreshLayout.setRefreshing(true);
				refreshChessManuals();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main_game, container, false);
		initView(rootView);
		return rootView;
	}

	private boolean toastRefreshingOrLoading() {
		if (mCurrentServer.isLoadingMore()) {
			Toast.makeText(getActivity(), R.string.toast_loading, Toast.LENGTH_SHORT).show();
			return true;
		} else if (mCurrentServer.isRefreshing()) {
			Toast.makeText(getActivity(), R.string.toast_refreshing, Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

	private void initView(View rootView) {
		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light, android.R.color.holo_orange_light,
				android.R.color.holo_red_light);
		mChessManualListView = (ActionSlideExpandableListView) rootView
				.findViewById(R.id.listview_game);
		mChessManualListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {

					@Override
					public void onClick(View listView, View buttonview, int position) {
						if (buttonview.getId() == R.id.buttonCollect) {
							LogUtil.e("GameFragment", "收藏棋谱");
							mCurrentServer.collect(mAdapter.getItem(position));
						}
					}
				}, R.id.buttonCollect);
		mChessManualListView.setOnItemClickListener(mOnItemClickListener);

		mCurrentServer = ChessManualServerManager.getInstance().getChessManualServer();
		mAdapter = new ChessManualListViewAdapter(getActivity(), mCurrentServer);
		mChessManualListView.setAdapter(mAdapter);
	}

	private void refreshChessManuals() {
		ChessManualReaderManager.getInstance().refreshChessManuals(
				new IChessManualsReaderListener() {

					@Override
					public void readSuccess(ArrayList<ChessManual> chessManuals) {
						mSwipeRefreshLayout.setRefreshing(false);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void readFail() {
						mSwipeRefreshLayout.setRefreshing(false);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	private void loadMoreChessManuals() {
		ChessManualReaderManager.getInstance().loadMoreChessManuals(
				new IChessManualsReaderListener() {

					@Override
					public void readSuccess(ArrayList<ChessManual> chessManuals) {
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void readFail() {
						mAdapter.notifyDataSetChanged();
					}
				});
		mAdapter.notifyDataSetChanged();
	}
}
