package com.soyomaker.handsgonew.core;

public class DefaultBoardModel implements IBoardModel {

	private IGridModel mGridModel;

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
		if (lastPoint != null) {
			lastPoint.setStyle(GoPoint.STYLE_HIGHLIGHT);
		}
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

		int player = point.getPlayer();
		if ((player != GoPoint.PLAYER_BLACK) && (player != GoPoint.PLAYER_WHITE)) {
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
		resetHighlight();
	}
}
