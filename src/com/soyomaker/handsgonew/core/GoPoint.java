package com.soyomaker.handsgonew.core;

import com.soyomaker.handsgonew.core.sgf.TreeNode;

/**
 * 棋子模型类
 * 
 * @author like
 * 
 */
public class GoPoint {

	public final static int PLAYER_EMPTY = 0;
	public final static int PLAYER_BLACK = 1;
	public final static int PLAYER_WHITE = -1;

	public final static int STYLE_GENERAL = 0;
	public final static int STYLE_HIGHLIGHT = 1;

	public final static int NONE = -1;
	public final static int TRIANGLE = -2;
	public final static int SQUARE = -3;
	public final static int CIRCLE = -4;
	public final static int CROSS = -5;

	private int mPlayer = PLAYER_EMPTY;
	private int mStyle = STYLE_GENERAL;
	private int mNumber = NONE;// 棋子上的手数
	private int mMark = NONE;
	private int mLetter = NONE;
	private String mLabel;
	private TreeNode mTreeNode;

	public TreeNode getTreeNode() {
		return mTreeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.mTreeNode = treeNode;
	}

	public int getPlayer() {
		return mPlayer;
	}

	public void setPlayer(int player) {
		this.mPlayer = player;
	}

	public int getStyle() {
		return mStyle;
	}

	public void setStyle(int style) {
		this.mStyle = style;
	}

	public int getNumber() {
		return mNumber;
	}

	public void setNumber(int number) {
		this.mNumber = number;
	}

	public int getMark() {
		return mMark;
	}

	public void setMark(int mark) {
		this.mMark = mark;
	}

	public int getLetter() {
		return mLetter;
	}

	public void setLetter(int letter) {
		this.mLetter = letter;
	}

	public String getLabel() {
		return mLabel;
	}

	public void setLabel(String label) {
		this.mLabel = label;
	}
}
