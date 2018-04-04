package com.hct.calendar.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.WorkerThread;
import android.support.annotation.NonNull;

import com.apkfuns.logutils.LogUtils;

public class NetAccess {
    /**
     * @param urlStr
     *            a string of access URL
     * @return the data json string
     * @throws IOException
     */
    @WorkerThread
    @NonNull
    public static String accessNetWork(@NonNull String urlStr)
            throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            LogUtils.e("respCode = " + response);
            is = conn.getInputStream();
            // Convert the InputStream into a string
            String contentAsString = convertStreamToString(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Reads an InputStream and converts it to a String.
     * 
     * @param is
     *            InputStream
     * @return string
     */
    @WorkerThread
    @NonNull
    private static String convertStreamToString(@NonNull InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

}
