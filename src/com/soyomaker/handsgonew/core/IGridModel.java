package com.soyomaker.handsgonew.core;

public interface IGridModel {

	public int getGridSize();

	public boolean isEmpty(int col, int row);

	public void reset(int gridSize);

	public void setGridModelListener(IGridModelListener l);

	public GoPoint getObject(int col, int row);

	public void setObject(int col, int row, GoPoint c);
}
