/*
 * Add by zhangwei
 *
 * Loaction based service
 */

package com.android.calendar.lbs;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class LocationBasedService {
    public static final int LOCATION_REQUEST = 0;

    public static void startHctLBSAPP(Activity activity, String location,
            String mapParam) {
        Intent localIntent = new Intent();
        localIntent.setAction("com.hct.lbs.SHOW");

        try {
            JSONObject root = null;

            if (mapParam != null) {
                root = new JSONObject(mapParam);
                root.put("InitLocation", "SpecificLocation");
            } else if ((location != null) && (!location.equals(""))) {
                root = new JSONObject();
                root.put("InitLocation", "MyLocation");
                root.put("SearchKeyword", location);
            } else {
                root = new JSONObject();
                root.put("InitLocation", "MyLocation");
                root.put("MapZoom", 12);
            }

            localIntent.putExtra("MapParam", root.toString());
            Log.i("startHctLBSAPP", root.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        activity.startActivityForResult(localIntent, LOCATION_REQUEST);
    }

    private static final String MAP_SDK_PACKAGE = "com.hct.lbs";

    public static boolean isLbsAppAvilible(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(
                    MAP_SDK_PACKAGE, 0);
            if (packageInfo != null) {
                if (packageInfo.applicationInfo != null) {
                    return packageInfo.applicationInfo.enabled;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
