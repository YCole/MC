package com.hct.calendar.data;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import android.annotation.NonNull;
import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;
import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hct.calendar.domain.LocationBody;
import com.hct.calendar.domain.WeatherBody;
import com.hct.calendar.domain.WeatherItem;
import com.hct.calendar.http.NetAccess;
import com.hct.calendar.http.WeatherApi;
import com.hct.calendar.lbs.LocationUtils;
import com.hct.calendar.utils.LocaleUtils;

/**
 * get weather data from net !
 * 
 * @author cat
 * 
 */
public class WeatherData {

    public static void getWeatherFromNet(@NonNull final Context context,
            @NonNull final Action<WeatherItem> action) {
        if (LocationUtils.isOPen(context)) {
            LocationUtils.getLocation((Activity) context,
                    new Action<Location>() {

                        @Override
                        public void next(Location local) {
                            double latitude = local.getLatitude();
                            double longitude = local.getLongitude();
                            String lan = String.format(Locale.getDefault(),
                                    "%.1f", latitude);
                            String lon = String.format(Locale.getDefault(),
                                    "%.1f", longitude);
                            LogUtils.e(lan + ":" + lon);
                            final String locationUrl = WeatherApi
                                    .getLocationUrl(context, lan, lon,
                                            LocaleUtils.getLang(context));
                            LogUtils.e("locationURL = " + locationUrl);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String json = NetAccess
                                                .accessNetWork(locationUrl);
                                        LocationBody bean = new Gson()
                                                .fromJson(json,
                                                        LocationBody.class);
                                        LogUtils.e(bean);
                                        String weatherUrl = WeatherApi
                                                .generateUrl(
                                                        context,
                                                        "currentconditions",
                                                        "",
                                                        bean.Key,
                                                        LocaleUtils
                                                                .getLang(context));
                                        LogUtils.e("weatherUrl = " + weatherUrl);
                                        String weatherJson = NetAccess
                                                .accessNetWork(weatherUrl);
                                        LogUtils.e(weatherJson);
                                        WeatherItem item = getItemFromJson(
                                                context, weatherJson,
                                                bean.LocalizedName);
                                        LogUtils.e(item);
                                        action.next(item);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            }).start();
                        }

                    });
        }
    }

    @NonNull
    private static WeatherItem getItemFromJson(Context context,
            String weatherJson, String local) {
        Type type = new TypeToken<List<WeatherBody>>() {
        }.getType();
        List<WeatherBody> weatherBeanList = new Gson().fromJson(weatherJson,
                type);
        if (weatherBeanList != null && weatherBeanList.size() > 0) {
            WeatherBody wb = weatherBeanList.get(0);
            LogUtils.e(wb);
            WeatherBody.LocalSourceBean ls = wb.LocalSource;
            WeatherItem item = new WeatherItem();
            item.localizedName = local;
            int code, weatherStr, weatherIcon, max, min;
            max = (int) wb.TemperatureSummary.Past12HourRange.Maximum.Metric.Value;
            min = (int) wb.TemperatureSummary.Past12HourRange.Minimum.Metric.Value;
            item.temperatureMax = String.valueOf(max);
            item.temperatureMin = String.valueOf(min);
            if (ls != null) {
                code = Integer.parseInt(ls.WeatherCode.trim());
                weatherStr = CalendarApplication.weatherText.get(code,
                        R.string.sunny);
                weatherIcon = CalendarApplication.weatherIconList.get(code,
                        R.drawable.sunny);
            } else {
                code = wb.WeatherIcon;
                weatherStr = CalendarApplication.accuWeatherText.get(code,
                        R.string.sunny);
                weatherIcon = CalendarApplication.accuWeatherIcon.get(code,
                        R.drawable.sunny);
            }

            item.weatherIcon = weatherIcon;
            item.weatherInfo = context.getString(weatherStr);
            LogUtils.e("weatherItem:");
            LogUtils.e(item);
            return item;
        } else {
            LogUtils.e(weatherBeanList);
            throw new RuntimeException("weatherBeanList is invilid...");
        }
    }

}
