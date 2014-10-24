package com.soyomaker.handsgo.ui.fileexplorer;

import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.ui.fileexplorer.FileSortHelper.SortMethod;
import com.soyomaker.handsgo.ui.fileexplorer.MediaFile.MediaFileType;

public class FileCategoryHelper {

	public static final int COLUMN_ID = 0;
	public static final int COLUMN_PATH = 1;
	public static final int COLUMN_SIZE = 2;
	public static final int COLUMN_DATE = 3;

	public enum FileCategory {
		All, Music, Video, Picture, Theme, Doc, Zip, Apk, Custom, Other, Favorite
	}

	private static final String LOG_TAG = "FileCategoryHelper";

	private static String APK_EXT = "apk";
	private static String THEME_EXT = "mtz";
	private static String[] ZIP_EXTS = new String[] { "zip", "rar" };

	public static HashMap<FileCategory, FilenameExtFilter> sFilters = new HashMap<FileCategory, FilenameExtFilter>();

	public static HashMap<FileCategory, Integer> sCategoryNames = new HashMap<FileCategory, Integer>();

	static {
		sCategoryNames.put(FileCategory.All, R.string.category_all);
		sCategoryNames.put(FileCategory.Music, R.string.category_music);
		sCategoryNames.put(FileCategory.Video, R.string.category_video);
		sCategoryNames.put(FileCategory.Picture, R.string.category_picture);
		sCategoryNames.put(FileCategory.Theme, R.string.category_theme);
		sCategoryNames.put(FileCategory.Doc, R.string.category_document);
		sCategoryNames.put(FileCategory.Zip, R.string.category_zip);
		sCategoryNames.put(FileCategory.Apk, R.string.category_apk);
		sCategoryNames.put(FileCategory.Other, R.string.category_other);
		sCategoryNames.put(FileCategory.Favorite, R.string.category_favorite);
	}

	public static FileCategory[] sCategories = new FileCategory[] { FileCategory.Music,
			FileCategory.Video, FileCategory.Picture, FileCategory.Theme, FileCategory.Doc,
			FileCategory.Zip, FileCategory.Apk, FileCategory.Other };

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

	public void setCustomCategory(String[] exts) {
		mCategory = FileCategory.Custom;
		if (sFilters.containsKey(FileCategory.Custom)) {
			sFilters.remove(FileCategory.Custom);
		}

		sFilters.put(FileCategory.Custom, new FilenameExtFilter(exts));
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

	private void setCategoryInfo(FileCategory fc, long count, long size) {
		CategoryInfo info = mCategoryInfo.get(fc);
		if (info == null) {
			info = new CategoryInfo();
			mCategoryInfo.put(fc, info);
		}
		info.count = count;
		info.size = size;
	}

	private String buildDocSelection() {
		StringBuilder selection = new StringBuilder();
		Iterator<String> iter = Util.sDocMimeTypesSet.iterator();
		while (iter.hasNext()) {
			selection.append("(" + FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
		}
		return selection.substring(0, selection.lastIndexOf(")") + 1);
	}

	private String buildSelectionByCategory(FileCategory cat) {
		String selection = null;
		switch (cat) {
		case Theme:
			selection = FileColumns.DATA + " LIKE '%.mtz'";
			break;
		case Doc:
			selection = buildDocSelection();
			break;
		case Zip:
			selection = "(" + FileColumns.MIME_TYPE + " == '" + Util.sZipFileMimeType + "')";
			break;
		case Apk:
			selection = FileColumns.DATA + " LIKE '%.apk'";
			break;
		default:
			selection = null;
		}
		return selection;
	}

	private Uri getContentUriByCategory(FileCategory cat) {
		Uri uri;
		String volumeName = "external";
		switch (cat) {
		case Theme:
		case Doc:
		case Zip:
		case Apk:
			uri = Files.getContentUri(volumeName);
			break;
		case Music:
			uri = Audio.Media.getContentUri(volumeName);
			break;
		case Video:
			uri = Video.Media.getContentUri(volumeName);
			break;
		case Picture:
			uri = Images.Media.getContentUri(volumeName);
			break;
		default:
			uri = null;
		}
		return uri;
	}

	private String buildSortOrder(SortMethod sort) {
		String sortOrder = null;
		switch (sort) {
		case name:
			sortOrder = FileColumns.TITLE + " asc";
			break;
		case size:
			sortOrder = FileColumns.SIZE + " asc";
			break;
		case date:
			sortOrder = FileColumns.DATE_MODIFIED + " desc";
			break;
		case type:
			sortOrder = FileColumns.MIME_TYPE + " asc, " + FileColumns.TITLE + " asc";
			break;
		}
		return sortOrder;
	}

	public Cursor query(FileCategory fc, SortMethod sort) {
		Uri uri = getContentUriByCategory(fc);
		String selection = buildSelectionByCategory(fc);
		String sortOrder = buildSortOrder(sort);

		if (uri == null) {
			Log.e(LOG_TAG, "invalid uri, category:" + fc.name());
			return null;
		}

		String[] columns = new String[] { FileColumns._ID, FileColumns.DATA, FileColumns.SIZE,
				FileColumns.DATE_MODIFIED };

		return mContext.getContentResolver().query(uri, columns, selection, null, sortOrder);
	}

	public void refreshCategoryInfo() {
		// clear
		for (FileCategory fc : sCategories) {
			setCategoryInfo(fc, 0, 0);
		}

		// query database
		String volumeName = "external";

		Uri uri = Audio.Media.getContentUri(volumeName);
		refreshMediaCategory(FileCategory.Music, uri);

		uri = Video.Media.getContentUri(volumeName);
		refreshMediaCategory(FileCategory.Video, uri);

		uri = Images.Media.getContentUri(volumeName);
		refreshMediaCategory(FileCategory.Picture, uri);

		uri = Files.getContentUri(volumeName);
		refreshMediaCategory(FileCategory.Theme, uri);
		refreshMediaCategory(FileCategory.Doc, uri);
		refreshMediaCategory(FileCategory.Zip, uri);
		refreshMediaCategory(FileCategory.Apk, uri);
	}

	private boolean refreshMediaCategory(FileCategory fc, Uri uri) {
		String[] columns = new String[] { "COUNT(*)", "SUM(_size)" };
		Cursor c = mContext.getContentResolver().query(uri, columns, buildSelectionByCategory(fc),
				null, null);
		if (c == null) {
			Log.e(LOG_TAG, "fail to query uri:" + uri);
			return false;
		}

		if (c.moveToNext()) {
			setCategoryInfo(fc, c.getLong(0), c.getLong(1));
			Log.v(LOG_TAG, "Retrieved " + fc.name() + " info >>> count:" + c.getLong(0) + " size:"
					+ c.getLong(1));
			c.close();
			return true;
		}

		return false;
	}

	public static FileCategory getCategoryFromPath(String path) {
		MediaFileType type = MediaFile.getFileType(path);
		if (type != null) {
			if (MediaFile.isAudioFileType(type.fileType))
				return FileCategory.Music;
			if (MediaFile.isVideoFileType(type.fileType))
				return FileCategory.Video;
			if (MediaFile.isImageFileType(type.fileType))
				return FileCategory.Picture;
			if (Util.sDocMimeTypesSet.contains(type.mimeType))
				return FileCategory.Doc;
		}

		int dotPosition = path.lastIndexOf('.');
		if (dotPosition < 0) {
			return FileCategory.Other;
		}

		String ext = path.substring(dotPosition + 1);
		if (ext.equalsIgnoreCase(APK_EXT)) {
			return FileCategory.Apk;
		}
		if (ext.equalsIgnoreCase(THEME_EXT)) {
			return FileCategory.Theme;
		}

		if (matchExts(ext, ZIP_EXTS)) {
			return FileCategory.Zip;
		}

		return FileCategory.Other;
	}

	private static boolean matchExts(String ext, String[] exts) {
		for (String ex : exts) {
			if (ex.equalsIgnoreCase(ext))
				return true;
		}
		return false;
	}
}
