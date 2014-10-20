package com.soyomaker.handsgo.core;

public interface IGridListener {

	public void touchPressed(int col, int row);

	public void touchReleased(int col, int row);

	public void touchMoved(int col, int row);
}
