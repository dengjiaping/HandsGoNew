package com.soyomaker.handsgo.core.sgf;

import java.io.PrintWriter;

public class ActionMark extends ActionBase {

    public ActionMark(String arg) {
        super("M", arg);
    }

    public ActionMark() {
        super("M");
    }

    public void print(PrintWriter o) {
        o.println();
        o.print("MA");
        ListElement p = mArguments.getFirst();
        while (p != null) {
            o.print("[" + (String) (p.getContent()) + "]");
            p = p.getNext();
        }
    }
}
