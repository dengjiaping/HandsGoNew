/*
 * 
 */
package com.soyomaker.handsgonew.db;

import java.io.File;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.soyomaker.handsgonew.util.StorageUtil;

/**
 * 数据库建立在SD卡上.
 * 
 * @author Tsimle
 */
public abstract class SdCardSQLiteOpenHelper {

	private static final String TAG = SQLiteOpenHelper.class.getSimpleName();

	private final String mFilePath;

	private final String mDbPath;

	private final String mDbName;

	private final CursorFactory mFactory;

	private final int mNewVersion;

	private SQLiteDatabase mDatabase = null;

	private boolean mIsInitializing = false;

	public SdCardSQLiteOpenHelper(String dbPath, String dbName, CursorFactory factory, int version) {
		if (version < 1)
			throw new IllegalArgumentException("Version must be >= 1, was " + version);

		mDbPath = dbPath;
		mDbName = dbName;
		mFilePath = mDbPath + mDbName;
		mFactory = factory;
		mNewVersion = version;
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		if (!StorageUtil.hasExternalStorage()) {
			return null;
		}

		boolean success = false;
		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;

			File dbf = new File(mFilePath);
			if (!dbf.exists()) {
				db = onCreateDatabase(mDbPath, mDbName, mFactory);
			} else {
				db = SQLiteDatabase.openOrCreateDatabase(mFilePath, mFactory);
			}
			if (db == null) {
				return null;
			}
			int version = db.getVersion();
			if (version != mNewVersion) {
				db.beginTransaction();
				try {
					if (version == 0) {
						onCreate(db);
					} else {
						onUpgrade(db, version, mNewVersion);
					}
					db.setVersion(mNewVersion);
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}

			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try {
						mDatabase.close();
					} catch (Exception e) {
					}
				}
				mDatabase = db;
			} else {
				if (db != null)
					db.close();
			}
		}
	}

	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mDatabase != null && mDatabase.isOpen()) {
			return mDatabase; // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getReadableDatabase called recursively");
		}

		if (!StorageUtil.hasExternalStorage()) {
			return null;
		}

		try {
			return getWritableDatabase();
		} catch (SQLiteException e) {
			if (mFilePath == null)
				throw e; // Can't open a temp database read-only!
			Log.e(TAG, "Couldn't open " + mFilePath + " for writing (will try read-only):", e);
		}

		SQLiteDatabase db = null;
		try {
			mIsInitializing = true;
			db = SQLiteDatabase.openDatabase(mFilePath, mFactory, SQLiteDatabase.OPEN_READONLY);
			if (db.getVersion() != mNewVersion) {
				throw new SQLiteException("Can't upgrade read-only database from version "
						+ db.getVersion() + " to " + mNewVersion + ": " + mFilePath);
			}

			onOpen(db);
			Log.w(TAG, "Opened " + mFilePath + " in read-only mode");
			mDatabase = db;
			return mDatabase;
		} finally {
			mIsInitializing = false;
			if (db != null && db != mDatabase)
				db.close();
		}
	}

	/**
	 * Close any open database object.
	 */
	public synchronized void close() {
		if (mIsInitializing)
			throw new IllegalStateException("Closed during initialization");

		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	/**
	 * Called when the database is created for the first time. This is where the
	 * creation of tables and the initial population of the tables should
	 * happen.
	 * 
	 * @param db
	 *            The database.
	 */
	public abstract void onCreate(SQLiteDatabase db);

	/**
	 * Called when the database needs to be upgraded. The implementation should
	 * use this method to drop tables, add tables, or do anything else it needs
	 * to upgrade to the new schema version.
	 * 
	 * <p>
	 * The SQLite ALTER TABLE documentation can be found <a
	 * href="http://sqlite.org/lang_altertable.html">here</a>. If you add new
	 * columns you can use ALTER TABLE to insert them into a live table. If you
	 * rename or remove columns you can use ALTER TABLE to rename the old table,
	 * then create the new table and then populate the new table with the
	 * contents of the old table.
	 * 
	 * @param db
	 *            The database.
	 * @param oldVersion
	 *            The old database version.
	 * @param newVersion
	 *            The new database version.
	 */
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

	/**
	 * Called when the database has been opened. Override method should check
	 * 
	 * @param db
	 *            The database. {@link SQLiteDatabase#isReadOnly} before
	 *            updating the database.
	 */
	public void onOpen(SQLiteDatabase db) {
	}

	/**
	 * Called when you want to create database yourself.
	 * 
	 * @param dbPath
	 *            the db path
	 * @param dbName
	 *            the db name
	 * @param factory
	 *            the factory
	 * @return the sQ lite database
	 */
	public SQLiteDatabase onCreateDatabase(String dbPath, String dbName, CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(dbPath + dbName, mFactory);
	}
}
