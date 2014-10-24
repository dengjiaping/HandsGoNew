package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppUtil;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.StorageUtil;
import com.umeng.analytics.MobclickAgent;

public class LaboratoryActivity extends BaseActivity {

	private Button mFightingWithGnugo;

	private long mDownloadId;

	private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			if (mDownloadId == intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)) {
				Intent install = new Intent(Intent.ACTION_VIEW);
				DownloadManager downloadManager = (DownloadManager) LaboratoryActivity.this
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Uri downloadFileUri = downloadManager.getUriForDownloadedFile(mDownloadId);
				install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
				install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(install);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_laboratory);

		initData();
		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_laboratory);

		mFightingWithGnugo = (Button) findViewById(R.id.fighting_with_gnugo);
		mFightingWithGnugo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (AppUtil.hasInstalled(AppConstants.PACKAGE_NAME_GNUGO)) {
					Intent intent = new Intent(LaboratoryActivity.this, LocalFightActivity.class);
					startActivity(intent);
				} else if (mDownloadId > 0) {
					Toast.makeText(LaboratoryActivity.this, R.string.toast_downloading,
							Toast.LENGTH_LONG).show();
				} else {
					DialogUtils.showPromptDialog(LaboratoryActivity.this,
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
		Toast.makeText(this, R.string.gnugo_start_download, Toast.LENGTH_LONG).show();
		String url = MobclickAgent.getConfigParams(this, AppConstants.DOWNLOAD_GNUGO_URL);
		if (TextUtils.isEmpty(url)) {
			url = "https://raw.githubusercontent.com/uestccokey/HandsGoNew/master/Gnugo-3.8.apk";
		}
		DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
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
		registerReceiver(mDownloadReceiver, filter);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public String getPageName() {
		return "实验室界面";
	}
}