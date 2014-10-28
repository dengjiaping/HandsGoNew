package com.soyomaker.handsgo.core.sgf;

/**
 * 链表类
 * 
 * @author like
 * 
 */
public class ListClass {

    private ListElement mFirstElement, mLastElement;

    public ListClass() {
    }

    /**
     * 追加
     * 
     * @param l
     */
    public void append(ListElement l) {
        if (mLastElement == null) {
            init(l);
        } else {
            mLastElement.setNext(l);
            l.setPrevious(mLastElement);
            mLastElement = l;
            l.setNext(null);
            l.setList(this);
        }
    }

    /**
     * 前置
     * 
     * @param l
     */
    public void prepend(ListElement l) {
        if (mFirstElement == null) {
            init(l);
        } else {
            mFirstElement.setPrevious(l);
            l.setNext(mFirstElement);
            mFirstElement = l;
            l.setPrevious(null);
            l.setList(this);
        }
    }

    /**
     * 插入
     * 
     * @param l
     * @param after
     */
    public void insert(ListElement l, ListElement after) {
        if (after == mLastElement)
            append(l);
        else if (after == null)
            prepend(l);
        else {
            after.getNext().setPrevious(l);
            l.setNext(after.getNext());
            after.setNext(l);
            l.setPrevious(after);
            l.setList(this);
        }
    }

    /**
     * 初始化
     * 
     * @param l
     */
    public void init(ListElement l) {
        mLastElement = mFirstElement = l;
        l.setPrevious(null);
        l.setNext(null);
        l.setList(this);
    }

    /**
     * 删除
     * 
     * @param l
     */
    public void remove(ListElement l) {
        if (mFirstElement == l) {
            mFirstElement = l.getNext();
            if (mFirstElement != null) {
                mFirstElement.setPrevious(null);
            } else {
                mLastElement = null;
            }
        } else if (mLastElement == l) {
            mLastElement = l.getPrevious();
            if (mLastElement != null) {
                mLastElement.setNext(null);
            } else {
                mFirstElement = null;
            }
        } else {
            l.getPrevious().setNext(l.getNext());
            l.getNext().setPrevious(l.getPrevious());
        }
        l.setNext(null);
        l.setPrevious(null);
        l.setList(null);
    }

    public void removeAll() {
        mFirstElement = null;
        mLastElement = null;
    }

    public void removeAfter(ListElement e) {
        e.setNext(null);
        mLastElement = e;
    }

    public ListElement getFirst() {
        return mFirstElement;
    }

    public ListElement getLast() {
        return mLastElement;
    }

    @Override
    public String toString() {
        ListElement e = mFirstElement;
        String s = "";
        while (e != null) {
            s = s + e.getContent().toString() + ", ";
            e = e.getNext();
        }
        return s;
    }
}
