package com.ddinc.ftpnow.host.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.ddinc.ftpnow.host.database.UserInfoEntry.TABLE_NAME;


/**
 * Auxiliary category of the database. Used to create, delete, update, and reset databases.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "UserInfo.db";
    private static final int DB_VERSION = 1;
    private ServerData serverData;

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
            + UserInfoEntry._ID + " INTEGER PRIMARY KEY,"
            + UserInfoEntry.NAME + " TEXT NOT NULL,"
            + UserInfoEntry.PASSWORD + " TEXT NOT NULL,"
            + UserInfoEntry.WRITE_PERMISSION + " TEXT NOT NULL,"
            + UserInfoEntry.USER_ENABLED + " TEXT NOT NULL,"
            + UserInfoEntry.UPLOAD_RATE + " INTEGER NOT NULL,"
            + UserInfoEntry.DOWNLOAD_RATE + " INTEGER NOT NULL,"
            + UserInfoEntry.DEFAULT_PATH + " TEXT NOT NULL)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DbHelper(Context context, ServerData serverData) {
        super(context, DB_NAME, null, DB_VERSION);
        this.serverData = serverData;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        serverData.putDefaultUserInfoToDb(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    //Reset the database
    public void cleanDb(SQLiteDatabase db) {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }
}
