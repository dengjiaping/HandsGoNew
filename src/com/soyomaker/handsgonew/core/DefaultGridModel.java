package com.soyomaker.handsgonew.core;

public class DefaultGridModel implements IGridModel {

	protected GoPoint mBlocks[][];

	protected int mGridSize;

	protected IGridModelListener mListener = null;

	public DefaultGridModel(int gridSize) {
		reset(gridSize);
	}

	@Override
	public int getGridSize() {
		return mGridSize;
	}

	@Override
	public boolean isEmpty(int x, int y) {
		if (mBlocks[x][y] == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void reset(int gridSize) {
		mGridSize = gridSize;

		mBlocks = new GoPoint[mGridSize][mGridSize];
		for (int i = 0; i < mGridSize; i++) {
			for (int j = 0; j < mGridSize; j++) {
				mBlocks[i][j] = new GoPoint();
			}
		}
		fireDataChanged();
	}

	@Override
	public void setGridModelListener(IGridModelListener l) {
		mListener = l;
	}

	@Override
	public GoPoint getObject(int x, int y) {
		return mBlocks[x][y];
	}

	@Override
	public void setObject(int x, int y, GoPoint obj) {
		mBlocks[x][y] = obj;
		fireDataChanged();
	}

	private void fireDataChanged() {
		if (mListener != null) {
			mListener.gridDataChanged();
		}
	}
}
