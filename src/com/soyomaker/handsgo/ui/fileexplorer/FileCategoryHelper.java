package com.soyomaker.handsgo.ui.fileexplorer;

import java.io.FilenameFilter;
import java.util.HashMap;

import android.content.Context;

import com.soyomaker.handsgo.R;

public class FileCategoryHelper {

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_PATH = 1;
    public static final int COLUMN_SIZE = 2;
    public static final int COLUMN_DATE = 3;

    public enum FileCategory {
        All, Other
    }

    public static HashMap<FileCategory, FilenameExtFilter> sFilters = new HashMap<FileCategory, FilenameExtFilter>();

    public static HashMap<FileCategory, Integer> sCategoryNames = new HashMap<FileCategory, Integer>();

    static {
        sCategoryNames.put(FileCategory.All, R.string.category_all);
        sCategoryNames.put(FileCategory.Other, R.string.category_other);
    }

    public static FileCategory[] sCategories = new FileCategory[] { FileCategory.Other };

    private FileCategory mCategory;

    private Context mContext;

    public FileCategoryHelper(Context context) {
        mContext = context;

        mCategory = FileCategory.All;
    }

    public FileCategory getCurCategory() {
        return mCategory;
    }

    public void setCurCategory(FileCategory c) {
        mCategory = c;
    }

    public int getCurCategoryNameResId() {
        return sCategoryNames.get(mCategory);
    }

    public FilenameFilter getFilter() {
        return sFilters.get(mCategory);
    }

    private HashMap<FileCategory, CategoryInfo> mCategoryInfo = new HashMap<FileCategory, CategoryInfo>();

    public HashMap<FileCategory, CategoryInfo> getCategoryInfos() {
        return mCategoryInfo;
    }

    public CategoryInfo getCategoryInfo(FileCategory fc) {
        if (mCategoryInfo.containsKey(fc)) {
            return mCategoryInfo.get(fc);
        } else {
            CategoryInfo info = new CategoryInfo();
            mCategoryInfo.put(fc, info);
            return info;
        }
    }

    public class CategoryInfo {
        public long count;

        public long size;
    }

    public static FileCategory getCategoryFromPath(String path) {
        return FileCategory.Other;
    }
}
