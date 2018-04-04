package com.hct.calendar.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.apkfuns.logutils.LogUtils;

public class InnerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database
    // version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "innerCalendar.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ALMANAC =
    // date + dateJson
    "CREATE TABLE IF NOT EXISTS " + AlmanacColumns.TABLE_NAME + " ("
            + AlmanacColumns.COLUMN_DATE + TEXT_TYPE + PRIMARY_KEY + COMMA_SEP
            + AlmanacColumns.COLUMN_DATE_JSON + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ALMABAC = "DROP TABLE IF EXISTS "
            + AlmanacColumns.TABLE_NAME;

    public InnerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogUtils.e("create almanac table SQL = " + SQL_CREATE_ALMANAC);
        db.execSQL(SQL_CREATE_ALMANAC);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy
        // is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ALMABAC);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
