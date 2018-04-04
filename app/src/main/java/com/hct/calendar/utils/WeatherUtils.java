package com.hct.calendar.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;

public class WeatherUtils {

    public static String getUTCTime() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String date = DateFormat.format("yyyyMMddkkmm", cal).toString();
        return date.substring(0, date.length() - 1);
    }

    public static void main(String[] args) {
        System.out.println("" + getUTCTime());
    }

    public static String StringtoSign(String apiKey, String apiContentType,
            String requestDate) {
        StringBuffer sf = new StringBuffer();
        sf.append(apiKey);
        sf.append("\r\n");
        sf.append(apiContentType);
        sf.append("\r\n");
        sf.append(requestDate);
        return sf.toString();
    }

    public static String generate_url(String content_type,
            String content_type1, String locationkey, String key,
            String secret, String language) {
        String request_date = "";
        if (NetworkUtils.isNetworkAvailable()) {
            request_date = getUTCTime();
        } else {
            request_date = (String) SharedPreferencesUtils.get("getUTCTime",
                    getUTCTime());
        }
        SharedPreferencesUtils.put("getUTCTime", request_date);

        Log.d("url", "request_date" + request_date);
        String stringToSign = StringtoSign(key, content_type, request_date);
        Log.d("stringToSign", stringToSign);
        String base64encoderStr = "";
        try {
            base64encoderStr = Base64.encodeToString(
                    signature(stringToSign, secret), Base64.NO_WRAP);
            Log.d("url", "accesskey = " + base64encoderStr + "*****");
            base64encoderStr = URLEncoder.encode(base64encoderStr, "UTF-8");
            Log.d("url", "URLEncoder accesskey = " + base64encoderStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = "";
        if (content_type.equals("airquality")) {
            url = "http://api.weathercn.com/airquality/v1/observations/"
                    + locationkey + "?apikey=" + key + "&requestDate="
                    + request_date + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
        } else if (content_type.equals("currentconditions")) {
            url = "http://api.weathercn.com/currentconditions/v1/"
                    + locationkey + "?apikey=" + key + "&requestDate="
                    + request_date + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
        } else if (content_type.equals("forecasts")) {
            if (content_type1.equals("forecast24h")) {
                url = "http://api.weathercn.com/forecasts/v1/hourly/24hour/"
                        + locationkey + "?apikey=" + key + "&requestDate="
                        + request_date + "&accessKey=" + base64encoderStr
                        + "&details=true&language=" + language;
            } else if (content_type1.equals("forecast5day")) {
                url = "http://api.weathercn.com/forecasts/v1/daily/5day/"
                        + locationkey + "?apikey=" + key + "&requestDate="
                        + request_date + "&accessKey=" + base64encoderStr
                        + "&details=true&language=" + language;
            }
        } else if (content_type.equals("alerts")) {
            url = "http://api.weathercn.com/alerts/v1/" + locationkey
                    + ".json?apikey=" + key + "&requestDate=" + request_date
                    + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
        }
        return url;
    }

    public static String getLocationUrl(String key, String secret,
            String language, String longitude, String latitude, String cityName) {
        String request_date = "";
        Calendar calendar = Calendar.getInstance();
        String currentTime = calendar.get(Calendar.YEAR)
                + ""
                + (calendar.get(Calendar.MONTH) + 1 < 10 ? "0"
                        + (calendar.get(Calendar.MONTH) + 1) : (calendar
                        .get(Calendar.MONTH) + 1))
                + ""
                + (calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
                        + calendar.get(Calendar.DAY_OF_MONTH) : calendar
                        .get(Calendar.DAY_OF_MONTH));
        if (NetworkUtils.isNetworkAvailable()) {
            request_date = getUTCTime();
            // 20170804072
            String date = request_date.substring(0, 8);
            Log.e("fushuo", "currentTime=" + currentTime);
            Log.e("fushuo", "date=" + date);
            if (Integer.parseInt(currentTime) > Integer.parseInt(date)) {
                // ToastHelper.show(CalendarApplication.getContext(),
                // CalendarApplication.getContext().getString(R.string.search_past_time));
                return "";
            }
            Log.e("fushuo", "currentTime=" + currentTime);
        } else {
            request_date = (String) SharedPreferencesUtils.get("getUTCTime",
                    getUTCTime());
        }
        SharedPreferencesUtils.put("getUTCTime", request_date);
        Log.d("url", "request_date" + request_date);
        Log.e("fushuo", "1");
        String stringToSign = StringtoSign(key, "locations", request_date);
        String base64encoderStr = "";
        String url = "";
        try {
            Log.e("fushuo", "2" + System.currentTimeMillis());
            base64encoderStr = Base64.encodeToString(
                    signature(stringToSign, secret), Base64.NO_WRAP);
            base64encoderStr = URLEncoder.encode(base64encoderStr, "UTF-8");
            Log.e("fushuo", "3" + System.currentTimeMillis());
            if (cityName.length() > 0) {
                url = "http://api.weathercn.com/locations/v1/cities/autocomplete?q="
                        + cityName
                        + "&apikey="
                        + key
                        + "&requestDate="
                        + request_date
                        + "&accessKey="
                        + base64encoderStr
                        + "&details=true&language=" + language;
            } else {
                url = "http://api.weathercn.com/locations/v1/cities/geoposition/search.json?q="
                        + longitude
                        + ","
                        + latitude
                        + "&apikey="
                        + key
                        + "&requestDate="
                        + request_date
                        + "&accessKey="
                        + base64encoderStr
                        + "&details=true&language="
                        + language;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return url;
    }

    public static byte[] signature(String data, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException,
            UnsupportedEncodingException {
        byte[] keyBytes = privateKey.getBytes("utf-8");
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(signingKey);
        byte[] rawHmac1 = mac.doFinal(data.getBytes("utf-8"));
        return rawHmac1;
    }

    public static String getWindLevel(double wind_speed) {
        String[] windLevel = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.windlevel);
        if (wind_speed < 1)
            return windLevel[0];
        else if (wind_speed < 6)
            return windLevel[1];
        else if (wind_speed < 12)
            return windLevel[2];
        else if (wind_speed < 20)
            return windLevel[3];
        else if (wind_speed < 29)
            return windLevel[4];
        else if (wind_speed < 39)
            return windLevel[5];
        else if (wind_speed < 50)
            return windLevel[6];
        else if (wind_speed < 62)
            return windLevel[7];
        else if (wind_speed < 75)
            return windLevel[8];
        else if (wind_speed < 89)
            return windLevel[9];
        else if (wind_speed < 103)
            return windLevel[10];
        else if (wind_speed < 118)
            return windLevel[11];
        else
            return windLevel[12];
    }

    public static String getPollutionLevel(int index) {
        String[] polutionLevel = CalendarApplication.getContext()
                .getResources().getStringArray(R.array.polutionlevel);
        if (index > 0 && index <= 50)
            return polutionLevel[0];
        else if (index > 50 && index <= 100)
            return polutionLevel[1];
        else if (index > 100 && index <= 150)
            return polutionLevel[2];
        else if (index > 150 && index <= 200)
            return polutionLevel[3];
        else if (index > 200 && index <= 300)
            return polutionLevel[4];
        else
            return polutionLevel[5];
    }

    public static int getPollutionIcon(int index) {
        if (index > 0 && index <= 50)
            return R.drawable.sunny;
        else if (index > 50 && index <= 100)
            return R.drawable.sunny;
        else if (index > 100 && index <= 150)
            return R.drawable.sunny;
        else if (index > 150 && index <= 200)
            return R.drawable.sunny;
        else if (index > 200 && index <= 300)
            return R.drawable.sunny;
        else
            return R.drawable.sunny;
    }
}
