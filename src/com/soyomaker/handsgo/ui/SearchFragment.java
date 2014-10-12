package com.soyomaker.handsgo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.soyomaker.handsgo.R;

/**
 * 棋谱搜索界面
 * 
 * @author like
 * 
 */
public class SearchFragment extends Fragment {

	private static final String TAG = "SearchFragment";

	private Button mShapeSearchButton;
	private Button mKeywordsSearchButton;

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
		mShapeSearchButton = (Button) rootView.findViewById(R.id.shape_search);
		mShapeSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ShapeSearchActivity.class);
				startActivity(intent);
			}
		});
		mKeywordsSearchButton = (Button) rootView.findViewById(R.id.keywords_search);
		mKeywordsSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), KeywordsSearchActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		// TODO
	}
}
