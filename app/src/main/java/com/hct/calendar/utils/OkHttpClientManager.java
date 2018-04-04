package com.hct.calendar.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.calendar.CalendarApplication;
import com.android.calendar.weather.CurrentCondition;
import com.android.calendar.weather.CurrentConditionCallBack;
import com.android.calendar.weather.LocationText;
import com.android.calendar.weather.LocationTextCallBack;
import com.android.calendar.weather.NetInterceptor;
import com.android.calendar.weather.ResultCallback;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

public class OkHttpClientManager {

    private static OkHttpClientManager mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mDelivery;
    private Gson mGson;

    private static final String TAG = "OkHttpClientManager";

    CacheControl cacheControl = new CacheControl.Builder()
    // .noCache()
    // .noStore()
            .maxAge(10, TimeUnit.MINUTES).maxStale(1, TimeUnit.DAYS).build();

    private OkHttpClientManager() {
        File cacheFile = new File(CalendarApplication.getContext()
                .getCacheDir(), "weatherCache");
        mOkHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new NetInterceptor())
                .cache(new Cache(cacheFile, 10 * 1024 * 1024))
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public static OkHttpClientManager getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpClientManager.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpClientManager();
                }
            }
        }
        return mInstance;
    }

    public void requestCurrentCondition(String actionUrl,
            final CurrentConditionCallBack callBack) {
        final Request request = new Request.Builder().url(actionUrl)
                .header("Cache-Control", cacheControl.toString()).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    try {
                        Gson gson = new Gson();
                        List<CurrentCondition> list = gson.fromJson(string,
                                new TypeToken<List<CurrentCondition>>() {
                                }.getType());
                        if (list != null && list.size() > 0) {
                            successCurrentConditionCallBack(list, callBack);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("", "error");
                }
            }
        });
    }

    public void requestLocationText(String actionUrl,
            final LocationTextCallBack callBack) {
        final Request request = new Request.Builder().url(actionUrl)
                .header("Cache-Control", cacheControl.toString()).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    try {
                        Gson gson = new Gson();
                        List<LocationText> list = gson.fromJson(string,
                                new TypeToken<List<LocationText>>() {
                                }.getType());
                        if (list != null && list.size() > 0) {
                            successLocationTextCallBack(list, callBack);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        });
    }

    public void requestLocationText(String actionUrl,
            final LocationTextCallBack callBack, int i) {
        final Request request = new Request.Builder().url(actionUrl)
                .header("Cache-Control", cacheControl.toString()).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    try {
                        Gson gson = new Gson();
                        List<LocationText> list = gson.fromJson(string,
                                new TypeToken<List<LocationText>>() {
                                }.getType());
                        successLocationTextCallBack(list, callBack);
                    } catch (Exception e) {
                        System.out.println("fail");
                    }
                } else {
                    System.out.println("fail");
                }
            }
        });
    }

    private void successCurrentConditionCallBack(
            final List<CurrentCondition> result,
            final CurrentConditionCallBack callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onLocationReqSuccess(result);
                }
            }
        });
    }

    private void successLocationTextCallBack(final List<LocationText> result,
            final LocationTextCallBack callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onLocationReqSuccess(result);
                }
            }
        });
    }

    public static <T> T parseJsonToBean(String json, Class<T> cls) {
        Gson gson = new Gson();
        T t = null;
        try {
            t = gson.fromJson(json, cls);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return t;
    }

    public static <T> List<T> parseJsonToList(String json) {
        Gson gson = new Gson();
        List<T> list = gson.fromJson(json, new TypeToken<List<T>>() {
        }.getType());
        return list;
    }

    public static <T> List<T> parseJsonToList2(String json, Type type) {
        Gson gson = new Gson();
        List<T> list = gson.fromJson(json, type);
        return list;
    }

    public static <T> List<T> changeGsonToList(String gsonString) {
        Gson gson = new Gson();
        List<T> list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
        return list;
    }

    /**
     * 
     * 
     * @param json
     * @return
     */
    public static <T> LinkedTreeMap<String, T> parseJsonToMap(String json,
            Class<T> cls) {
        Gson gson = new Gson();
        Type type = new TypeToken<LinkedTreeMap<String, T>>() {
        }.getType();
        LinkedTreeMap<String, T> map = null;
        try {
            map = gson.fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return map;
    }

    public <T> void requestGetByAsyn(String actionUrl,
            final ResultCallback<T> callBack, final Class<T> tClass) {
        final Request request = new Request.Builder().url(actionUrl)
                .header("Cache-Control", cacheControl.toString()).build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                failedCallBack("fail", callBack);
                Log.e(TAG, e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if (response.isSuccessful()) {
                    // LogUtils.e(response.headers());
                    // LogUtils.e(response);
                    boolean successful = response.isSuccessful();
                    long len = response.body().contentLength();
                    // LogUtils.e("response ----->"
                    // +" , len = "+len+" , success = "+successful);
                    String string = response.body().string();
                    // LogUtils.e("response ----->"
                    // +string+" , len = "+len+" , success = "+successful);
                    try {
                        T t = parseJsonToBean(string, tClass);
                        if (t != null) {
                            successCallBack(t, callBack);
                        }
                    } catch (Exception e) {
                        System.out.println("fail");
                    }

                } else {
                    failedCallBack("fail", callBack);
                }
            }
        });

    }

    private <T> void failedCallBack(final String errorMsg,
            final ResultCallback<T> callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqFailed(errorMsg);
                }
            }
        });
    }

    private <T> void successCallBack(final T result,
            final ResultCallback<T> callBack) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onReqSuccess(result);
                }
            }
        });
    }

}
