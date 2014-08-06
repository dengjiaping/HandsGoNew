package com.soyomaker.handsgonew.core.sgf;

public class Node {

	private ListClass mActions;
	private int mNextExpectedNumber;
	private boolean mIsMain;
	private ListClass mChanges;
	public int mPw, mPb;

	public Node(int n) {
		mActions = new ListClass();
		mNextExpectedNumber = n;
		mIsMain = false;
		mChanges = new ListClass();
		mPw = mPb = 0;
	}

	public void addAction(ActionBase a) {
		mActions.append(new ListElement(a));
	}

	public void expandAction(ActionBase a) {
		ListElement p = find(a.getType());
		if (p == null) {
			addAction(a);
		} else {
			ActionBase pa = (ActionBase) p.getContent();
			pa.addArgument(a.getArgument());
		}
	}

	public void toggleAction(ActionBase a) {
		ListElement p = find(a.getType());
		if (p == null) {
			addAction(a);
		} else {
			ActionBase pa = (ActionBase) p.getContent();
			pa.toggleArgument(a.getArgument());
		}
	}

	private ListElement find(String s) {
		ListElement p = mActions.getFirst();
		while (p != null) {
			ActionBase a = (ActionBase) p.getContent();
			if (a.getType().equals(s)) {
				return p;
			}
			p = p.getNext();
		}
		return null;
	}

	public boolean contains(String s, String argument) {
		ListElement p = find(s);
		if (p == null) {
			return false;
		}
		ActionBase a = (ActionBase) p.getContent();
		return a.contains(argument);
	}

	public boolean contains(String s) {
		return find(s) != null;
	}

	public void prependAction(ActionBase a) {
		mActions.prepend(new ListElement(a));
	}

	public void insertAction(ActionBase a, ListElement p) {
		mActions.insert(new ListElement(a), p);
	}

	public void removeAction(ListElement la) {
		mActions.remove(la);
	}

	public void setAction(String type, String arg, boolean front) {
		ListElement l = mActions.getFirst();
		while (l != null) {
			ActionBase a = (ActionBase) l.getContent();
			if (a.getType().equals(type)) {
				if (arg.equals("")) {
					mActions.remove(l);
					return;
				} else {
					ListElement la = a.getArguments();
					if (la != null) {
						la.setContent(arg);
					} else {
						a.addArgument(arg);
					}
				}
				return;
			}
			l = l.getNext();
		}
		if (front) {
			prependAction(new ActionBase(type, arg));
		} else {
			addAction(new ActionBase(type, arg));
		}
	}

	public void setAction(String type, String arg) {
		setAction(type, arg, false);
	}

	public String getAction(String type) {
		ListElement l = mActions.getFirst();
		while (l != null) {
			ActionBase a = (ActionBase) l.getContent();
			if (a.getType().equals(type)) {
				ListElement la = a.getArguments();
				if (la != null) {
					return (String) la.getContent();
				} else {
					return "";
				}
			}
			l = l.getNext();
		}
		return "";
	}

	public void removeActions() {
		mActions = new ListClass();
	}

	public void addChange(Change c) {
		mChanges.append(new ListElement(c));
	}

	public void clearChanges() {
		mChanges.removeAll();
	}

	public void setMain(boolean m) {
		mIsMain = m;
	}

	public void setMain(Tree p) {
		mIsMain = false;
		try {
			if (((Node) p.getContent()).isMain()) {
				mIsMain = (this == ((Node) p.getFirstChild().getContent()));
			} else if (p.getParent() == null) {
				mIsMain = true;
			}
		} catch (Exception e) {
		}
	}

	public void setNumber(int n) {
		mNextExpectedNumber = n;
	}

	public void copyAction(Node n, String action) {
		if (n.contains(action)) {
			expandAction(new ActionBase(action, n.getAction(action)));
		}
	}

	public ListElement getActions() {
		return mActions.getFirst();
	}

	public ListElement getLastAction() {
		return mActions.getLast();
	}

	public ListElement getChanges() {
		return mChanges.getFirst();
	}

	public ListElement getLastChange() {
		return mChanges.getLast();
	}

	public int getNumber() {
		return mNextExpectedNumber;
	}

	public boolean isMain() {
		return mIsMain;
	}
}
