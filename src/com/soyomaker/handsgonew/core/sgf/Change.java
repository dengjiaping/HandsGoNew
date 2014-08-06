package com.soyomaker.handsgonew.core.sgf;

public class Change {

	public int x, y, color;
	public int number;

	public Change(int x, int y, int color, int number) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.number = number;
	}

	public Change(int x, int y, int color) {
		this(x, y, color, 0);
	}
}
