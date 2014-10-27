package com.soyomaker.handsgo.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.manager.ChessManualReaderManager;
import com.soyomaker.handsgo.manager.ChessManualReaderManager.IChessManualsReaderListener;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.server.IChessManualServer;

/**
 * 精彩时局界面
 * 
 * @author like
 * 
 */
public class GameFragment extends BaseFragment {

	private static final String TAG = "GameFragment";
	public static final String EXTRA_SERVER = "EXTRA_SERVER";
	private ChessManualListViewAdapter mAdapter;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ListView mChessManualListView;

	private IChessManualServer mCurrentServer;

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

	private void setBundle(Bundle bundle) {
		if (bundle != null) {
			this.mCurrentServer = (IChessManualServer) bundle.getSerializable(EXTRA_SERVER);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBundle(getArguments());
	}

	public void onResume() {
		super.onResume();
		mAdapter.updateChessManuals();
		if (mCurrentServer.getChessManuals().isEmpty()) {
			refreshChessManuals();
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
		mChessManualListView = (ListView) rootView.findViewById(R.id.listview_game);
		mChessManualListView.setOnItemClickListener(mOnItemClickListener);

		mAdapter = new ChessManualListViewAdapter(getActivity(), mCurrentServer);
		mChessManualListView.setAdapter(mAdapter);
	}

	private void refreshChessManuals() {
		mSwipeRefreshLayout.setRefreshing(true);
		if (!mCurrentServer.isRefreshing()) {
			ChessManualReaderManager.getInstance().refreshChessManuals(this.mCurrentServer,
					new IChessManualsReaderListener() {

						@Override
						public void readSuccess(ArrayList<ChessManual> chessManuals) {
							mSwipeRefreshLayout.setRefreshing(false);
							mAdapter.updateChessManuals();
						}

						@Override
						public void readFail() {
							mSwipeRefreshLayout.setRefreshing(false);
							mAdapter.updateChessManuals();
						}
					});
		}
	}

	private void loadMoreChessManuals() {
		if (!mCurrentServer.isLoadingMore()) {
			ChessManualReaderManager.getInstance().loadMoreChessManuals(this.mCurrentServer,
					new IChessManualsReaderListener() {

						@Override
						public void readSuccess(ArrayList<ChessManual> chessManuals) {
							mAdapter.updateChessManuals();
						}

						@Override
						public void readFail() {
							mAdapter.updateChessManuals();
						}
					});
		}
		mAdapter.updateChessManuals();
	}

	@Override
	public String getPageName() {
		return "精彩时局界面";
	}
}
