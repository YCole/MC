package com.hct.calendar.lbs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.apkfuns.logutils.LogUtils;
import com.hct.calendar.data.Action;

/**
 * Created by cat on 2017/6/5.
 */

public class LocationUtils {
    public static final int REQUEST_LOCATION_PERMISSION = 17 + 31;

    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    public static void getLocation(@NonNull Activity context,
            @NonNull final Action<Location> action) {
        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                action.next(location);
                // Called when a new location is found by the network location
                // provider.
                LogUtils.e(location);

                long time = location.getTime();
                SimpleDateFormat sf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String format = sf.format(new Date(time));
                // Remove the listener you previously added
                locationManager.removeUpdates(this);
            }

            public void onStatusChanged(String provider, int status,
                    Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location
        // updates
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(
                            context,
                            new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                            REQUEST_LOCATION_PERMISSION);
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        LogUtils.e("***---------------********");
    }

    public static interface RequestLocationPermission {
        void requestLocation(boolean granted);
    }

    public static RequestLocationPermission rP;
}
