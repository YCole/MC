package com.android.calendar.smsservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackListDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "BlackList.db";
    private static final String TBL_BLACKLIST = "BlackList";
    private static final String TBL_CHECKLIST = "CheckList";
    private static final String CREATE_TBL = " create table "
            + " BlackList(_id integer primary key autoincrement, number TEXT)";
    private static final String CREATE_TBL2 = " create table "
            + " CheckList(_id integer primary key autoincrement, number TEXT, times INTEGER)";

    private SQLiteDatabase db;

    BlackListDBHelper(Context c) {
        super(c, DB_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TBL);
        db.execSQL(CREATE_TBL2);
    }

    public void insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_BLACKLIST, null, values);
    }

    public void insert_chklist(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_CHECKLIST, null, values);
    }

    public Cursor query() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TBL_BLACKLIST, null, null, null, null, null, null);
        return c;
    }

    public Cursor query(String number) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TBL_BLACKLIST, null, "number = '" + number + "'",
                null, null, null, null);
        return c;
    }

    public Cursor query_chklist(String number) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TBL_CHECKLIST, null, "number = '" + number + "'",
                null, null, null, null);
        return c;
    }

    public void del(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL_BLACKLIST, "_id=?", new String[] { String.valueOf(id) });
    }

    public void del_chklist(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL_CHECKLIST, "_id=?", new String[] { String.valueOf(id) });
    }

    public void del_chklist(String number) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TBL_CHECKLIST, "number= '" + number + "'", null);
    }

    public void update_chklist(ContentValues values, int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(TBL_CHECKLIST, values, "_id=" + id, null);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}