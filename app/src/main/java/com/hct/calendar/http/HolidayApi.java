package com.hct.calendar.http;

import java.io.IOException;

import android.annotation.NonNull;

import com.google.gson.Gson;
import com.hct.calendar.data.Action;
import com.hct.calendar.domain.Holiday;

public class HolidayApi {

    public static void getHolidayJson(@NonNull final Action<String> action) {
        final String holidayUrl = String.format(
                "http://zhwnlapi.etouch.cn/Ecalender/openapi/holidays?key=%s",
                "c40fbdbd2f6d413195f6045ce03e49dc");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultJson = NetAccess.accessNetWork(holidayUrl);
                    action.next(resultJson);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void getHolidayList(@NonNull final Action<Holiday> action) {
        final String holidayUrl = String.format(
                "http://zhwnlapi.etouch.cn/Ecalender/openapi/holidays?key=%s",
                "c40fbdbd2f6d413195f6045ce03e49dc");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultJson = NetAccess.accessNetWork(holidayUrl);
                    Holiday bean = new Gson().fromJson(resultJson,
                            Holiday.class);
                    action.next(bean);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
