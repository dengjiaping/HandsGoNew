/*
 * 
 */
package com.soyomaker.handsgonew.db;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.soyomaker.handsgonew.util.StorageUtil;

public class DBOpenHelper extends SdCardSQLiteOpenHelper {

	private static final String DB_NAME = "handsgo.db";

	public static final String HISTORY_TABLE_NAME = "HistoryChessManual";

	public static final String FAVORITE_TABLE_NAME = "FavoriteChessManual";

	public static final String GROUP_TABLE_NAME = "ChessManualGroup";

	private static final int VERSION = 1;

	public DBOpenHelper(Context context) {
		super(StorageUtil.getDirByType(StorageUtil.DIR_TYPE_DB), DB_NAME, null, VERSION);
	}

	@Override
	public SQLiteDatabase onCreateDatabase(String dbPath, String dbName, CursorFactory factory) {
		if (!StorageUtil.hasExternalStorage()) {
			return null;
		}
		SQLiteDatabase db = null;
		// 创建数据库
		File dir = new File(dbPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File dbf = new File(dbPath + dbName);
		if (dbf.exists()) {
			dbf.delete();
		}
		db = SQLiteDatabase.openOrCreateDatabase(dbf, null);
		return db;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// 创建收藏的棋谱表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + FAVORITE_TABLE_NAME
				+ "(_id integer primary key autoincrement," + "sgfUrl varchar(256),"
				+ "blackName varchar(256)," + "whiteName varchar(256)," + "matchName varchar(256),"
				+ "matchResult varchar(256)," + "matchTime varchar(256)," + "charset varchar(256),"
				+ "sgfContent text," + "type integer DEFAULT (0)," + "groupId integer DEFAULT (0))");
		// 创建浏览过的棋谱表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + HISTORY_TABLE_NAME
				+ "(_id integer primary key autoincrement," + "sgfUrl varchar(256),"
				+ "blackName varchar(256)," + "whiteName varchar(256)," + "matchName varchar(256),"
				+ "matchResult varchar(256)," + "matchTime varchar(256)," + "charset varchar(256),"
				+ "sgfContent text," + "type integer DEFAULT (0)," + "groupId integer DEFAULT (0))");
		// 创建收藏分组表
		db.execSQL("CREATE TABLE IF NOT EXISTS " + GROUP_TABLE_NAME
				+ "(_id integer primary key autoincrement," + "name varchar(256))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
