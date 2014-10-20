package com.soyomaker.handsgo.ui;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileExplorerActivity;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppUtil;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.StorageUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 学习讲解界面
 * 
 * @author like
 * 
 */
public class StudyFragment extends BaseFragment {

	private Button mOpenLocalSgfButton;

	private Button mFightingWithGnugo;

	private long mDownloadId;

	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (mDownloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
				Intent install = new Intent(Intent.ACTION_VIEW);
				DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(
						Context.DOWNLOAD_SERVICE);
				Uri downloadFileUri = downloadManager.getUriForDownloadedFile(mDownloadId);
				install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(install);
			}
		}
	};

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
					Intent intent = new Intent(getActivity(), LocalFightActivity.class);
					startActivity(intent);
				} else if (mDownloadId > 0) {
					Toast.makeText(getActivity(), R.string.toast_downloading, Toast.LENGTH_LONG)
							.show();
				} else {
					DialogUtils.showPromptDialog(getActivity(),
							R.string.dialog_download_gnugo_title,
							R.string.dialog_download_gnugo_content,
							new DialogUtils.DefaultDialogListener() {
								public void onPositiveClick(DialogInterface dialog) {
									downloadGnugoApk();
								}
							}, R.string.dialog_download_gnugo_ok, R.string.cancel);
				}
			}
		});
	}

	private void downloadGnugoApk() {
		Toast.makeText(getActivity(), R.string.gnugo_start_download, Toast.LENGTH_LONG).show();
		String url = MobclickAgent.getConfigParams(getActivity(), AppConstants.DOWNLOAD_GNUGO_URL);
		if (TextUtils.isEmpty(url)) {
			url = "https://raw.githubusercontent.com/uestccokey/HandsGoNew/master/Gnugo-3.8.apk";
		}
		DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(
				Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse(url);
		Request request = new Request(uri);
		// 设置允许使用的网络类型，这里是移动网络和wifi都可以
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// 设置下载中通知栏提示的标题
		request.setTitle(getString(R.string.gnugo_name));
		// 设置下载中通知栏提示的介绍
		request.setDescription(getString(R.string.gnugo_description));
		// 确保handsgo/apk文件夹已存在
		StorageUtil.getDirByType(StorageUtil.DIR_TYPE_APK);
		// 设置下载地址
		request.setDestinationInExternalPublicDir("handsgo/apk", "Gnugo-3.8.apk");
		request.setMimeType("application/vnd.android.package-archive");
		mDownloadId = downloadManager.enqueue(request);
	}

	private void initData() {
		// 注册广播接收器，当下载完成时自动安装
		IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		getActivity().registerReceiver(mDownloadReceiver, filter);
	}

	@Override
	public String getPageName() {
		return "学习讲解界面";
	}
}
