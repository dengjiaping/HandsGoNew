package com.soyomaker.handsgonew.core.sgf;

/**
 * 链表元素
 * 
 * @author like
 * 
 */
public class ListElement {

	private ListElement mNextElement, mPreviousElement;
	private Object mContent;
	private ListClass mListClass;

	public ListElement(Object content) {
		mContent = content;
	}

	public Object getContent() {
		return mContent;
	}

	public ListElement getNext() {
		return mNextElement;
	}

	public ListElement getPrevious() {
		return mPreviousElement;
	}

	public ListClass getList() {
		return mListClass;
	}

	public void setContent(Object o) {
		mContent = o;
	}

	public void setNext(ListElement o) {
		mNextElement = o;
	}

	public void setPrevious(ListElement o) {
		mPreviousElement = o;
	}

	public void setList(ListClass l) {
		mListClass = l;
	}
}
