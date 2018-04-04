package com.hct.calendar.dao;

import java.util.ArrayList;
import java.util.List;

import android.annotation.NonNull;
import android.annotation.WorkerThread;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.hct.calendar.domain.AlmanacBean;
import com.hct.calendar.provider.InnerProvider;

@WorkerThread
public class AlmanacDao {

    private static Uri almanacUri = Uri.parse(InnerProvider.ALMANAC_URI);

    /**
     * 
     * @param context
     * @param day
     *            format = "yyyy-MM-dd" 2017-5-11 , 2016-12-23
     * @return one row data
     */
    public static AlmanacBean query(@NonNull Context context,
            @NonNull String day) {
        String[] projection = { AlmanacColumns.COLUMN_DATE,
                AlmanacColumns.COLUMN_DATE_JSON, };

        // // Filter results WHERE "title" = 'My Title'
        String selection = AlmanacColumns.COLUMN_DATE + " = ?";
        String[] selectionArgs = { day };

        if (null == context) {
            return null;
        }
        if (null == context.getContentResolver()) {
            return null;
        }
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(almanacUri, projection,
                    selection, selectionArgs, null);
        } catch (Exception e) {

        }
        ArrayList<AlmanacBean> list = new ArrayList<AlmanacBean>();
        try {
            while (cursor != null && cursor.moveToNext()) {
                AlmanacBean bean = new AlmanacBean();
                bean.eachDay = cursor.getString(cursor
                        .getColumnIndexOrThrow(AlmanacColumns.COLUMN_DATE));
                bean.dateJson = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(AlmanacColumns.COLUMN_DATE_JSON));
                list.add(bean);
            }
            if (list.size() > 1) {
                throw new RuntimeException(
                        "The current id no corresponding data, "
                                + "or a corresponding number of  data! ");
            } else if (list.size() == 0) {
                return null; // no current data !
            }
            return list.get(0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }

        }

    }

    public static List<AlmanacBean> query(@NonNull Context context) {
        String[] projection = { AlmanacColumns.COLUMN_DATE,
                AlmanacColumns.COLUMN_DATE_JSON, };

        Cursor cursor = context.getContentResolver().query(almanacUri,
                projection, null, null, null);
        ArrayList<AlmanacBean> list = new ArrayList<AlmanacBean>();
        try {
            while (cursor != null && cursor.moveToNext()) {
                AlmanacBean bean = new AlmanacBean();
                bean.eachDay = cursor.getString(cursor
                        .getColumnIndexOrThrow(AlmanacColumns.COLUMN_DATE));
                bean.dateJson = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(AlmanacColumns.COLUMN_DATE_JSON));
                list.add(bean);
            }
            return list;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int delete(@NonNull Context context, @NonNull String day) {
        String where = AlmanacColumns.COLUMN_DATE + " = ?";
        String[] selectionArgs = { day };
        int delete = context.getContentResolver().delete(almanacUri, where,
                selectionArgs);
        return delete;
    }

    public static int delete(@NonNull Context context) {
        int delete = context.getContentResolver()
                .delete(almanacUri, null, null);
        return delete;
    }

    public static long insert(@NonNull Context context,
            @NonNull AlmanacBean bean) {
        InnerDbHelper dbHelper = new InnerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(AlmanacColumns.COLUMN_DATE, bean.eachDay);
            values.put(AlmanacColumns.COLUMN_DATE_JSON, bean.dateJson);
            long insert = db.insert(AlmanacColumns.TABLE_NAME, null, values);
            return insert;
        } finally {
            db.close();
            dbHelper.close();
        }
    }

    public static long replace(@NonNull Context context,
            @NonNull AlmanacBean bean) {
        InnerDbHelper dbHelper = new InnerDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(AlmanacColumns.COLUMN_DATE, bean.eachDay);
            values.put(AlmanacColumns.COLUMN_DATE_JSON, bean.dateJson);
            long replaceRows = db.replace(AlmanacColumns.TABLE_NAME, null,
                    values);
            return replaceRows;
        } finally {
            db.close();
            dbHelper.close();
        }
    }
}
