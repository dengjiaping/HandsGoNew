package com.soyomaker.handsgo.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.sina.sae.cloudservice.api.CloudChangePassword;
import com.sina.sae.cloudservice.api.CloudClient;
import com.sina.sae.cloudservice.api.CloudComment;
import com.sina.sae.cloudservice.api.CloudGetComments;
import com.sina.sae.cloudservice.api.CloudLogin;
import com.sina.sae.cloudservice.api.CloudRegister;
import com.sina.sae.cloudservice.api.CommonParams;
import com.sina.sae.cloudservice.exception.CloudServiceException;
import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.model.Comment;
import com.soyomaker.handsgo.model.User;
import com.soyomaker.handsgo.util.AppConstants;
import com.soyomaker.handsgo.util.AppPrefrence;

public class CloudManager {

	private static CloudManager mInstance = new CloudManager();

	private boolean mInitSuccess;
	private User mLoginUser;
	private Map<String, ArrayList<Comment>> mCommentMap = new HashMap<String, ArrayList<Comment>>();
	private Map<String, Boolean> mCommentRefreshingMap = new HashMap<String, Boolean>();
	private Map<String, String> mCommonParams = new HashMap<String, String>();

	private CloudManager() {
		mCommonParams.put("version",
				HandsGoApplication.getAppContext().getString(R.string.app_version));
		mCommonParams.put("platform", "android");
		CommonParams.setCommonParams(mCommonParams);
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

	public boolean isRefreshingComment(String sgfUrl) {
		Boolean refreshing = mCommentRefreshingMap.get(sgfUrl);
		return refreshing == null ? false : refreshing;
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
	 * 修改密码
	 * 
	 * @param context
	 * @param name
	 * @param oldpassword
	 * @param newpassword
	 * @return
	 */
	public int changePassword(Context context, String name, String oldpassword, String newpassword) {
		init(context);

		int code = -1;
		try {
			code = CloudChangePassword.changePassword(name, oldpassword, newpassword);
		} catch (CloudServiceException e) {
			code = e.getCode();
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * 请求评论列表
	 * 
	 * @param context
	 * @param sgfUrl
	 * @return
	 */
	public ArrayList<Comment> requestComments(Context context, String sgfUrl) {
		mCommentRefreshingMap.put(sgfUrl, true);
		init(context);

		List<Map<String, String>> lists = null;
		try {
			lists = CloudGetComments.getComments(sgfUrl);
		} catch (CloudServiceException e) {
			e.printStackTrace();
		}

		if (lists != null && !lists.isEmpty()) {
			ArrayList<Comment> comments = new ArrayList<Comment>();
			for (Map<String, String> map : lists) {
				Comment comment = new Comment();
				comment.setComment(map.get("comment"));
				comment.setCommentSgf(map.get("commentsgf"));
				comment.setUserName(map.get("username"));
				comment.setInsertTime(map.get("inserttime"));
				String userId = map.get("userid");
				if (!TextUtils.isEmpty(userId) && TextUtils.isDigitsOnly(userId)) {
					comment.setUserId(Integer.valueOf(userId));
				}
				String id = map.get("commentid");
				if (!TextUtils.isEmpty(id) && TextUtils.isDigitsOnly(id)) {
					comment.setId(Integer.valueOf(id));
				}
				comments.add(comment);
			}
			ArrayList<Comment> oldComments = mCommentMap.get(sgfUrl);
			if (oldComments != null) {
				oldComments.clear();
				oldComments.addAll(comments);
			} else {
				mCommentMap.put(sgfUrl, comments);
			}
		}
		mCommentRefreshingMap.put(sgfUrl, false);
		return getComments(sgfUrl);
	}

	/**
	 * 发送评论
	 * 
	 * @param context
	 * @param comment
	 * @return
	 */
	public boolean sendComment(Context context, Comment comment) {
		init(context);

		int code = -1;
		try {
			code = CloudComment.sendComment("" + comment.getUserId(), comment.getUserName(),
					comment.getCommentSgf(), comment.getComment());
		} catch (CloudServiceException e) {
			code = e.getCode();
			e.printStackTrace();
		}

		return code == 0;
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
	public int register(Context context, String name, String password, String deviceId,
			String email, String gender) {
		init(context);

		int code = -1;
		try {
			code = CloudRegister.register(name, password, deviceId, email, gender);
		} catch (CloudServiceException e) {
			code = e.getCode();
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * 退出登录
	 */
	public void signin(Context context) {
		mLoginUser = null;

		AppPrefrence.saveUserName(context, "");
		AppPrefrence.saveUserPassword(context, "");

		mCommonParams.remove("userid");
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
			AppPrefrence.saveUserName(context, name);
			AppPrefrence.saveUserPassword(context, password);

			mCommonParams.put("userid", userid);

			return mLoginUser;
		} else {
			return null;
		}
	}
}
