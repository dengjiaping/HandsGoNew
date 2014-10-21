package com.soyomaker.handsgo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.sina.sae.cloudservice.api.CloudClient;
import com.sina.sae.cloudservice.api.CloudLogin;
import com.sina.sae.cloudservice.api.CloudRegister;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.soyomaker.handsgo.model.Comment;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.util.AppConstants;

public class CloudManager {

    private static CloudManager mInstance = new CloudManager();

    private boolean mInitSuccess;
    private User mLoginUser;
    private Map<String, ArrayList<Comment>> mCommentMap = new HashMap<String, ArrayList<Comment>>();

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

    public User getLoginUser() {
        return mLoginUser;
    }

    public boolean hasLogin() {
        return mLoginUser != null;
    }

    public ArrayList<Comment> getComments(String sgfUrl) {
        ArrayList<Comment> comments = mCommentMap.get(sgfUrl);
        if (comments == null) {
            comments = new ArrayList<Comment>();
            mCommentMap.put(sgfUrl, comments);
        }
        return comments;
    }

    /**
     * 注册账号（需在异步线程调用）
     * 
     * @param context
     * @param name
     * @param password
     * @param deviceId
     * @param email
     * @param gender
     * @return
     */
    public boolean register(Context context, String name, String password, String deviceId,
            String email, String gender) {
        init(context);

        int rows = -1;
        try {
            rows = CloudRegister.register(name, password, deviceId, email, gender);
        } catch (CloudServiceException e) {
            e.printStackTrace();
        }

        return rows > 0;
    }

    /**
     * 登录账号（需在异步线程调用）
     * 
     * @param context
     * @param name
     * @param password
     * @return
     */
    public User login(Context context, String name, String password) {
        init(context);

        List<Map<String, String>> lists = null;
        try {
            lists = CloudLogin.login(name, password);
        } catch (CloudServiceException e) {
            e.printStackTrace();
        }

        if (lists != null && !lists.isEmpty()) {
            Map<String, String> map = lists.get(0);
            mLoginUser = new User();
            mLoginUser.setEmail(map.get("email"));
            String userid = map.get("userid");
            if (!TextUtils.isEmpty(userid) && TextUtils.isDigitsOnly(userid)) {
                mLoginUser.setId(Integer.valueOf(userid));
            }
            String level = map.get("level");
            if (!TextUtils.isEmpty(level) && TextUtils.isDigitsOnly(level)) {
                mLoginUser.setLevel(Integer.valueOf(level));
            }
            mLoginUser.setName(map.get("name"));
            mLoginUser.setPassword(map.get("password"));
            String score = map.get("score");
            if (!TextUtils.isEmpty(score) && TextUtils.isDigitsOnly(score)) {
                mLoginUser.setScore(Integer.valueOf(score));
            }
            String space = map.get("space");
            if (!TextUtils.isEmpty(space) && TextUtils.isDigitsOnly(space)) {
                mLoginUser.setSpace(Integer.valueOf(space));
            }
            String gender = map.get("gender");
            if (!TextUtils.isEmpty(gender) && TextUtils.isDigitsOnly(gender)) {
                mLoginUser.setGender(Integer.valueOf(gender));
            }
            return mLoginUser;
        } else {
            return null;
        }
    }
}
