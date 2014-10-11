package com.soyomaker.handsgo.util;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.os.StatFs;

/**
 * 存储卡管理类
 * 
 * @author MaXingliang
 */
public class StorageUtil {

	public static final String EXTERNAL_STORAGE = Environment.getExternalStorageDirectory()
			.toString();

	/**
	 * SDCard主目录.
	 */
	private static final String DIR_HOME = EXTERNAL_STORAGE + "/handsgo";

	private static final String IMAGE_DIR = DIR_HOME + "/image";
	private static final String LOG_DIR = DIR_HOME + "/log";
	private static final String DB_DIR = DIR_HOME + "/db";

	public static final int DIR_TYPE_IMAGE = 1;
	public static final int DIR_TYPE_LOG = 2;
	public static final int DIR_TYPE_DB = 3;

	public static final int SD_NO_SPACE = 0;
	public static final int SD_NO_SD = 1;
	public static final int SD_OK = 2;

	/**
	 * 该文件用来在图库中屏蔽本应用的图片.
	 */
	private static final String NOMEDIA_FILE = DIR_HOME + "/.nomedia";

	private StorageUtil() {
	}

	public static String getDirByType(int type) {
		String dir = "/";
		String filePath = "";
		switch (type) {
		case DIR_TYPE_IMAGE: {
			filePath = IMAGE_DIR;
			break;
		}
		case DIR_TYPE_LOG: {
			filePath = LOG_DIR;
			break;
		}
		case DIR_TYPE_DB: {
			filePath = DB_DIR;
			break;
		}
		}

		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		if (file.exists()) {
			if (file.isDirectory()) {
				dir = file.getPath();
			}
		} else {
			// 文件没创建成功，可能是sd卡不存在，但是还是把路径返回
			dir = filePath;
		}
		return dir;
	}

	/**
	 * sdcard是否可读
	 * 
	 * @return
	 */
	public static boolean isStorageReadable() {
		return Environment.getExternalStorageDirectory().canRead();
	}

	/**
	 * sdcard是否可写
	 * 
	 * @return
	 */
	public static boolean isStorageWriteable() {
		return Environment.getExternalStorageDirectory().canWrite();
	}

	/**
	 * sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean hasExternalStorage() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断存储空间是否足够
	 * 
	 * @param needSize
	 * @return
	 */
	public static int checkExternalSpace(long needSize) {
		int flag = SD_OK;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			long restSize = availCount * blockSize;
			if (restSize > needSize) {
				flag = SD_OK;
			} else {
				flag = SD_NO_SPACE;
			}
		} else {
			flag = SD_NO_SD;
		}
		return flag;
	}

	/**
	 * 隐藏媒体文件（屏蔽其他软件扫描）.
	 */
	public static void hideMediaFile() {
		File file = new File(StorageUtil.NOMEDIA_FILE);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
