package com.soyomaker.handsgo.core.sgf;

public class TreeNode extends Tree {

    public TreeNode(int number) {
        super(new Node(number));
    }

    public TreeNode(Node n) {
        super(n);
    }

    public Node getNode() {
        return ((Node) getContent());
    }

    public void setAction(String type, String s, boolean flag) {
        getNode().setAction(type, s, flag);
    }

    public void setAction(String type, String s) {
        getNode().setAction(type, s);
    }

    public void addAction(ActionBase a) {
        getNode().addAction(a);
    }

    public String getAction(String type) {
        return getNode().getAction(type);
    }

    public boolean isMain() {
        return getNode().isMain();
    }

    public boolean isLastMain() {
        return !hasChildren() && isMain();
    }

    public void main(boolean flag) {
        getNode().setMain(flag);
    }

    public TreeNode getParentPos() {
        return (TreeNode) getParent();
    }

    public TreeNode getFirstChildPos() {
        return (TreeNode) getFirstChild();
    }

    public TreeNode getLastChildPos() {
        return (TreeNode) getLastChild();
    }
}
