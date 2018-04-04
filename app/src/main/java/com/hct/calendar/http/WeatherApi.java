package com.hct.calendar.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;

public class WeatherApi {
    public class Secret {
        public static final String APIKey = "98a7c387efc849cda7d67c74a796599d";
        public static final String Secret = "hoybV3bj+E23iKnrfX/rxA==";
    }

    public static boolean isNetworkAvailable(@NonNull Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String getUTCTime() {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        String date = DateFormat.format("yyyyMMddkkmm", cal).toString();
        return date.substring(0, date.length() - 1);
    }

    private static String StringtoSign(String apiKey, String apiContentType,
            String requestDate) {
        StringBuilder sf = new StringBuilder();
        sf.append(apiKey);
        sf.append("\r\n");
        sf.append(apiContentType);
        sf.append("\r\n");
        sf.append(requestDate);
        return sf.toString();
    }

    public static String generateUrl(@NonNull Context context,
            String urlChoose, String content_type1, String locationkey,
            String language) {
        // ("currentconditions", "", key, Secret.APIKey, Secret.Secret,
        // "zh-cn");
        String key = Secret.APIKey;
        String secret = Secret.Secret;
        String request_date = "";
        if (isNetworkAvailable(context)) {
            request_date = getUTCTime();
        } else {
            // request_date = (String) SharedPreferencesUtils.get("getUTCTime",
            // getUTCTime());
        }
        // SharedPreferencesUtils.put("getUTCTime", request_date);

        LogUtils.d("request_date" + request_date);
        String stringToSign = StringtoSign(key, urlChoose, request_date);
        Log.d("stringToSign", stringToSign);
        String base64encoderStr = "";
        try {
            base64encoderStr = Base64.encodeToString(
                    signature(stringToSign, secret), Base64.NO_WRAP);
            LogUtils.d("accesskey = " + base64encoderStr + "*****");
            base64encoderStr = URLEncoder.encode(base64encoderStr, "UTF-8");
            LogUtils.d("URLEncoder accesskey = " + base64encoderStr);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String url = "";
        switch (urlChoose) {
        case "airquality":
            url = "http://apidev.weathercn.com/airquality/v1/observations/"
                    + locationkey + "?apikey=" + key + "&requestDate="
                    + request_date + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
            break;
        case "currentconditions":
            url = "http://apidev.weathercn.com/currentconditions/v1/"
                    + locationkey + "?apikey=" + key + "&requestDate="
                    + request_date + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
            break;
        case "forecasts":
            if (content_type1.equals("forecast24h")) {
                url = "http://apidev.weathercn.com/forecasts/v1/hourly/24hour/"
                        + locationkey + "?apikey=" + key + "&requestDate="
                        + request_date + "&accessKey=" + base64encoderStr
                        + "&details=true&language=" + language;
            } else if (content_type1.equals("forecast5day")) {
                url = "http://apidev.weathercn.com/forecasts/v1/daily/5day/"
                        + locationkey + "?apikey=" + key + "&requestDate="
                        + request_date + "&accessKey=" + base64encoderStr
                        + "&details=true&language=" + language;
            }
            break;
        case "alerts":
            url = "http://apidev.weathercn.com/alerts/v1/" + locationkey
                    + ".json?apikey=" + key + "&requestDate=" + request_date
                    + "&accessKey=" + base64encoderStr
                    + "&details=true&language=" + language;
            break;
        }
        LogUtils.e("w.link = " + url);
        return url;
    }

    public static String getLocationUrl(@NonNull Context context,
            String latitude, String longitude, String language) {
        String cityName = "";
        String key = Secret.APIKey;
        String secret = Secret.Secret;
        String request_date = "";
        if (isNetworkAvailable(context)) {
            request_date = getUTCTime();
        } else {
            // request_date = (String) SharedPreferencesUtils.get("getUTCTime",
            // getUTCTime());
        }
        // SharedPreferencesUtils.put("getUTCTime", request_date);

        LogUtils.d("request_date" + request_date);
        String stringToSign = StringtoSign(key, "locations", request_date);
        String base64encoderStr = "";
        String url = "";
        try {
            base64encoderStr = Base64.encodeToString(
                    signature(stringToSign, secret), Base64.NO_WRAP);
            base64encoderStr = URLEncoder.encode(base64encoderStr, "UTF-8");

            if (cityName.length() > 0) {
                url = "http://apidev.weathercn.com/locations/v1/cities/autocomplete?q="
                        + cityName
                        + "&apikey="
                        + key
                        + "&requestDate="
                        + request_date
                        + "&accessKey="
                        + base64encoderStr
                        + "&details=true&language=" + language;
            } else {
                url = "http://apidev.weathercn.com/locations/v1/cities/geoposition/search.json?q="
                        + latitude
                        + ","
                        + longitude
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
        LogUtils.e("L.link = " + url);
        return url;
    }

    private static byte[] signature(String data, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeyException,
            UnsupportedEncodingException {
        byte[] keyBytes = privateKey.getBytes("utf-8");
        SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacMD5");
        Mac mac = Mac.getInstance("HmacMD5");
        mac.init(signingKey);
        byte[] rawHmac1 = mac.doFinal(data.getBytes("utf-8"));
        return rawHmac1;
    }
}
