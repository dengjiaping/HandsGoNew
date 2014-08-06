package com.soyomaker.handsgonew.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soyomaker.handsgonew.R;

/**
 * 棋谱搜索界面
 * 
 * @author like
 * 
 */
public class SearchFragment extends Fragment {

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
		// TODO
	}

	private void initData() {
		// TODO
	}
}
