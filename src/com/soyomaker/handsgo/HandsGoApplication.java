package com.soyomaker.handsgo;

import android.app.Application;
import android.content.Context;

import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;
import com.soyomaker.handsgo.util.CrashHandler;
import com.soyomaker.handsgo.util.StorageUtil;
import com.umeng.analytics.MobclickAgent;
import com.weibo.image.SinaImageLoader;

public class HandsGoApplication extends Application {

	private static Context sContext;
	private static ChessManual sChessManual;

	@Override
	public void onCreate() {
		super.onCreate();
		HandsGoApplication.sContext = this;

		// 记录崩溃异常
		CrashHandler ueHandler = new CrashHandler(sContext);
		Thread.setDefaultUncaughtExceptionHandler(ueHandler);

		// 初始化数据库
		DBService.init(this);
		// 创建默认分组
		if (DBService.getGroupCaches().isEmpty()) {
			Group defaultGroup = new Group();
			defaultGroup.setName("默认分组");
			DBService.saveGroup(defaultGroup);
		}

		// 初始化图片加载器
		SinaImageLoader.init(sContext, StorageUtil.getDirByType(StorageUtil.DIR_TYPE_IMAGE));

		// 友盟统计初始配置
		MobclickAgent.openActivityDurationTrack(false);
	}

	public static Context getAppContext() {
		return sContext;
	}

	public static ChessManual getChessManual() {
		return sChessManual;
	}

	public static void setChessManual(ChessManual chessManual) {
		sChessManual = chessManual;
	}
}
