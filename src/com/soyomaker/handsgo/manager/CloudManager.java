package com.soyomaker.handsgo.manager;

import android.content.Context;

import com.sina.sae.cloudservice.api.CloudClient;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.soyomaker.handsgo.util.AppConstants;

public class CloudManager {

    private static CloudManager mInstance = new CloudManager();

    private boolean mInitSuccess;

    private CloudManager() {
    }

    public static CloudManager getInstance() {
        return mInstance;
    }

    private void init(Context context) {
        if (CloudClient.checkNetwork(context) && !mInitSuccess) {
            // 初始化CloudClient
            try {
                CloudClient.init(context, AppConstants.CLOUD_APP_NAME,
                        AppConstants.CLOUD_APP_ACCESS_KEY, AppConstants.CLOUD_APP_SECRET_KEY);
                mInitSuccess = true;
            } catch (CloudServiceException e) {
                mInitSuccess = false;
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO
     * 1，注册账号，2，登录账号，3，同步该账号已收藏的棋谱（获取，上传，删除）（每个账号分配1MB空间，使用积分换取更大空间100积分=1MB）
     */
}
