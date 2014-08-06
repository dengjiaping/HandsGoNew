package com.soyomaker.handsgonew.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.soyomaker.handsgonew.R;

@SuppressLint("ViewConstructor")
public class GoBoard extends Grid {

	protected float mDensity = 1.5f;

	protected Bitmap mBlackCubic = null;

	protected Bitmap mWhiteCubic = null;

	protected int mBasicTextSize = 9;

	protected int mDefaultBackgroundColor = 0xffEE9A00;

	public GoBoard(Context context, AttributeSet attrs, IBoardModel gm, int cubicSize,
			int leftRightBorder, int topBottomBorder) {
		super(context, attrs, gm.getGridModel(), cubicSize, leftRightBorder, topBottomBorder);
		initView();
	}

	public GoBoard(Context context, IBoardModel gm, int cubicSize, int leftRightBorder,
			int topBottomBorder) {
		super(context, gm.getGridModel(), cubicSize, leftRightBorder, topBottomBorder);
		initView();
	}

	private void initView() {
		this.mBlackCubic = BitmapFactory.decodeResource(this.getResources(), R.drawable.b2);
		this.mWhiteCubic = BitmapFactory.decodeResource(this.getResources(), R.drawable.w2);
		this.mDensity = this.getResources().getDisplayMetrics().density;
		// this.mDefaultBackgroundColor =
	}

	public int getBoardBackgroundColor() {
		return mDefaultBackgroundColor;
	}

	public void setBoardBackgroundColor(int color) {
		mDefaultBackgroundColor = color;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mCubicSize * mGridModel.getGridSize() + 2 * mLeftRightBorder,
				mCubicSize * mGridModel.getGridSize() + 2 * mTopBottomBorder);
	}

	@Override
	public void drawBoard(Canvas g) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		// 1，绘制棋盘背景色
		paint.setColor(mDefaultBackgroundColor);
		g.drawRect(0, 0, mCubicSize * mGridModel.getGridSize() + 2 * mLeftRightBorder, mCubicSize
				* mGridModel.getGridSize() + 2 * mTopBottomBorder, paint);

		// 2，绘制棋盘坐标
		paint.setColor(0xff000000);
		paint.setTextSize(Math.round(mBasicTextSize * mDensity));
		// 纵坐标
		for (int i = 0; i < mGridModel.getGridSize(); i++) {
			g.drawText("" + (i + 1), 7, (mGridModel.getGridSize() - i) * mCubicSize + 7, paint);
		}
		// 横坐标
		char c = 'A';
		for (int i = 0; i < mGridModel.getGridSize(); i++) {
			char cc = (char) (c + i);
			if (cc >= 'I') {
				g.drawText("" + (char) (cc + 1), (i + 1) * mCubicSize - 7, mCubicSize - 7, paint);
			} else {
				g.drawText("" + cc, (i + 1) * mCubicSize - 7, mCubicSize - 7, paint);
			}
		}

		// 3，绘制棋盘线
		paint.setColor(0xff000000);
		g.translate(mLeftRightBorder + mCubicSize / 2, mTopBottomBorder + mCubicSize / 2);
		for (int i = 0; i < mGridModel.getGridSize(); i++) {
			g.drawLine(0, i * mCubicSize, mCubicSize * (mGridModel.getGridSize() - 1), i
					* mCubicSize, paint);
			g.drawLine(i * mCubicSize, 0, i * mCubicSize, mCubicSize
					* (mGridModel.getGridSize() - 1), paint);
		}
		g.translate(-(mLeftRightBorder + mCubicSize / 2), -(mTopBottomBorder + mCubicSize / 2));

		// 4，绘制星位
		int gridSize = mGridModel.getGridSize();
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				switch (gridSize) {
				case 9:
					if ((i == 2 || i == 6) && (j == 2 || j == 6) || (i == 4 && j == 4)) {
						drawStar(g, i, j, paint);
					}
					break;
				case 13:
					if ((i == 3 || i == 9) && (j == 3 || j == 9) || (i == 6 && j == 6)) {
						drawStar(g, i, j, paint);
					}
					break;
				case 19:
				default:
					if ((i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15)) {
						drawStar(g, i, j, paint);
					}
					break;
				}
			}
		}
	}

	/**
	 * 绘制星位
	 */
	private void drawStar(Canvas g, int i, int j, Paint paint) {
		g.translate(mLeftRightBorder + i * mCubicSize, mTopBottomBorder + j * mCubicSize);
		paint.setColor(0xff000000);
		paint.setStyle(Style.FILL);
		g.drawArc(new RectF(mCubicSize / 2 - 4, mCubicSize / 2 - 4, mCubicSize / 2 + 4,
				mCubicSize / 2 + 4), 0, 360, true, paint);
		g.translate(-(mLeftRightBorder + i * mCubicSize), -(mTopBottomBorder + j * mCubicSize));
	}

	@Override
	public void drawChessman(Canvas g, int col, int row) {

	}
}
