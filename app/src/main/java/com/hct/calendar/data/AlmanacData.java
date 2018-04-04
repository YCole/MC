package com.hct.calendar.data;

import java.io.IOException;

import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.hct.calendar.dao.AlmanacDao;
import com.hct.calendar.domain.AlmanacBean;
import com.hct.calendar.domain.AlmanacBody;
import com.hct.calendar.http.AlmanacApi;
import com.hct.calendar.http.NetAccess;

/**
 * get data from net or db
 * 
 * @author cat
 * 
 */
public class AlmanacData {

    @Nullable
    public static void getAlmanacBody(@NonNull final Context context,
            @NonNull final String day, @NonNull final Action<AlmanacBean> action) {
        // access db first
        new Thread(new Runnable() {
            @Override
            public void run() {
                AlmanacBean bean = AlmanacDao.query(context, day);
                LogUtils.e(bean);
                // if no data in db , access network
                boolean checkNetWork = AlmanacApi.checkNetWork(context);
                if (bean == null && checkNetWork) {
                    String json = "{}";
                    try {
                        json = NetAccess.accessNetWork(AlmanacApi
                                .urlTamplate(day));
                        long insert = AlmanacDao.replace(context,
                                new AlmanacBean(day, json));
                        LogUtils.e(insert);
                        bean = AlmanacDao.query(context, day);
                        if (bean == null) {
                            // throw new RuntimeException("Why can't I get
                            // almanac data?");
                            LogUtils.w(bean);
                        } else {
                            final AlmanacBody body = new Gson().fromJson(
                                    bean.dateJson, AlmanacBody.class);
                            LogUtils.e(body);
                        }
                    } catch (IOException e) {
                        // throw new RuntimeException(e);
                        LogUtils.w(e);
                    }

                }
                if (action != null) {
                    action.next(bean);
                }
            }
        }).start();
    }
}
