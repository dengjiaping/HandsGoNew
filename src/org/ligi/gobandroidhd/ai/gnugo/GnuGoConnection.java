package org.ligi.gobandroidhd.ai.gnugo;

public class GnuGoConnection {

    public native static void initGTP(float pMemory);

    public native static String playGTP(String pInput);
}
