package com.soyomaker.handsgonew.core.sgf;

public class Tree {

	private ListClass mChildren;
	private Object mContent;
	private ListElement mListElement;
	private Tree mParent;

	public Tree(Object o) {
		mContent = o;
		mChildren = new ListClass();
	}

	public void addChild(Tree t) {
		ListElement p = new ListElement(t);
		mChildren.append(p);
		t.mListElement = p;
		t.mParent = this;
	}

	public void insertChild(Tree t) {
		if (!hasChildren()) {
			addChild(t);
			return;
		}
		// give t my children
		t.mChildren = mChildren;
		// make t my only child
		mChildren = new ListClass();
		ListElement p = new ListElement(t);
		mChildren.append(p);
		t.mListElement = p;
		t.mParent = this;
		// fix the parents of all grandchildren
		ListElement le = t.mChildren.getFirst();
		while (le != null) {
			Tree h = (Tree) (le.getContent());
			h.mParent = t;
			le = le.getNext();
		}
	}

	public void remove(Tree t) {
		if (t.getParent() != this) {
			return;
		}
		mChildren.remove(t.mListElement);
	}

	public void removeAll() {
		mChildren.removeAll();
	}

	public boolean hasChildren() {
		return mChildren.getFirst() != null;
	}

	public Tree getFirstChild() {
		return (Tree) mChildren.getFirst().getContent();
	}

	public Tree getLastChild() {
		return (Tree) mChildren.getLast().getContent();
	}

	public Tree getParent() {
		return mParent;
	}

	public ListClass getChildren() {
		return mChildren;
	}

	public Object getContent() {
		return mContent;
	}

	public void setContent(Object o) {
		mContent = o;
	}

	public ListElement getFirstElement() {
		return mChildren.getFirst();
	}

	public ListElement getLastElement() {
		return mChildren.getLast();
	}

	public ListElement getListElement() {
		return mListElement;
	}
}
