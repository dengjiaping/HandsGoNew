package com.soyomaker.handsgo.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.soyomaker.handsgo.HandsGoApplication;

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

    public static String getDeviceId(Context context) {
        String imei = null;
        try {
            imei = ((TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE)))
                    .getDeviceId();
        } catch (Throwable e) {
        }
        return imei == null ? Secure.getString(context.getContentResolver(), Secure.ANDROID_ID)
                : imei;
    }
}
