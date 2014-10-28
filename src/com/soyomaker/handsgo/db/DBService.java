/*
 * 
 */
package com.soyomaker.handsgo.db;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.soyomaker.handsgo.model.ChessManual;
import com.soyomaker.handsgo.model.Group;

public class DBService {

    private static DBOpenHelper mDbOpenHelper;

    private static ArrayList<ChessManual> mFavoriteChessManualCaches = new ArrayList<ChessManual>();

    private static ArrayList<ChessManual> mHistoryChessManualCaches = new ArrayList<ChessManual>();

    private static ArrayList<Group> mGroupsCaches = new ArrayList<Group>();

    private static boolean shouldUpdateFavoriteCache = true;

    private static boolean shouldUpdateHistoryCache = true;

    private static boolean shouldUpdateGroupCache = true;

    private static ReentrantReadWriteLock sReadWriteLock = new ReentrantReadWriteLock();

    /**
     * 获取可读的数据库.
     * 
     * @return the readable database
     */
    public static SQLiteDatabase getReadableDatabase() {
        sReadWriteLock.readLock().lock();
        return mDbOpenHelper.getReadableDatabase();
    }

    /**
     * 获取可写的数据库.
     * 
     * @return the writable database
     */
    public static SQLiteDatabase getWritableDatabase() {
        sReadWriteLock.writeLock().lock();
        return mDbOpenHelper.getWritableDatabase();
    }

    /**
     * 关闭数据库.
     * 
     * @param db
     */
    public static void closeDatabase(SQLiteDatabase db) {
        if (sReadWriteLock.isWriteLocked()) {
            if (db != null && db.isOpen()) {
                db.close();
            }
            sReadWriteLock.writeLock().unlock();
        } else {
            if (sReadWriteLock.getReadLockCount() <= 1) {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
            sReadWriteLock.readLock().unlock();
        }
    }

    public synchronized static void init(Context context) {
        mDbOpenHelper = new DBOpenHelper(context);
    }

    public static ArrayList<ChessManual> getHistoryChessManualCaches() {
        if (shouldUpdateHistoryCache) {
            mHistoryChessManualCaches = getAllHistoryChessManual();
            shouldUpdateHistoryCache = false;
        }
        return mHistoryChessManualCaches;
    }

    public static ArrayList<ChessManual> getFavoriteChessManualCaches() {
        if (shouldUpdateFavoriteCache) {
            mFavoriteChessManualCaches = getAllFavoriteChessManual();
            shouldUpdateFavoriteCache = false;
        }
        return mFavoriteChessManualCaches;
    }

    public static ArrayList<Group> getGroupCaches() {
        if (shouldUpdateGroupCache) {
            mGroupsCaches = getAllGroup();
            shouldUpdateGroupCache = false;
        }
        return mGroupsCaches;
    }

    public static boolean isHistoryChessManual(ChessManual chessManual) {
        if (chessManual == null || chessManual.getSgfUrl() == null) {
            return false;
        }
        ArrayList<ChessManual> chessManuals = getHistoryChessManualCaches();
        for (ChessManual cManual : chessManuals) {
            if (chessManual.equals(cManual)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFavoriteChessManual(ChessManual chessManual) {
        if (chessManual == null || chessManual.getSgfUrl() == null) {
            return false;
        }
        ArrayList<ChessManual> chessManuals = getFavoriteChessManualCaches();
        for (ChessManual cManual : chessManuals) {
            if (chessManual.equals(cManual)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Group> getAllGroup() {
        ArrayList<Group> groups = new ArrayList<Group>();
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.GROUP_TABLE_NAME;
        if (db == null) {
            return groups;
        }
        try {
            db.beginTransaction();
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Group group = new Group();
                    group.setId(cursor.getInt(0));
                    group.setName(cursor.getString(1));
                    group.setChessManuals(getFavoriteChessManualsByGroupId(group.getId()));
                    groups.add(group);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
            closeDatabase(db);
        }
        return groups;
    }

    public static ArrayList<ChessManual> getAllHistoryChessManual() {
        ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME;
        if (db == null) {
            return chessManuals;
        }
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ChessManual chessManual = new ChessManual();
                    chessManual.setId(cursor.getInt(0));
                    chessManual.setSgfUrl(cursor.getString(1));
                    chessManual.setBlackName(cursor.getString(2));
                    chessManual.setWhiteName(cursor.getString(3));
                    chessManual.setMatchName(cursor.getString(4));
                    chessManual.setMatchResult(cursor.getString(5));
                    chessManual.setMatchTime(cursor.getString(6));
                    chessManual.setCharset(cursor.getString(7));
                    chessManual.setSgfContent(cursor.getString(8));
                    chessManual.setType(cursor.getInt(9));
                    chessManual.setGroupId(cursor.getInt(10));
                    chessManuals.add(chessManual);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(db);
        }
        return chessManuals;
    }

    public static ArrayList<ChessManual> getAllFavoriteChessManual() {
        ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME;
        if (db == null) {
            return chessManuals;
        }
        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ChessManual chessManual = new ChessManual();
                    chessManual.setId(cursor.getInt(0));
                    chessManual.setSgfUrl(cursor.getString(1));
                    chessManual.setBlackName(cursor.getString(2));
                    chessManual.setWhiteName(cursor.getString(3));
                    chessManual.setMatchName(cursor.getString(4));
                    chessManual.setMatchResult(cursor.getString(5));
                    chessManual.setMatchTime(cursor.getString(6));
                    chessManual.setCharset(cursor.getString(7));
                    chessManual.setSgfContent(cursor.getString(8));
                    chessManual.setType(cursor.getInt(9));
                    chessManual.setGroupId(cursor.getInt(10));
                    chessManuals.add(chessManual);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(db);
        }
        return chessManuals;
    }

    public static ArrayList<ChessManual> getFavoriteChessManualsByGroupId(int groudId) {
        ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME + " where groupId = ?";
        if (db == null) {
            return chessManuals;
        }
        try {
            cursor = db.rawQuery(sql, new String[] { "" + groudId });
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ChessManual chessManual = new ChessManual();
                    chessManual.setId(cursor.getInt(0));
                    chessManual.setSgfUrl(cursor.getString(1));
                    chessManual.setBlackName(cursor.getString(2));
                    chessManual.setWhiteName(cursor.getString(3));
                    chessManual.setMatchName(cursor.getString(4));
                    chessManual.setMatchResult(cursor.getString(5));
                    chessManual.setMatchTime(cursor.getString(6));
                    chessManual.setCharset(cursor.getString(7));
                    chessManual.setSgfContent(cursor.getString(8));
                    chessManual.setType(cursor.getInt(9));
                    chessManual.setGroupId(cursor.getInt(10));
                    chessManuals.add(chessManual);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(db);
        }
        return chessManuals;
    }

    public static ChessManual getHistoryChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return null;
        }
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME + " where sgfUrl = ?";
        if (db == null) {
            return null;
        }

        ChessManual newChessManual = null;
        try {
            cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
            if (cursor != null && cursor.moveToFirst()) {
                newChessManual = new ChessManual();
                newChessManual.setId(cursor.getInt(0));
                newChessManual.setSgfUrl(cursor.getString(1));
                newChessManual.setBlackName(cursor.getString(2));
                newChessManual.setWhiteName(cursor.getString(3));
                newChessManual.setMatchName(cursor.getString(4));
                newChessManual.setMatchResult(cursor.getString(5));
                newChessManual.setMatchTime(cursor.getString(6));
                newChessManual.setCharset(cursor.getString(7));
                newChessManual.setSgfContent(cursor.getString(8));
                newChessManual.setType(cursor.getInt(9));
                newChessManual.setGroupId(cursor.getInt(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(db);
        }
        return newChessManual;
    }

    public static ChessManual getFavoriteChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return null;
        }
        Cursor cursor = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME + " where sgfUrl = ?";
        if (db == null) {
            return null;
        }

        ChessManual newChessManual = null;
        try {
            cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
            if (cursor != null && cursor.moveToFirst()) {
                newChessManual = new ChessManual();
                newChessManual.setId(cursor.getInt(0));
                newChessManual.setSgfUrl(cursor.getString(1));
                newChessManual.setBlackName(cursor.getString(2));
                newChessManual.setWhiteName(cursor.getString(3));
                newChessManual.setMatchName(cursor.getString(4));
                newChessManual.setMatchResult(cursor.getString(5));
                newChessManual.setMatchTime(cursor.getString(6));
                newChessManual.setCharset(cursor.getString(7));
                newChessManual.setSgfContent(cursor.getString(8));
                newChessManual.setType(cursor.getInt(9));
                newChessManual.setGroupId(cursor.getInt(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            closeDatabase(db);
        }
        return newChessManual;
    }

    public static boolean saveGroup(Group group) {
        if (TextUtils.isEmpty(group.getName())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }
        try {
            db.beginTransaction();
            if (group.getId() < 0) {
                db.execSQL("insert into " + DBOpenHelper.GROUP_TABLE_NAME + " (name) values (?)",
                        new Object[] { group.getName() });
                Cursor cursor = db.rawQuery("select LAST_INSERT_ROWID()", null);
                if (cursor != null && cursor.moveToFirst()) {
                    group.setId(cursor.getInt(0));
                }
                if (cursor != null) {
                    cursor.close();
                }
            } else {
                db.execSQL("update " + DBOpenHelper.GROUP_TABLE_NAME
                        + " set name = ? where _id = ?",
                        new Object[] { group.getName(), group.getId() });
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateGroupCache = true;
        return true;
    }

    public static boolean saveHistoryChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBOpenHelper.HISTORY_TABLE_NAME + " where sgfUrl = ?";
            db.beginTransaction();
            cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
            if (cursor != null && cursor.getCount() > 0) {
                db.execSQL(
                        "update "
                                + DBOpenHelper.HISTORY_TABLE_NAME
                                + " set blackName = ?,whiteName = ?,matchName = ?,matchResult = ?,"
                                + "matchTime = ?,charset = ?,sgfContent = ?,type = ?,groupId = ? where sgfUrl = ?",
                        new Object[] { chessManual.getBlackName(), chessManual.getWhiteName(),
                                chessManual.getMatchName(), chessManual.getMatchResult(),
                                chessManual.getMatchTime(), chessManual.getCharset(),
                                chessManual.getSgfContent(), chessManual.getType(),
                                chessManual.getGroupId(), chessManual.getSgfUrl() });
            } else {
                db.execSQL(
                        "insert into "
                                + DBOpenHelper.HISTORY_TABLE_NAME
                                + " (sgfUrl,blackName,whiteName,matchName,matchResult,"
                                + "matchTime,charset,sgfContent,type,groupId) values (?,?,?,?,?,?,?,?,?,?)",
                        new Object[] { chessManual.getSgfUrl(), chessManual.getBlackName(),
                                chessManual.getWhiteName(), chessManual.getMatchName(),
                                chessManual.getMatchResult(), chessManual.getMatchTime(),
                                chessManual.getCharset(), chessManual.getSgfContent(),
                                chessManual.getType(), chessManual.getGroupId() });
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateHistoryCache = true;
        return true;
    }

    public static boolean saveFavoriteChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }
        Cursor cursor = null;
        try {
            String sql = "select * from " + DBOpenHelper.FAVORITE_TABLE_NAME + " where sgfUrl = ?";
            db.beginTransaction();
            cursor = db.rawQuery(sql, new String[] { chessManual.getSgfUrl() });
            if (cursor != null && cursor.getCount() > 0) {
                db.execSQL(
                        "update "
                                + DBOpenHelper.FAVORITE_TABLE_NAME
                                + " set blackName = ?,whiteName = ?,matchName = ?,matchResult = ?,"
                                + "matchTime = ?,charset = ?,sgfContent = ?,type = ?,groupId = ? where sgfUrl = ?",
                        new Object[] { chessManual.getBlackName(), chessManual.getWhiteName(),
                                chessManual.getMatchName(), chessManual.getMatchResult(),
                                chessManual.getMatchTime(), chessManual.getCharset(),
                                chessManual.getSgfContent(), chessManual.getType(),
                                chessManual.getGroupId(), chessManual.getSgfUrl() });
            } else {
                db.execSQL(
                        "insert into "
                                + DBOpenHelper.FAVORITE_TABLE_NAME
                                + " (sgfUrl,blackName,whiteName,matchName,matchResult,"
                                + "matchTime,charset,sgfContent,type,groupId) values (?,?,?,?,?,?,?,?,?,?)",
                        new Object[] { chessManual.getSgfUrl(), chessManual.getBlackName(),
                                chessManual.getWhiteName(), chessManual.getMatchName(),
                                chessManual.getMatchResult(), chessManual.getMatchTime(),
                                chessManual.getCharset(), chessManual.getSgfContent(),
                                chessManual.getType(), chessManual.getGroupId() });
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateGroupCache = true;
        shouldUpdateFavoriteCache = true;
        return true;
    }

    public static boolean deleteGroup(Group group) {
        if (TextUtils.isEmpty(group.getName())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }

        try {
            db.beginTransaction();
            deleteFavoriteChessManuals(db, group.getChessManuals());
            db.execSQL("delete from " + DBOpenHelper.GROUP_TABLE_NAME + " where _id = ?",
                    new String[] { "" + group.getId() });
            group.setId(-1);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateGroupCache = true;
        return true;
    }

    public static boolean deleteHistoryChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }

        try {
            db.beginTransaction();
            db.execSQL("delete from " + DBOpenHelper.HISTORY_TABLE_NAME + " where sgfUrl = ?",
                    new String[] { chessManual.getSgfUrl() });
            chessManual.setId(-1);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateHistoryCache = true;
        return true;
    }

    private static void deleteHistoryChessManuals(SQLiteDatabase db,
            ArrayList<ChessManual> chessManuals) {
        for (ChessManual chessManual : chessManuals) {
            db.execSQL("delete from " + DBOpenHelper.HISTORY_TABLE_NAME + " where sgfUrl = ?",
                    new String[] { chessManual.getSgfUrl() });
            chessManual.setId(-1);
        }
        shouldUpdateHistoryCache = true;
    }

    public static boolean deleteFavoriteChessManual(ChessManual chessManual) {
        if (TextUtils.isEmpty(chessManual.getSgfUrl())) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        if (db == null) {
            return false;
        }

        try {
            db.beginTransaction();
            db.execSQL("delete from " + DBOpenHelper.FAVORITE_TABLE_NAME + " where sgfUrl = ?",
                    new String[] { chessManual.getSgfUrl() });
            chessManual.setId(-1);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            closeDatabase(db);
        }
        shouldUpdateGroupCache = true;
        shouldUpdateFavoriteCache = true;
        return true;
    }

    private static void deleteFavoriteChessManuals(SQLiteDatabase db,
            ArrayList<ChessManual> chessManuals) {
        for (ChessManual chessManual : chessManuals) {
            db.execSQL("delete from " + DBOpenHelper.FAVORITE_TABLE_NAME + " where sgfUrl = ?",
                    new String[] { chessManual.getSgfUrl() });
            chessManual.setId(-1);
        }
        shouldUpdateFavoriteCache = true;
    }
}
