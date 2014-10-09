package com.soyomaker.handsgonew.core;

import com.soyomaker.handsgonew.util.LogUtil;

public class DefaultBoardModel implements IBoardModel {

	private IGridModel mGridModel;

	private int mNextPlayer = GoPoint.PLAYER_BLACK;

	private boolean mShowHighlight = true;

	public DefaultBoardModel(int bsize) {
		mGridModel = new DefaultGridModel(bsize);
	}

	@Override
	public void forcePut(int x, int y, GoPoint point) {
		if (!validatePoint(x, y)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() != GoPoint.PLAYER_EMPTY) {
			return;
		}

		p.setPlayer(point.getPlayer());
		p.setNumber(point.getNumber());

		resetHighlight();
	}

	@Override
	public void forceRemove(int x, int y) {
		if (!validatePoint(x, y)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() == GoPoint.PLAYER_EMPTY) {
			return;
		}

		p.setPlayer(GoPoint.PLAYER_EMPTY);
		p.setNumber(GoPoint.NONE);

		resetHighlight();
	}

	/**
	 * 重置高亮落子
	 */
	private void resetHighlight() {
		// 寻找当前局势的最后一步棋（通过比较number）
		GoPoint lastPoint = null;
		int number = -Integer.MAX_VALUE;
		for (int i = 0; i < getBoardSize(); i++) {
			for (int j = 0; j < getBoardSize(); j++) {
				GoPoint p = getPoint(i, j);
				p.setStyle(GoPoint.STYLE_GENERAL);

				int player = p.getPlayer();
				if (player == GoPoint.PLAYER_BLACK || player == GoPoint.PLAYER_WHITE) {
					int pNumber = p.getNumber();
					if (number < pNumber) {
						number = pNumber;
						lastPoint = p;
					}
				}
			}
		}
		// 将最后一步设为高亮显示
		if (lastPoint != null && mShowHighlight) {
			lastPoint.setStyle(GoPoint.STYLE_HIGHLIGHT);
		}
	}

	public void setShowHighlight(boolean showHighlight) {
		mShowHighlight = showHighlight;
	}

	public boolean getShowHighlight() {
		return mShowHighlight;
	}

	public void setNextPlayer(int nextPlayer) {
		mNextPlayer = nextPlayer;
	}

	public int getNextPlayer() {
		return mNextPlayer;
	}

	@Override
	public int getBoardSize() {
		return mGridModel.getGridSize();
	}

	@Override
	public IGridModel getGridModel() {
		return mGridModel;
	}

	@Override
	public GoPoint getPoint(int col, int row) {
		return mGridModel.getObject(col, row);
	}

	@Override
	public void performPut(int x, int y, GoPoint point) throws GoException {
		if (!validatePoint(x, y)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() != GoPoint.PLAYER_EMPTY) {
			throw new GoException("There is already one chessman");
		}

		p.setPlayer(point.getPlayer());
		p.setNumber(point.getNumber());

		resetHighlight();
	}

	@Override
	public void performRemove(int x, int y) throws GoException {
		if (!validatePoint(x, y)) {
			throw new GoException("Invalid point");
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() == GoPoint.PLAYER_EMPTY) {
			throw new GoException("No chessman to remove");
		}

		p.setPlayer(GoPoint.PLAYER_EMPTY);
		p.setNumber(GoPoint.NONE);

		resetHighlight();
	}

	private boolean validatePoint(int col, int row) {
		if ((col < 0) || (col >= getBoardSize()) || (row < 0) || (row >= getBoardSize())) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void reset(int boardSize) {
		mGridModel.reset(boardSize);
		mNextPlayer = GoPoint.PLAYER_BLACK;
		resetHighlight();
	}

	public void print() {
		StringBuffer buffer = new StringBuffer();

		int size = getBoardSize();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				GoPoint goPoint = getPoint(j, i);
				int player = goPoint.getPlayer();
				if (player == GoPoint.PLAYER_BLACK) {
					buffer.append("●");
				} else if (player == GoPoint.PLAYER_WHITE) {
					buffer.append("○");
				} else {
					buffer.append(" ");
				}
				buffer.append(",");
			}
			buffer.append("\n");
		}

		LogUtil.e("DefaultBoardModel", buffer.toString());
	}

	public void markRek(int i, int j, int c) {
		GoPoint goPoint = getPoint(i, j);
		int size = getBoardSize();
		if (goPoint.isMarked() || goPoint.getPlayer() != c) {
			return;
		}
		goPoint.setMarked(true);
		if (i > 0) {
			markRek(i - 1, j, c);
		}
		if (j > 0) {
			markRek(i, j - 1, c);
		}
		if (i < size - 1) {
			markRek(i + 1, j, c);
		}
		if (j < size - 1) {
			markRek(i, j + 1, c);
		}
	}

	public void markGroup(int n, int m) {
		unmarkAll();
		GoPoint goPoint = getPoint(n, m);
		markRek(n, m, goPoint.getPlayer());
	}

	public boolean markRekTest(int i, int j, int c, int ct) {
		GoPoint goPoint = getPoint(i, j);
		int size = getBoardSize();
		if (goPoint.isMarked())
			return false;
		if (goPoint.getPlayer() != c) {
			if (goPoint.getPlayer() == ct) {
				return true;
			} else {
				return false;
			}
		}
		goPoint.setMarked(true);
		if (i > 0) {
			if (markRekTest(i - 1, j, c, ct)) {
				return true;
			}
		}
		if (j > 0) {
			if (markRekTest(i, j - 1, c, ct)) {
				return true;
			}
		}
		if (i < size - 1) {
			if (markRekTest(i + 1, j, c, ct)) {
				return true;
			}
		}
		if (j < size - 1) {
			if (markRekTest(i, j + 1, c, ct)) {
				return true;
			}
		}
		return false;
	}

	public boolean markGroupTest(int n, int m, int ct) {
		unmarkAll();
		GoPoint goPoint = getPoint(n, m);
		return markRekTest(n, m, goPoint.getPlayer(), ct);
	}

	public void unmarkAll() {
		int size = getBoardSize();
		int i, j;
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++) {
				GoPoint goPoint = getPoint(i, j);
				goPoint.setMarked(false);
			}
	}

	public int count(int i, int j) {
		int size = getBoardSize();
		unmarkAll();
		markGroup(i, j);
		int count = 0;
		for (i = 0; i < size; i++) {
			for (j = 0; j < size; j++) {
				GoPoint goPoint = getPoint(i, j);
				if (goPoint.isMarked()) {
					count++;
				}
			}
		}
		return count;
	}
}
