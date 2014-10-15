package com.soyomaker.handsgo.core.sgf;

public class Field {

    final static int az = 'z' - 'a';

    /** return a string containing the coordinates in SGF */
    public static String string(int i, int j) {
        char[] a = new char[2];
        if (i >= az)
            a[0] = (char) ('A' + i - az - 1);
        else
            a[0] = (char) ('a' + i);
        if (j >= az)
            a[1] = (char) ('A' + j - az - 1);
        else
            a[1] = (char) ('a' + j);
        return new String(a);
    }

    /** return a string containing the coordinates in SGF */
    public static String coordinate(int i, int j, int s) {
        if (s > 25) {
            return (i + 1) + "," + (s - j);
        } else {
            if (i >= 8)
                i++;
            return "" + (char) ('A' + i) + (s - j);
        }
    }

    /** get the first coordinate from the SGF string */
    public static int i(String s) {
        if (s.length() < 2)
            return -1;
        char c = s.charAt(0);
        if (c < 'a')
            return c - 'A' + az + 1;
        return c - 'a';
    }

    /** get the second coordinate from the SGF string */
    public static int j(String s) {
        if (s.length() < 2)
            return -1;
        char c = s.charAt(1);
        if (c < 'a')
            return c - 'A' + az + 1;
        return c - 'a';
    }
}
