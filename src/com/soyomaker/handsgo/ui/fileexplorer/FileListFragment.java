package com.soyomaker.handsgo.ui.fileexplorer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.soyomaker.handsgo.R;

public class FileListFragment extends Fragment implements IFileInteractionListener,
		IBackPressedListener {

	public static final String EXT_FILTER_KEY = "ext_filter";

	private static final String LOG_TAG = "FileListFragment";

	public static final String EXT_FILE_FIRST_KEY = "ext_file_first";

	public static final String ROOT_DIRECTORY = "root_directory";

	private static final String PRIMARY_FOLDER = "pref_key_primary_folder";
	private static final String READ_ROOT = "pref_key_read_root";
	private static final String SHOW_REAL_PATH = "pref_key_show_real_path";
	private static final String SYSTEM_SEPARATOR = File.separator;

	private ListView mFileListView;

	private ArrayAdapter<FileInfo> mAdapter;

	private FileViewInteractionHub mFileViewInteractionHub;

	private FileCategoryHelper mFileCagetoryHelper;

	private FileIconHelper mFileIconHelper;

	private ArrayList<FileInfo> mFileNameList = new ArrayList<FileInfo>();

	private Activity mActivity;

	private View mRootView;

	private static final String sdDir = Util.getSdDirectory();

	// memorize the scroll positions of previous paths
	private ArrayList<PathScrollPositionItem> mScrollPositionList = new ArrayList<PathScrollPositionItem>();
	private String mPreviousPath;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Log.v(LOG_TAG, "received broadcast:" + intent.toString());
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
					|| action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updateUI();
					}
				});
			}
		}
	};

	private boolean mBackspaceExit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mActivity = getActivity();
		mRootView = inflater.inflate(R.layout.file_explorer_list, container, false);

		mFileCagetoryHelper = new FileCategoryHelper(mActivity);
		mFileViewInteractionHub = new FileViewInteractionHub(this);
		Intent intent = mActivity.getIntent();

		mFileListView = (ListView) mRootView.findViewById(R.id.file_path_list);
		mFileIconHelper = new FileIconHelper(mActivity);
		mAdapter = new FileListAdapter(mActivity, R.layout.file_browser_item, mFileNameList,
				mFileViewInteractionHub, mFileIconHelper);

		boolean baseSd = intent.getBooleanExtra(GlobalConsts.KEY_BASE_SD, !isReadRoot(mActivity));

		String rootDir = intent.getStringExtra(ROOT_DIRECTORY);
		if (!TextUtils.isEmpty(rootDir)) {
			if (baseSd && FileListFragment.sdDir.startsWith(rootDir)) {
				rootDir = FileListFragment.sdDir;
			}
		} else {
			rootDir = baseSd ? FileListFragment.sdDir : GlobalConsts.ROOT_PATH;
		}
		mFileViewInteractionHub.setRootPath(rootDir);

		String currentDir = getPrimaryFolder(mActivity);
		Uri uri = intent.getData();
		if (uri != null) {
			if (baseSd && FileListFragment.sdDir.startsWith(uri.getPath())) {
				currentDir = FileListFragment.sdDir;
			} else {
				currentDir = uri.getPath();
			}
		}
		mFileViewInteractionHub.setCurrentPath(currentDir);

		mBackspaceExit = uri != null;

		mFileListView.setAdapter(mAdapter);
		mFileViewInteractionHub.refreshFileList();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
		mActivity.registerReceiver(mReceiver, intentFilter);

		updateUI();
		setHasOptionsMenu(true);
		return mRootView;
	}

	public static String getPrimaryFolder(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		String primaryFolder = settings.getString(PRIMARY_FOLDER, GlobalConsts.MNT_PATH);

		if (TextUtils.isEmpty(primaryFolder)) { // setting primary folder =
												// empty("")
			primaryFolder = GlobalConsts.ROOT_PATH;
		}

		// it's remove the end char of the home folder setting when it with the
		// '/' at the end.
		// if has the backslash at end of the home folder, it's has minor bug at
		// "UpLevel" function.
		int length = primaryFolder.length();
		if (length > 1 && SYSTEM_SEPARATOR.equals(primaryFolder.substring(length - 1))) { // length
																							// =
																							// 1,
																							// ROOT_PATH
			return primaryFolder.substring(0, length - 1);
		} else {
			return primaryFolder;
		}
	}

	public static boolean isReadRoot(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

		boolean isReadRootFromSetting = settings.getBoolean(READ_ROOT, false);
		boolean isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix = !getPrimaryFolder(context)
				.startsWith(Util.getSdDirectory());

		return isReadRootFromSetting || isReadRootWhenSettingPrimaryFolderWithoutSdCardPrefix;
	}

	public static boolean showRealPath(Context context) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getBoolean(SHOW_REAL_PATH, false);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mActivity.unregisterReceiver(mReceiver);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		mFileViewInteractionHub.onPrepareOptionsMenu(menu);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		mFileViewInteractionHub.onCreateOptionsMenu(menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onBack() {
		if (mBackspaceExit || !Util.isSDCardReady() || mFileViewInteractionHub == null) {
			return false;
		}
		return mFileViewInteractionHub.onBackPressed();
	}

	private class PathScrollPositionItem {
		String path;
		int pos;

		PathScrollPositionItem(String s, int p) {
			path = s;
			pos = p;
		}
	}

	// execute before change, return the memorized scroll position
	private int computeScrollPosition(String path) {
		int pos = 0;
		if (mPreviousPath != null) {
			if (path.startsWith(mPreviousPath)) {
				int firstVisiblePosition = mFileListView.getFirstVisiblePosition();
				if (mScrollPositionList.size() != 0
						&& mPreviousPath
								.equals(mScrollPositionList.get(mScrollPositionList.size() - 1).path)) {
					mScrollPositionList.get(mScrollPositionList.size() - 1).pos = firstVisiblePosition;
					Log.i(LOG_TAG, "computeScrollPosition: update item: " + mPreviousPath + " "
							+ firstVisiblePosition + " stack count:" + mScrollPositionList.size());
					pos = firstVisiblePosition;
				} else {
					mScrollPositionList.add(new PathScrollPositionItem(mPreviousPath,
							firstVisiblePosition));
					Log.i(LOG_TAG, "computeScrollPosition: add item: " + mPreviousPath + " "
							+ firstVisiblePosition + " stack count:" + mScrollPositionList.size());
				}
			} else {
				int i;
				for (i = 0; i < mScrollPositionList.size(); i++) {
					if (!path.startsWith(mScrollPositionList.get(i).path)) {
						break;
					}
				}
				// navigate to a totally new branch, not in current stack
				if (i > 0) {
					pos = mScrollPositionList.get(i - 1).pos;
				}

				for (int j = mScrollPositionList.size() - 1; j >= i - 1 && j >= 0; j--) {
					mScrollPositionList.remove(j);
				}
			}
		}

		Log.i(LOG_TAG, "computeScrollPosition: result pos: " + path + " " + pos + " stack count:"
				+ mScrollPositionList.size());
		mPreviousPath = path;
		return pos;
	}

	public boolean onRefreshFileList(String path, FileSortHelper sort) {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			return false;
		}
		final int pos = computeScrollPosition(path);
		ArrayList<FileInfo> fileList = mFileNameList;
		fileList.clear();

		File[] listFiles = file.listFiles(mFileCagetoryHelper.getFilter());
		if (listFiles == null)
			return true;

		for (File child : listFiles) {
			String absolutePath = child.getAbsolutePath();
			if (Util.isNormalFile(absolutePath) && Util.shouldShowFile(absolutePath)) {
				FileInfo lFileInfo = Util.GetFileInfo(child, mFileCagetoryHelper.getFilter(),
						Settings.instance().getShowDotAndHiddenFiles());
				if (lFileInfo != null) {
					fileList.add(lFileInfo);
				}
			}
		}

		sortCurrentList(sort);
		showEmptyView(fileList.size() == 0);
		mFileListView.post(new Runnable() {
			@Override
			public void run() {
				mFileListView.setSelection(pos);
			}
		});
		return true;
	}

	private void updateUI() {
		boolean sdCardReady = Util.isSDCardReady();
		View noSdView = mRootView.findViewById(R.id.sd_not_available_page);
		noSdView.setVisibility(sdCardReady ? View.GONE : View.VISIBLE);

		View navigationBar = mRootView.findViewById(R.id.navigation_bar);
		navigationBar.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);
		mFileListView.setVisibility(sdCardReady ? View.VISIBLE : View.GONE);

		if (sdCardReady) {
			mFileViewInteractionHub.refreshFileList();
		}
	}

	private void showEmptyView(boolean show) {
		View emptyView = mRootView.findViewById(R.id.empty_view);
		if (emptyView != null)
			emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	@Override
	public View getViewById(int id) {
		return mRootView.findViewById(id);
	}

	@Override
	public Context getContext() {
		return mActivity;
	}

	@Override
	public void onDataChanged() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onPick(FileInfo f) {
		try {
			Intent intent = Intent.parseUri(Uri.fromFile(new File(f.filePath)).toString(), 0);
			mActivity.setResult(Activity.RESULT_OK, intent);
			mActivity.finish();
			return;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldShowOperationPane() {
		return true;
	}

	@Override
	public boolean onOperation(int id) {
		return false;
	}

	// 支持显示真实路径
	@Override
	public String getDisplayPath(String path) {
		if (path.startsWith(FileListFragment.sdDir) && !showRealPath(mActivity)) {
			return getString(R.string.sd_folder) + path.substring(FileListFragment.sdDir.length());
		} else {
			return path;
		}
	}

	@Override
	public String getRealPath(String displayPath) {
		final String perfixName = getString(R.string.sd_folder);
		if (displayPath.startsWith(perfixName)) {
			return FileListFragment.sdDir + displayPath.substring(perfixName.length());
		} else {
			return displayPath;
		}
	}

	@Override
	public boolean onNavigation(String path) {
		return false;
	}

	@Override
	public boolean shouldHideMenu(int menu) {
		return false;
	}

	public void refresh() {
		if (mFileViewInteractionHub != null) {
			mFileViewInteractionHub.refreshFileList();
		}
	}

	public interface SelectFilesCallback {
		// files equals null indicates canceled
		void selected(ArrayList<FileInfo> files);
	}

	@Override
	public FileIconHelper getFileIconHelper() {
		return mFileIconHelper;
	}

	public boolean setPath(String location) {
		if (!location.startsWith(mFileViewInteractionHub.getRootPath())) {
			return false;
		}
		mFileViewInteractionHub.setCurrentPath(location);
		mFileViewInteractionHub.refreshFileList();
		return true;
	}

	@Override
	public FileInfo getItem(int pos) {
		if (pos < 0 || pos > mFileNameList.size() - 1)
			return null;

		return mFileNameList.get(pos);
	}

	@Override
	public void sortCurrentList(FileSortHelper sort) {
		Collections.sort(mFileNameList, sort.getComparator());
		onDataChanged();
	}

	@Override
	public ArrayList<FileInfo> getAllFiles() {
		return mFileNameList;
	}

	@Override
	public void addSingleFile(FileInfo file) {
		mFileNameList.add(file);
		onDataChanged();
	}

	@Override
	public int getItemCount() {
		return mFileNameList.size();
	}

	@Override
	public void runOnUiThread(Runnable r) {
		mActivity.runOnUiThread(r);
	}
}
