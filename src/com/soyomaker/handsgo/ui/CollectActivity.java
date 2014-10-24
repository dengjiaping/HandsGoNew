package com.soyomaker.handsgo.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.adapter.GroupExpandableListViewAdapter;
import com.soyomaker.handsgo.manager.ChessManualServerManager;
import com.soyomaker.handsgo.manager.CollectManager;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.server.CollectServer;
import com.soyomaker.handsgo.util.AppPrefrence;
import com.soyomaker.handsgo.util.LogUtil;

/**
 * 我的收藏界面
 * 
 * @author like
 * 
 */
public class CollectActivity extends BaseActivity {

	private GroupExpandableListViewAdapter mAdapter;
	private ExpandableListView mChessManualListView;
	private CollectServer mCollectServer;
	private Button mUploadButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);

		initView();
	}

	private void initView() {
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.title_collect);

		mChessManualListView = (ExpandableListView) findViewById(R.id.listview_collect);

		mUploadButton = (Button) findViewById(R.id.btn_upload);
		mUploadButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LogUtil.e("CollectActivity", "上传服务器备份");
				String name = AppPrefrence.getUserName(CollectActivity.this);
				String password = AppPrefrence.getUserPassword(CollectActivity.this);
			}
		});

		mCollectServer = ChessManualServerManager.getCollectServer();
		mAdapter = new GroupExpandableListViewAdapter(this, mCollectServer.getGroups());
		mChessManualListView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.collect, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_collect_create_group: {
			final View view = LayoutInflater.from(CollectActivity.this).inflate(
					R.layout.dialog_create_group_edt, null);
			final EditText editText = (EditText) view.findViewById(R.id.editText);
			final Group group = new Group();
			group.setName(getString(R.string.create_group_dialog_default_name));
			editText.setText(R.string.create_group_dialog_default_name);
			new AlertDialog.Builder(CollectActivity.this)
					.setTitle(R.string.create_group_dialog_title).setIcon(R.drawable.ic_launcher)
					.setView(editText)
					.setPositiveButton(R.string.create_group_dialog_ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newName = editText.getText().toString();
							if (!TextUtils.isEmpty(newName)) {
								group.setName(newName);
								CollectManager.getInstance().addGroup(group);

								mAdapter.notifyDataSetChanged();
							} else {
								// TODO toast
							}
						}
					}).setNegativeButton(R.string.create_group_dialog_cancel, null).show();
		}
			break;
		}
		return super.onOptionsItemSelected(item);
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
		return "我的收藏界面";
	}
}
