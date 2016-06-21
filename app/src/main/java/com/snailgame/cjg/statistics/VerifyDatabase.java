package com.snailgame.cjg.statistics;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class VerifyDatabase extends SQLiteOpenHelper implements BaseColumns {

    public static final String TABLE_NAME = "verify";
    private static final String DB_NAME = "verify.db";
    private static final int VERSION = 2;
    public static final String COL_URL = "url";
    public static final String COL_DATA = "data";

    private static VerifyDatabase db;

    public VerifyDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " +TABLE_NAME
                + " (" +
                _ID + " integer primary key autoincrement," +
                COL_DATA + " varchar(100)," +
                COL_URL + " varchar(4000)" +
                ")");
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        String sql = "drop table if exists " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    public static VerifyDatabase getInstance(Context context) {
        if (db == null)
            db = new VerifyDatabase(context);
        return db;
    }
}
