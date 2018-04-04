package com.hct.calendar.provider;

import java.util.ArrayList;

import android.annotation.Nullable;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.hct.calendar.dao.AlmanacColumns;
import com.hct.calendar.dao.InnerDbHelper;

public class InnerProvider extends ContentProvider {

    public static final int TABLE_ALMANAC_DIR = 0;
    public static final int TABLE_HOLIDAY_DIR = 2;
    private static final String AUTHROTY = "com.android.calendar.provider";
    private static final String ALMANAC_TABLE = AlmanacColumns.TABLE_NAME;
    private static final String SCHEMA = "content://";

    public static final String ALMANAC_URI = SCHEMA + AUTHROTY + "/"
            + ALMANAC_TABLE;
    private static UriMatcher matcher;
    private InnerDbHelper dbHelper;

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHROTY, AlmanacColumns.TABLE_NAME, TABLE_ALMANAC_DIR);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new InnerDbHelper(getContext());
        return false;
    }

    @Override
    @Nullable
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        switch (matcher.match(uri)) {
        case TABLE_ALMANAC_DIR:
            cursor = db.query(ALMANAC_TABLE, projection, selection,
                    selectionArgs, null, null, sortOrder);
            break;
        case TABLE_HOLIDAY_DIR:
            break;
        default:
            break;
        }
        return cursor;
    }

    @Override
    @Nullable
    public String getType(Uri uri) {
        switch (matcher.match(uri)) {
        case TABLE_ALMANAC_DIR:
            return "vnd.android.cursor.dir/vnd." + AUTHROTY + "."
                    + AlmanacColumns.TABLE_NAME;
        case TABLE_HOLIDAY_DIR:
            break; // TODO: holiday table !
        default:
            break;
        }
        return null;

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri parse = null;
        switch (matcher.match(uri)) {
        case TABLE_ALMANAC_DIR:

            long insert = db.insert(ALMANAC_TABLE, null, values);
            parse = Uri.parse(SCHEMA + AUTHROTY + "/" + ALMANAC_TABLE + "/"
                    + insert);
            break;
        case TABLE_HOLIDAY_DIR:
            break;
        default:
            break;
        }
        return parse;
    }

    /**
     * from: http://blog.csdn.net/mylzc/article/details/6794400
     */
    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int deleteRows = 0;
        switch (matcher.match(uri)) {
        case TABLE_ALMANAC_DIR:
            deleteRows = db.delete(ALMANAC_TABLE, selection, selectionArgs);
            break;
        case TABLE_HOLIDAY_DIR:
            break;
        default:
            break;
        }

        return deleteRows;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int updateRows = 0;
        switch (matcher.match(uri)) {
        case TABLE_ALMANAC_DIR:
            updateRows = db.update(ALMANAC_TABLE, values, selection,
                    selectionArgs);
            break;
        case TABLE_HOLIDAY_DIR:
            break;
        default:
            break;
        }

        return updateRows;
    }

}
