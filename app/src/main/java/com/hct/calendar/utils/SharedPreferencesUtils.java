package com.hct.calendar.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.calendar.CalendarApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SharedPreferencesUtils {

    public static final String FILE_NAME = "shared_data";
    public static final String LOCATION = "location";
    public static final String UPDATE_TIME = "update_time";
    public static final String AUTO_UPDATE = "auto_update";
    public static final String TEMPERATURE_CHANGE = "temperature_change";
    public static final String TIME = "time";
    private static Context mContext = CalendarApplication.mContext;

    /**
     * 
     * 
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 
     * 
     * @param key
     * @param defaultObject
     * @return
     */

    public static Object get(String key, Object defaultObject) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }

    }

    /**
     * 
     * 
     * @param key
     */
    public static void remove(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 
     */
    public static void clear() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * 
     * 
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }

    /**
     * 
     * 
     * @return
     */
    public static Map<String, ?> getAll() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

    /**
     * 
     * @param tag
     * @param datalist
     */
    public static <T> void setDataList(String tag, List<T> datalist) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        String strJson = gson.toJson(datalist);
        editor.clear();
        editor.putString(tag, strJson);
        editor.commit();

    }

    /**
     * 
     * @param tag
     * @return
     */
    public static <T> List<T> getDataList(String tag) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        List<T> datalist = new ArrayList<T>();
        String strJson = sharedPreferences.getString(tag, null);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;

    }

    public static void put(String filename, String key, Object object) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 
     * 
     * @param key
     * @param defaultObject
     * @return
     */

    public static Object get(String filename, String key, Object defaultObject) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(
                filename, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        } else {
            return sharedPreferences.getString(key, null);
        }

    }

}
