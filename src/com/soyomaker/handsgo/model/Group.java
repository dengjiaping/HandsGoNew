package com.soyomaker.handsgo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 棋谱分组.
 * 
 * @author like
 * 
 */
public class Group implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_GROUP = 1;

	private int mId = -1;

	private String mName;

	private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public String getName() {
		return mName;
	}

	public void setName(String mName) {
		this.mName = mName;
	}

	public ArrayList<ChessManual> getChessManuals() {
		return mChessManuals;
	}

	public void setChessManuals(ArrayList<ChessManual> mChessManuals) {
		this.mChessManuals = mChessManuals;
	}

	public String toString() {
		return mName + "[" + mChessManuals.size() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + mId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (mId != other.mId)
			return false;
		return true;
	}
}
