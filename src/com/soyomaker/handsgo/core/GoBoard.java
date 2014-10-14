package com.soyomaker.handsgo.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;

@SuppressLint("ViewConstructor")
public class GoBoard extends Grid {

    protected float mDensity = 1.5f;

    protected Bitmap mYunziBlackCubic;
    protected Bitmap mYunziWhiteCubic;

    protected Bitmap mHajiBlackCubic;
    protected Bitmap mHajiziWhiteCubic;

    protected int mBasicTextSize = 9;

    protected Paint mChessmanPaint;

    protected Paint mBoardPaint;

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
        this.mYunziBlackCubic = BitmapFactory.decodeResource(this.getResources(), R.drawable.b2);
        this.mYunziWhiteCubic = BitmapFactory.decodeResource(this.getResources(), R.drawable.w2);

        this.mHajiBlackCubic = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.stone_black);
        this.mHajiziWhiteCubic = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.stone_white);

        this.mDensity = this.getResources().getDisplayMetrics().density;

        this.mBoardPaint = new Paint();
        this.mBoardPaint.setAntiAlias(true);

        this.mChessmanPaint = new Paint();
        this.mChessmanPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCubicSize * mGridModel.getGridSize() + 2 * mLeftRightBorder,
                mCubicSize * mGridModel.getGridSize() + 2 * mTopBottomBorder);
    }

    @Override
    public void drawBoard(Canvas g) {
        // 1，绘制棋盘背景色
        mBoardPaint.setColor(AppPrefrence.getChessBoardColor(getContext()));
        g.drawRect(0, 0, mCubicSize * mGridModel.getGridSize() + 2 * mLeftRightBorder, mCubicSize
                * mGridModel.getGridSize() + 2 * mTopBottomBorder, mBoardPaint);

        if (AppPrefrence.getShowCoordinate(getContext())) {
            // 2，绘制棋盘坐标
            mBoardPaint.setColor(Color.BLACK);
            mBoardPaint.setTextSize(Math.round(mBasicTextSize * mDensity));
            // 纵坐标
            for (int i = 0; i < mGridModel.getGridSize(); i++) {
                g.drawText("" + (i + 1), 7, (mGridModel.getGridSize() - i) * mCubicSize + 7,
                        mBoardPaint);
            }
            // 横坐标
            char c = 'A';
            for (int i = 0; i < mGridModel.getGridSize(); i++) {
                char cc = (char) (c + i);
                if (cc >= 'I') {
                    g.drawText("" + (char) (cc + 1), (i + 1) * mCubicSize - 7, mCubicSize - 7,
                            mBoardPaint);
                } else {
                    g.drawText("" + cc, (i + 1) * mCubicSize - 7, mCubicSize - 7, mBoardPaint);
                }
            }
        }

        // 3，绘制棋盘线
        mBoardPaint.setColor(Color.BLACK);
        g.translate(mLeftRightBorder + mCubicSize / 2, mTopBottomBorder + mCubicSize / 2);
        for (int i = 0; i < mGridModel.getGridSize(); i++) {
            g.drawLine(0, i * mCubicSize, mCubicSize * (mGridModel.getGridSize() - 1), i
                    * mCubicSize, mBoardPaint);
            g.drawLine(i * mCubicSize, 0, i * mCubicSize, mCubicSize
                    * (mGridModel.getGridSize() - 1), mBoardPaint);
        }
        g.translate(-(mLeftRightBorder + mCubicSize / 2), -(mTopBottomBorder + mCubicSize / 2));

        // 4，绘制星位
        int gridSize = mGridModel.getGridSize();
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                switch (gridSize) {
                case 9:
                    if ((i == 2 || i == 6) && (j == 2 || j == 6) || (i == 4 && j == 4)) {
                        drawStar(g, i, j);
                    }
                    break;
                case 13:
                    if ((i == 3 || i == 9) && (j == 3 || j == 9) || (i == 6 && j == 6)) {
                        drawStar(g, i, j);
                    }
                    break;
                case 19:
                default:
                    if ((i == 3 || i == 9 || i == 15) && (j == 3 || j == 9 || j == 15)) {
                        drawStar(g, i, j);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void drawChessman(Canvas g, int col, int row) {
        // 绘制棋子和手数
        drawPiece(g, col, row);
        // 绘制分支位置
        drawTreeNode(g, col, row);
        // 绘制当前高亮状态
        drawStyle(g, col, row);
        // 绘制标记
        drawMark(g, col, row);
        // 绘制字母
        drawLetter(g, col, row);
        // 绘制标签
        drawLabel(g, col, row);
    }

    /**
     * 绘制星位
     * 
     * @param g
     * @param i
     * @param j
     */
    private void drawStar(Canvas g, int i, int j) {
        g.translate(mLeftRightBorder + i * mCubicSize, mTopBottomBorder + j * mCubicSize);
        mBoardPaint.setColor(Color.BLACK);
        mBoardPaint.setStyle(Style.FILL);
        g.drawArc(new RectF(mCubicSize / 2 - 4, mCubicSize / 2 - 4, mCubicSize / 2 + 4,
                mCubicSize / 2 + 4), 0, 360, true, mBoardPaint);
        g.translate(-(mLeftRightBorder + i * mCubicSize), -(mTopBottomBorder + j * mCubicSize));
    }

    /**
     * 绘制棋子和手数
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawPiece(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        if (p.getPlayer() == GoPoint.PLAYER_BLACK) {
            // 绘制棋子
            switch (AppPrefrence.getChessPieceStyle(getContext())) {
            case AppConstants.CHESS_PIECE_STYLE_3D_1: {
                g.drawBitmap(mYunziBlackCubic, new Rect(0, 0, mYunziBlackCubic.getWidth(),
                        mYunziBlackCubic.getHeight()), new Rect(cb / 24, cb / 24, 23 * cb / 24,
                        23 * cb / 24), mChessmanPaint);
            }
                break;
            case AppConstants.CHESS_PIECE_STYLE_3D_2: {
                g.drawBitmap(mHajiBlackCubic, new Rect(0, 0, mHajiBlackCubic.getWidth(),
                        mHajiBlackCubic.getHeight()), new Rect(cb / 24, cb / 24, 23 * cb / 24,
                        23 * cb / 24), mChessmanPaint);
            }
                break;
            case AppConstants.CHESS_PIECE_STYLE_2D: {
                mChessmanPaint.setColor(Color.BLACK);
                mChessmanPaint.setStyle(Style.FILL);
                g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24, cb * 23 / 24), 0, 360, true,
                        mChessmanPaint);
            }
                break;
            }
            // 绘制手数
            if (p.getNumber() > 0 && AppPrefrence.getShowNumber(getContext())) {
                mChessmanPaint.setColor(Color.WHITE);
                TextPaint tp = new TextPaint();
                tp.set(mChessmanPaint);
                tp.setTextSize(Math.round(mBasicTextSize * mDensity));
                float width = tp.measureText("" + p.getNumber());
                g.drawText("" + p.getNumber(), cb / 2 - width / 2 - 1,
                        cb / 2 + Math.round(6 * mDensity) / 2, tp);
            }
        } else if (p.getPlayer() == GoPoint.PLAYER_WHITE) {
            // 绘制棋子
            switch (AppPrefrence.getChessPieceStyle(getContext())) {
            case AppConstants.CHESS_PIECE_STYLE_3D_1: {
                g.drawBitmap(mYunziWhiteCubic, new Rect(0, 0, mYunziWhiteCubic.getWidth(),
                        mYunziWhiteCubic.getHeight()), new Rect(cb / 24, cb / 24, 23 * cb / 24,
                        23 * cb / 24), mChessmanPaint);
            }
                break;
            case AppConstants.CHESS_PIECE_STYLE_3D_2: {
                g.drawBitmap(mHajiziWhiteCubic, new Rect(0, 0, mHajiziWhiteCubic.getWidth(),
                        mHajiziWhiteCubic.getHeight()), new Rect(cb / 24, cb / 24, 23 * cb / 24,
                        23 * cb / 24), mChessmanPaint);
            }
                break;
            case AppConstants.CHESS_PIECE_STYLE_2D: {
                mChessmanPaint.setColor(Color.WHITE);
                mChessmanPaint.setStyle(Style.FILL);
                g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24, cb * 23 / 24), 0, 360, true,
                        mChessmanPaint);
                mChessmanPaint.setColor(Color.BLACK);
                mChessmanPaint.setStyle(Style.STROKE);
                g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24, cb * 23 / 24), 0, 360, true,
                        mChessmanPaint);
            }
                break;
            }
            // 绘制手数
            if (p.getNumber() > 0 && AppPrefrence.getShowNumber(getContext())) {
                mChessmanPaint.setColor(Color.BLACK);
                TextPaint tp = new TextPaint();
                tp.set(mChessmanPaint);
                tp.setTextSize(Math.round(mBasicTextSize * mDensity));
                float width = tp.measureText("" + p.getNumber());
                g.drawText("" + p.getNumber(), cb / 2 - width / 2 - 1,
                        cb / 2 + Math.round(6 * mDensity) / 2, tp);
            }
        }
    }

    /**
     * 绘制分支位置
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawTreeNode(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        if (p.getTreeNode() != null) {
            mChessmanPaint.setColor(Color.GREEN);
            g.drawLine(cb / 4, cb / 2, cb * 3 / 4, cb / 2, mChessmanPaint);
            g.drawLine(cb / 2, cb / 4, cb / 2, cb * 3 / 4, mChessmanPaint);
        }
    }

    /**
     * 绘制当前高亮状态
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawStyle(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        if (p.getStyle() == GoPoint.STYLE_HIGHLIGHT) {
            mChessmanPaint.setColor(Color.RED);
            mChessmanPaint.setStyle(Style.FILL);
            g.drawArc(new RectF(cb / 2 - 6, cb / 2 - 6, cb / 2 + 6, cb / 2 + 6), 0, 360, true,
                    mChessmanPaint);
        }
    }

    /**
     * 绘制标记
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawMark(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        int mark = p.getMark();
        if (mark != GoPoint.NONE) {
            mChessmanPaint.setColor(Color.RED);
            mChessmanPaint.setStyle(Style.FILL);
            if (mark == GoPoint.TRIANGLE) {
                // 绘制这个三角形,你可以绘制任意多边形
                Path path = new Path();
                // 此点为多边形的起点
                path.moveTo(cb / 2, cb / 5);
                path.lineTo(cb * 3 / 4, cb * 3 / 5);
                path.lineTo(cb / 4, cb * 3 / 5);
                // 使这些点构成封闭的多边形
                path.close();
                g.drawPath(path, mChessmanPaint);
            } else if (mark == GoPoint.SQUARE) {
                g.drawRect(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4, mChessmanPaint);
            } else if (mark == GoPoint.CIRCLE) {
                g.drawArc(new RectF(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4), 0, 360, true,
                        mChessmanPaint);
            } else if (mark == GoPoint.CROSS) {
                g.drawLine(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4, mChessmanPaint);
                g.drawLine(cb / 4, cb * 3 / 4, cb * 3 / 4, cb / 4, mChessmanPaint);
            }
        }
    }

    /**
     * 绘制字母
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawLetter(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        int letter = p.getLetter();
        if (letter != GoPoint.NONE) {
            mChessmanPaint.setColor(Color.MAGENTA);
            char cp = (char) letter;
            TextPaint tp = new TextPaint();
            tp.set(mChessmanPaint);
            tp.setTextSize(Math.round(12 * mDensity));
            float width = tp.measureText("" + cp);
            g.drawText("" + cp, cb / 2 - width / 2, cb / 2 + Math.round(8 * mDensity) / 2, tp);
        }
    }

    /**
     * 绘制标签
     * 
     * @param g
     * @param col
     * @param row
     */
    private void drawLabel(Canvas g, int col, int row) {
        GoPoint p = (GoPoint) mGridModel.getObject(col, row);
        int cb = mCubicSize;
        String label = p.getLabel();
        if (!TextUtils.isEmpty(label)) {
            mChessmanPaint.setColor(Color.MAGENTA);
            TextPaint tp = new TextPaint();
            tp.set(mChessmanPaint);
            tp.setTextSize(Math.round(12 * mDensity));
            float width = tp.measureText(label);
            g.drawText(label, cb / 2 - width / 2, cb / 2 + Math.round(8 * mDensity) / 2, tp);
        }
    }
}
