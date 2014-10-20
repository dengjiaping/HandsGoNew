package com.soyomaker.handsgo.core.sgf;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class SGFTree {

	protected TreeNode mHistory;
	private final int mMaxbuffer = 4096;
	private char[] mBuffer = new char[mMaxbuffer];
	private int mBufferN;
	private static int mLastnl = 0;

	public SGFTree(Node n) {
		mHistory = new TreeNode(n);
		mHistory.getNode().setMain(true);
	}

	public static SGFTree empty(int size) {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
				("(;SZ[" + size + "])").getBytes())));
		try {
			Vector<SGFTree> sgfTrees = SGFTree.load(br);
			return sgfTrees.elementAt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Vector<SGFTree> load(BufferedReader in) throws IOException {
		Vector<SGFTree> v = new Vector<SGFTree>();
		boolean linestart = true;
		int c;
		reading: while (true) {
			SGFTree T = new SGFTree(new Node(1));
			while (true) {
				try {
					c = T.readChar(in);
				} catch (IOException ex) {
					break reading;
				}
				if (linestart && c == '(') {
					break;
				}
				if (c == '\n') {
					linestart = true;
				} else {
					linestart = false;
				}
			}
			T.readNodes(T.mHistory, in);
			v.addElement(T);
		}
		return v;
	}

	public TreeNode top() {
		return mHistory;
	}

	private char readNext(BufferedReader in) throws IOException {
		int c = readChar(in);
		while (c == '\n' || c == '\t' || c == ' ') {
			c = readChar(in);
		}
		return (char) c;
	}

	private char readChar(BufferedReader in) throws IOException {
		int c;
		while (true) {
			c = in.read();
			if (c == -1)
				throw new IOException();
			if (c == 13) {
				if (mLastnl == 10)
					mLastnl = 0;
				else {
					mLastnl = 13;
					return '\n';
				}
			} else if (c == 10) {
				if (mLastnl == 13)
					mLastnl = 0;
				else {
					mLastnl = 10;
					return '\n';
				}
			} else {
				mLastnl = 0;
				return (char) c;
			}
		}
	}

	private char readNode(TreeNode p, BufferedReader in) throws IOException {
		boolean sgf = true;
		char c = readNext(in);
		ActionBase a;
		Node n = new Node(((Node) p.getContent()).getNumber());
		String s;
		loop: while (true) {
			mBufferN = 0;
			while (true) {
				if (c >= 'A' && c <= 'Z') {
					store(c);
				} else if (c == '(' || c == ';' || c == ')') {
					break loop;
				} else if (c == '[') {
					break;
				} else if (c < 'a' || c > 'z') {
					throw new IOException();
				}
				c = readNext(in);
			}
			if (mBufferN == 0) {
				throw new IOException();
			}
			s = new String(mBuffer, 0, mBufferN);
			if (s.equals("L")) {
				a = new ActionLabel();
			} else if (s.equals("M")) {
				a = new ActionMark();
			} else {
				a = new ActionBase(s);
			}
			while (c == '[') {
				mBufferN = 0;
				while (true) {
					c = readChar(in);
					if (c == '\\') {
						c = readChar(in);
						if (sgf && c == '\n') {
							if (mBufferN > 1 && mBuffer[mBufferN - 1] == ' ') {
								continue;
							} else {
								c = ' ';
							}
						}
					} else if (c == ']') {
						break;
					}
					store(c);
				}
				c = readNext(in);
				String s1;
				if (mBufferN > 0) {
					s1 = new String(mBuffer, 0, mBufferN);
				} else {
					s1 = "";
				}
				if (!expand(a, s1)) {
					a.addArgument(s1);
				}
			}
			n.addAction(a);
			if (a.getType().equals("B") || a.getType().equals("W")) {
				n.setNumber(n.getNumber() + 1);
			}
		}
		n.setMain(p);
		TreeNode newp;
		if (((Node) p.getContent()).getActions() == null) {
			p.setContent(n);
		} else {
			p.addChild(newp = new TreeNode(n));
			n.setMain(p);
			p = newp;
			if (p.getParentPos() != null && p != p.getParentPos().getFirstChildPos()) {
				((Node) p.getContent()).setNumber(2);
			}
		}
		return c;
	}

	private boolean expand(ActionBase a, String s) {
		String t = a.getType();
		if (!(t.equals("MA") || t.equals("SQ") || t.equals("TR") || t.equals("CR")
				|| t.equals("AW") || t.equals("AB") || t.equals("AE") || t.equals("SL")))
			return false;
		if (s.length() != 5 || s.charAt(2) != ':')
			return false;
		String s0 = s.substring(0, 2), s1 = s.substring(3);
		int i0 = Field.i(s0), j0 = Field.j(s0);
		int i1 = Field.i(s1), j1 = Field.j(s1);
		if (i1 < i0 || j1 < j0)
			return false;
		int i, j;
		for (i = i0; i <= i1; i++) {
			for (j = j0; j <= j1; j++) {
				a.addArgument(Field.string(i, j));
			}
		}
		return true;
	}

	private void store(char c) {
		try {
			mBuffer[mBufferN] = c;
			mBufferN++;
		} catch (ArrayIndexOutOfBoundsException e) {
			int newLength = mBuffer.length + mMaxbuffer;
			char[] newBuffer = new char[newLength];
			System.arraycopy(mBuffer, 0, newBuffer, 0, mBuffer.length);
			mBuffer = newBuffer;
			mBuffer[mBufferN++] = c;
		}
	}

	private void readNodes(TreeNode p, BufferedReader in) throws IOException {
		char c = readNext(in);
		while (true) {
			if (c == ';') {
				c = readNode(p, in);
				if (p.hasChildren()) {
					p = p.getLastChildPos();
				}
				continue;
			} else if (c == '(') {
				readNodes(p, in);
			} else if (c == ')') {
				break;
			}
			c = readNext(in);
		}
	}
}
