package com.soyomaker.handsgo.ui.fileexplorer;

import java.io.File;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileSortHelper.SortMethod;

public class FileViewInteractionHub {

    private static final String LOG_TAG = "FileViewInteractionHub";

    private IFileInteractionListener mFileViewListener;

    private FileSortHelper mFileSortHelper;

    private View mNavigationBar;

    private TextView mNavigationBarText;

    private View mDropdownNavigation;

    private ImageView mNavigationBarUpDownArrow;

    private Context mContext;

    public FileViewInteractionHub(IFileInteractionListener fileViewListener) {
        assert (fileViewListener != null);
        mFileViewListener = fileViewListener;
        setup();
        mFileSortHelper = new FileSortHelper();
        mContext = mFileViewListener.getContext();
    }

    public void sortCurrentList() {
        mFileViewListener.sortCurrentList(mFileSortHelper);
    }

    public FileInfo getItem(int pos) {
        return mFileViewListener.getItem(pos);
    }

    private void setup() {
        setupNaivgationBar();
        setupFileListView();
    }

    private void setupNaivgationBar() {
        mNavigationBar = mFileViewListener.getViewById(R.id.navigation_bar);
        mNavigationBarText = (TextView) mFileViewListener.getViewById(R.id.current_path_view);
        mNavigationBarUpDownArrow = (ImageView) mFileViewListener.getViewById(R.id.path_pane_arrow);
        View clickable = mFileViewListener.getViewById(R.id.current_path_pane);
        clickable.setOnClickListener(buttonClick);

        mDropdownNavigation = mFileViewListener.getViewById(R.id.dropdown_navigation);

        setupClick(mNavigationBar, R.id.path_pane_up_level);
    }

    private void setupClick(View v, int id) {
        View button = (v != null ? v.findViewById(id) : mFileViewListener.getViewById(id));
        if (button != null)
            button.setOnClickListener(buttonClick);
    }

    private View.OnClickListener buttonClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.current_path_pane:
                onNavigationBarClick();
                break;
            case R.id.path_pane_up_level:
                onOperationUpLevel();
                ActionMode mode = ((FileExplorerActivity) mContext).getActionMode();
                if (mode != null) {
                    mode.finish();
                }
                break;
            }
        }
    };

    private void onOperationReferesh() {
        refreshFileList();
    }

    private OnClickListener navigationClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            assert (path != null);
            showDropdownNavigation(false);
            if (mFileViewListener.onNavigation(path))
                return;

            if (path.isEmpty()) {
                mCurrentPath = mRoot;
            } else {
                mCurrentPath = path;
            }
            refreshFileList();
        }
    };

    protected void onNavigationBarClick() {
        if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
            showDropdownNavigation(false);
        } else {
            LinearLayout list = (LinearLayout) mDropdownNavigation
                    .findViewById(R.id.dropdown_navigation_list);
            list.removeAllViews();
            int pos = 0;
            String displayPath = mFileViewListener.getDisplayPath(mCurrentPath);
            boolean root = true;
            int left = 0;
            while (pos != -1 && !displayPath.equals("/")) {// 如果当前位置在根文件夹则不显示导航条
                int end = displayPath.indexOf("/", pos);
                if (end == -1)
                    break;

                View listItem = LayoutInflater.from(mContext).inflate(R.layout.dropdown_item, null);

                View listContent = listItem.findViewById(R.id.list_item);
                listContent.setPadding(left, 0, 0, 0);
                left += 20;
                ImageView img = (ImageView) listItem.findViewById(R.id.item_icon);

                img.setImageResource(root ? R.drawable.dropdown_icon_root
                        : R.drawable.dropdown_icon_folder);
                root = false;

                TextView text = (TextView) listItem.findViewById(R.id.path_name);
                String substring = displayPath.substring(pos, end);
                if (substring.isEmpty())
                    substring = "/";
                text.setText(substring);

                listItem.setOnClickListener(navigationClick);
                listItem.setTag(mFileViewListener.getRealPath(displayPath.substring(0, end)));
                pos = end + 1;
                list.addView(listItem);
            }
            if (list.getChildCount() > 0)
                showDropdownNavigation(true);

        }
    }

    public boolean onOperationUpLevel() {
        showDropdownNavigation(false);

        if (mFileViewListener.onOperation(GlobalConsts.OPERATION_UP_LEVEL)) {
            return true;
        }

        if (!mRoot.equals(mCurrentPath)) {
            mCurrentPath = new File(mCurrentPath).getParent();
            refreshFileList();
            return true;
        }

        return false;
    }

    public void onOperationSearch() {

    }

    public void onSortChanged(SortMethod s) {
        if (mFileSortHelper.getSortMethod() != s) {
            mFileSortHelper.setSortMethog(s);
            sortCurrentList();
        }
    }

    public void refreshFileList() {
        updateNavigationPane();

        // onRefreshFileList returns true indicates list has changed
        mFileViewListener.onRefreshFileList(mCurrentPath, mFileSortHelper);
    }

    private void updateNavigationPane() {
        View upLevel = mFileViewListener.getViewById(R.id.path_pane_up_level);
        upLevel.setVisibility(mRoot.equals(mCurrentPath) ? View.INVISIBLE : View.VISIBLE);

        View arrow = mFileViewListener.getViewById(R.id.path_pane_arrow);
        arrow.setVisibility(mRoot.equals(mCurrentPath) ? View.GONE : View.VISIBLE);

        mNavigationBarText.setText(mFileViewListener.getDisplayPath(mCurrentPath));
    }

    // File List view setup
    private ListView mFileListView;

    private void setupFileListView() {
        mFileListView = (ListView) mFileViewListener.getViewById(R.id.file_path_list);
        mFileListView.setLongClickable(true);
        mFileListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });
    }

    private static final int MENU_SORT = 1;

    private static final int MENU_SORT_NAME = 2;

    private static final int MENU_SORT_SIZE = 3;

    private static final int MENU_SORT_DATE = 4;

    private static final int MENU_SORT_TYPE = 5;

    private static final int MENU_REFRESH = 6;

    private OnMenuItemClickListener menuItemClick = new OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();
            if (mFileViewListener.onOperation(itemId)) {
                return true;
            }

            switch (itemId) {
            case MENU_REFRESH:
                onOperationReferesh();
                break;
            // sort
            case MENU_SORT_NAME:
                item.setChecked(true);
                onSortChanged(SortMethod.name);
                break;
            case MENU_SORT_SIZE:
                item.setChecked(true);
                onSortChanged(SortMethod.size);
                break;
            case MENU_SORT_DATE:
                item.setChecked(true);
                onSortChanged(SortMethod.date);
                break;
            case MENU_SORT_TYPE:
                item.setChecked(true);
                onSortChanged(SortMethod.type);
                break;
            default:
                return false;
            }

            return true;
        }
    };

    private String mCurrentPath;

    private String mRoot;

    public boolean onCreateOptionsMenu(Menu menu) {
        showDropdownNavigation(false);

        SubMenu sortMenu = menu.addSubMenu(0, MENU_SORT, 1, R.string.menu_item_sort).setIcon(
                R.drawable.ic_menu_sort);
        addMenuItem(sortMenu, MENU_SORT_NAME, 0, R.string.menu_item_sort_name);
        addMenuItem(sortMenu, MENU_SORT_SIZE, 1, R.string.menu_item_sort_size);
        addMenuItem(sortMenu, MENU_SORT_DATE, 2, R.string.menu_item_sort_date);
        addMenuItem(sortMenu, MENU_SORT_TYPE, 3, R.string.menu_item_sort_type);
        sortMenu.setGroupCheckable(0, true, true);
        sortMenu.getItem(0).setChecked(true);

        addMenuItem(menu, MENU_REFRESH, 5, R.string.operation_refresh, R.drawable.ic_menu_refresh);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string) {
        addMenuItem(menu, itemId, order, string, -1);
    }

    private void addMenuItem(Menu menu, int itemId, int order, int string, int iconRes) {
        if (!mFileViewListener.shouldHideMenu(itemId)) {
            MenuItem item = menu.add(0, itemId, order, string).setOnMenuItemClickListener(
                    menuItemClick);
            if (iconRes > 0) {
                item.setIcon(iconRes);
            }
        }
    }

    public void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileInfo lFileInfo = mFileViewListener.getItem(position);
        showDropdownNavigation(false);

        if (lFileInfo == null) {
            Log.e(LOG_TAG, "file does not exist on position:" + position);
            return;
        }

        if (!lFileInfo.IsDir) {
            viewFile(lFileInfo);
            return;
        }

        mCurrentPath = getAbsoluteName(mCurrentPath, lFileInfo.fileName);
        ActionMode actionMode = ((FileExplorerActivity) mContext).getActionMode();
        if (actionMode != null) {
            actionMode.finish();
        }
        refreshFileList();
    }

    public void setRootPath(String path) {
        mRoot = path;
        mCurrentPath = path;
    }

    public String getRootPath() {
        return mRoot;
    }

    public String getCurrentPath() {
        return mCurrentPath;
    }

    public void setCurrentPath(String path) {
        mCurrentPath = path;
    }

    private String getAbsoluteName(String path, String name) {
        return path.equals(GlobalConsts.ROOT_PATH) ? path + name : path + File.separator + name;
    }

    private void viewFile(FileInfo lFileInfo) {
        try {
            IntentBuilder.viewFile(mContext, lFileInfo.filePath);
        } catch (ActivityNotFoundException e) {
            Log.e(LOG_TAG, "fail to view file: " + e.toString());
        }
    }

    public boolean onBackPressed() {
        if (mDropdownNavigation.getVisibility() == View.VISIBLE) {
            mDropdownNavigation.setVisibility(View.GONE);
        } else if (!onOperationUpLevel()) {
            return false;
        }
        return true;
    }

    private void showDropdownNavigation(boolean show) {
        mDropdownNavigation.setVisibility(show ? View.VISIBLE : View.GONE);
        mNavigationBarUpDownArrow
                .setImageResource(mDropdownNavigation.getVisibility() == View.VISIBLE ? R.drawable.arrow_up
                        : R.drawable.arrow_down);
    }
}
