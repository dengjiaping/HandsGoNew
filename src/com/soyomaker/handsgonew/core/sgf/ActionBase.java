package com.soyomaker.handsgonew.core.sgf;

public class ActionBase {

	protected String mType;
	protected ListClass mArguments;

	public ActionBase(String type) {
		mType = type;
		mArguments = new ListClass();
	}

	public ActionBase(String type, String arg) {
		mType = type;
		mArguments = new ListClass();
		addArgument(arg);
	}

	public void addArgument(String s) {
		mArguments.append(new ListElement(s));
	}

	public void toggleArgument(String s) {
		ListElement ap = mArguments.getFirst();
		while (ap != null) {
			String t = (String) ap.getContent();
			if (t.equals(s)) {
				mArguments.remove(ap);
				return;
			}
			ap = ap.getNext();
		}
		mArguments.append(new ListElement(s));
	}

	public boolean contains(String s) {
		ListElement ap = mArguments.getFirst();
		while (ap != null) {
			String t = (String) ap.getContent();
			if (t.equals(s)) {
				return true;
			}
			ap = ap.getNext();
		}
		return false;
	}

	/**
	 * 是否动作含有文本信息
	 */
	public boolean isRelevant() {
		if (mType.equals("GN") || mType.equals("AP") || mType.equals("FF") || mType.equals("GM")
				|| mType.equals("N") || mType.equals("SZ") || mType.equals("PB")
				|| mType.equals("BR") || mType.equals("PW") || mType.equals("WR")
				|| mType.equals("HA") || mType.equals("KM") || mType.equals("RE")
				|| mType.equals("DT") || mType.equals("TM") || mType.equals("US")
				|| mType.equals("CP") || mType.equals("BL") || mType.equals("WL")
				|| mType.equals("C")) {
			return false;
		} else {
			return true;
		}
	}

	public void setType(String s) {
		mType = s;
	}

	public String getType() {
		return mType;
	}

	public ListElement getArguments() {
		return mArguments.getFirst();
	}

	public String getArgument() {
		if (getArguments() == null) {
			return "";
		} else {
			return (String) getArguments().getContent();
		}
	}
}
