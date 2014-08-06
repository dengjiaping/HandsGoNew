package com.soyomaker.handsgonew.ui.fileexplorer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.MenuItem;

import com.soyomaker.handsgonew.R;

public class FileExplorerActivity extends Activity {

	private ActionMode mActionMode;
	private FileListFragment mFileViewActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_file_explorer);

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			mFileViewActivity = new FileListFragment();
			getFragmentManager().beginTransaction().add(R.id.container, mFileViewActivity).commit();
		}
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

	public interface IBackPressedListener {

		/**
		 * 处理back事件。
		 * 
		 * @return True: 表示已经处理; False: 没有处理，让基类处理。
		 */
		boolean onBack();
	}

	@Override
	public void onBackPressed() {
		if (!mFileViewActivity.onBack()) {
			super.onBackPressed();
		}
	}

	public void setActionMode(ActionMode actionMode) {
		mActionMode = actionMode;
	}

	public ActionMode getActionMode() {
		return mActionMode;
	}

	public FileListFragment getFileViewActivity() {
		return mFileViewActivity;
	}
}
