package com.soyomaker.handsgo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soyomaker.handsgo.R;

/**
 * 学习讲解界面
 * 
 * @author like
 * 
 */
public class StudyFragment extends BaseFragment {

    public StudyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_study, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View rootView) {
    }

    private void initData() {
    }

    @Override
    public String getPageName() {
        return "学习讲解界面";
    }
}
