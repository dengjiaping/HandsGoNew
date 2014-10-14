package com.soyomaker.handsgo.util;

import com.soyomaker.handsgo.HandsGoApplication;

import android.content.pm.PackageManager.NameNotFoundException;

public class AppUtil {

    public static boolean hasInstalled(String packageName) {
        boolean hasInstalled = true;
        try {
            HandsGoApplication.getAppContext().getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            hasInstalled = false;
        }
        return hasInstalled;
    }
}
