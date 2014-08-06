package com.soyomaker.handsgonew.ui.fileexplorer;

import java.util.Collection;

import android.content.Context;
import android.content.Intent;
import android.view.View;

public interface IFileInteractionListener {

	public View getViewById(int id);

	public Context getContext();

	public void startActivity(Intent intent);

	public void onDataChanged();

	public void onPick(FileInfo f);

	public boolean shouldShowOperationPane();

	/**
	 * Handle operation listener.
	 * 
	 * @param id
	 * @return true: indicate have operated it; false: otherwise.
	 */
	public boolean onOperation(int id);

	public String getDisplayPath(String path);

	public String getRealPath(String displayPath);

	public void runOnUiThread(Runnable r);

	// return true indicates the navigation has been handled
	public boolean onNavigation(String path);

	public boolean shouldHideMenu(int menu);

	public FileIconHelper getFileIconHelper();

	public FileInfo getItem(int pos);

	public void sortCurrentList(FileSortHelper sort);

	public Collection<FileInfo> getAllFiles();

	public void addSingleFile(FileInfo file);

	public boolean onRefreshFileList(String path, FileSortHelper sort);

	public int getItemCount();
}
