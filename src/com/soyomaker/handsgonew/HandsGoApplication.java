package com.soyomaker.handsgonew;

import android.app.Application;
import android.content.Context;

import com.soyomaker.handsgonew.db.DBService;
import com.soyomaker.handsgonew.model.Group;
import com.soyomaker.handsgonew.util.CrashHandler;
import com.soyomaker.handsgonew.util.StorageUtil;
import com.weibo.image.SinaImageLoader;

public class HandsGoApplication extends Application {

	private static Context sContext;

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
	}

	public static Context getAppContext() {
		return sContext;
	}
}
