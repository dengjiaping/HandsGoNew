package com.soyomaker.handsgo.ui.fileexplorer;

public interface IBackPressedListener {

    /**
     * 处理back事件。
     * 
     * @return True: 表示已经处理; False: 没有处理，让基类处理。
     */
    boolean onBack();
}
