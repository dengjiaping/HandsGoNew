package com.soyomaker.handsgonew.core.sgf;

import java.io.PrintWriter;

public class ActionLabel extends ActionBase {

	public ActionLabel(String arg) {
		super("L", arg);
	}

	public ActionLabel() {
		super("L");
	}

	public void print(PrintWriter o) {
		o.println();
		o.print("LB");
		char[] c = new char[1];
		int i = 0;
		ListElement p = mArguments.getFirst();
		while (p != null) {
			c[0] = (char) ('a' + i);
			o.print("[" + (String) (p.getContent()) + ":" + new String(c) + "]");
			i++;
			p = p.getNext();
		}
	}
}
