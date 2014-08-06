package com.soyomaker.handsgonew.core;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public abstract class Grid extends View implements IGridModelListener {

	/**
	 * 格子大小
	 */
	protected int mCubicSize;

	/**
	 * 左右边界间距
	 */
	protected int mLeftRightBorder;

	/**
	 * 上下边界间距
	 */
	protected int mTopBottomBorder;

	protected IGridListener mGridListener;
	protected IGridModel mGridModel;

	public Grid(Context context, IGridModel gm, int lds, int lrb, int tbb) {
		super(context);
		mGridModel = gm;
		mGridModel.setGridModelListener(this);

		mCubicSize = lds;
		mLeftRightBorder = lrb;
		mTopBottomBorder = tbb;
	}

	public Grid(Context context, AttributeSet attrs, IGridModel gm, int lds, int lrb, int tbb) {
		super(context, attrs);
		mGridModel = gm;
		mGridModel.setGridModelListener(this);

		mCubicSize = lds;
		mLeftRightBorder = lrb;
		mTopBottomBorder = tbb;
	}

	public void setGridListener(IGridListener listener) {
		mGridListener = listener;
	}

	@Override
	public void gridDataChanged() {
		this.postInvalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawBoard(canvas);
		int gridSize = mGridModel.getGridSize();
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				canvas.translate(mLeftRightBorder + i * mCubicSize, mTopBottomBorder + j
						* mCubicSize);
				drawChessman(canvas, i, j);
				canvas.translate(-(mLeftRightBorder + i * mCubicSize), -(mTopBottomBorder + j
						* mCubicSize));
			}
		}
	}

	public void pointerMoved(int x, int y) {
		if (mGridListener == null) {
			return;
		}

		int gridSize = mGridModel.getGridSize();

		int i = (x - mLeftRightBorder) / mCubicSize;
		int j = (y - mTopBottomBorder) / mCubicSize;

		if ((i < 0) || (i >= gridSize) || (j < 0) || (j >= gridSize)) {
			return;
		}

		mGridListener.touchMoved(i, j);
	}

	public void pointerPressed(int x, int y) {
		if (mGridListener == null) {
			return;
		}

		int gridSize = mGridModel.getGridSize();

		int i = (x - mLeftRightBorder) / mCubicSize;
		int j = (y - mTopBottomBorder) / mCubicSize;

		if ((i < 0) || (i >= gridSize) || (j < 0) || (j >= gridSize)) {
			return;
		}

		mGridListener.touchPressed(i, j);
	}

	public void pointerReleased(int x, int y) {
		if (mGridListener == null) {
			return;
		}

		int gridSize = mGridModel.getGridSize();

		int i = (x - mLeftRightBorder) / mCubicSize;
		int j = (y - mTopBottomBorder) / mCubicSize;

		if ((i < 0) || (i >= gridSize) || (j < 0) || (j >= gridSize)) {
			return;
		}

		mGridListener.touchReleased(i, j);
	}

	/**
	 * 绘制棋盘
	 * 
	 * @param g
	 */
	public abstract void drawBoard(Canvas g);

	/**
	 * 绘制棋子
	 * 
	 * @param g
	 * @param col
	 * @param row
	 */
	public abstract void drawChessman(Canvas g, int col, int row);
}
