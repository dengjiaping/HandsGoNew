package com.soyomaker.handsgo.ui;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileExplorerActivity;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppUtil;
import com.soyomaker.handsgo.util.LogUtil;

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
                if (AppUtil.hasInstalled(AppConstants.PACKAGE_NAME_GNUGO)) {
                    Intent intent = new Intent(getActivity(), FightActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), R.string.gnugo_start_download, Toast.LENGTH_LONG)
                            .show();
                    DownloadManager downloadManager = (DownloadManager) getActivity()
                            .getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(AppConstants.DOWNLOAD_GNUGO_URL);
                    Request request = new Request(uri);
                    // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    // 不显示下载界面
                    request.setVisibleInDownloadsUi(false);
                    long id = downloadManager.enqueue(request);
                    LogUtil.e("StudyFragment", id + "系统下载：" + AppConstants.DOWNLOAD_GNUGO_URL);
                }
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
