package com.soyomaker.handsgo.ui.fileexplorer;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.BaseFragmentActivity;

public class FileExplorerActivity extends BaseFragmentActivity {

    private ActionMode mActionMode;
    private FileListFragment mFileViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);

        if (savedInstanceState == null) {
            mFileViewActivity = new FileListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mFileViewActivity)
                    .commit();
        }

        initView();
    }

    private void initView() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_local);
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

    @Override
    public String getPageName() {
        return "本地棋谱界面";
    }
}
