package com.soyomaker.handsgo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileExplorerActivity;

/**
 * 学习讲解界面
 * 
 * @author like
 * 
 */
public class StudyFragment extends BaseFragment {

    private Button mOpenLocalSgfButton;

    private Button mFightingWithGnugo;

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
        mOpenLocalSgfButton = (Button) rootView.findViewById(R.id.open_local_sgf);
        mOpenLocalSgfButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), FileExplorerActivity.class);
                startActivity(intent);
            }
        });
        mFightingWithGnugo = (Button) rootView.findViewById(R.id.fighting_with_gnugo);
        mFightingWithGnugo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FightActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        // TODO
    }

    @Override
    public String getPageName() {
        return "学习讲解界面";
    }
}
