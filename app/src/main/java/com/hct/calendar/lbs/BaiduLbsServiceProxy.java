package com.hct.calendar.lbs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.hct.lbsApp.aidl.IlbsService;

public class BaiduLbsServiceProxy extends LbsServiceProxy implements
        IlbsService {
    private static final String TAG = "BaiduLbsService";
    public static final String MAP_SDK_SERVICE_ACTION = "com.hct.lbs.start";
    public static final String MAP_SDK_ACTIVITY_ACTION = "com.hct.lbsApp.Locate";
    public static final String MAP_SDK_PACKAGE = "com.hct.lbsApp";
    public static final String MAP_SDK_VIEW_ACTION = "com.hct.lbsApp.Startsnapshot";
    public static final String MAP_SDK_CREATE_FENCEN_ACTION = "com.hct.lbsApp.remindingDialog";
    public static final int MAP_ACTIVITY_REQUEST_CODE = 1000;
    public static final int MAP_ACTIVITY_CREATE_FENCEN_REQUEST_CODE = 2000;
    private IlbsService mIlbsService;

    public BaiduLbsServiceProxy(Context context, Intent serIntent) {
        super(context, serIntent);
    }

    public static BaiduLbsServiceProxy newProxyInstance(Context context) {
        Intent lbsintent = new Intent(MAP_SDK_SERVICE_ACTION);
        lbsintent.setPackage(MAP_SDK_PACKAGE);
        return new BaiduLbsServiceProxy(context, lbsintent);
    }

    public static void startHctLBSAPPForLocation(Activity activity,
            String location, String mapParam) {
        Intent intent = getMapActivityIntent();
        try {
            activity.startActivityForResult(intent, MAP_ACTIVITY_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "startHctLBSAPPForLocation ,activity not find Intent:"
                    + intent);
        }
    }

    public static void startHctLBSAPPForView(Activity activity,
            String location, String mapParam) {
        Intent intent = new Intent(MAP_SDK_VIEW_ACTION);
        intent.putExtra("mapParam", mapParam);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG, "startHctLBSAPPForView ,activity not find Intent:"
                    + intent);
        }
    }

    public static void startHctLBSAPPForCreateFence(Activity activity,
            String mapParam, long eventId, long radius) {
        Intent intent = new Intent(MAP_SDK_CREATE_FENCEN_ACTION);
        try {
            activity.startActivityForResult(intent,
                    MAP_ACTIVITY_CREATE_FENCEN_REQUEST_CODE);
        } catch (ActivityNotFoundException ex) {
            Log.e(TAG,
                    "startHctLBSAPPForCreateFence ,activity not find Intent:"
                            + intent);
        }
    }

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

    /**
     * 
     * @return
     */
    public static Intent getMapActivityIntent() {
        Intent intent = new Intent(MAP_SDK_ACTIVITY_ACTION);
        intent.setPackage(MAP_SDK_PACKAGE);
        return intent;
    }

    @Override
    protected void holdBinder(IBinder binder) {
        mIlbsService = IlbsService.Stub.asInterface(binder);
    }

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void setCenterPoint(final double latPoint, final double lngPoint)
            throws RemoteException {
        setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    mIlbsService.setCenterPoint(latPoint, lngPoint);
                } catch (RemoteException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }, "setCenterPoint");
    }

    @Override
    public void setTestPoint(double latPoi, double lngPoi)
            throws RemoteException {

    }

    @Override
    public boolean isInCircle() throws RemoteException {
        return false;
    }

    @Override
    public void setDistance(int distance) throws RemoteException {

    }

    @Override
    public void reverseGeoCode(double latPoi, double lngPoi)
            throws RemoteException {

    }

    @Override
    public void startlbsSearchImpl() throws RemoteException {

    }

    @Override
    public String getCityAddress() throws RemoteException {
        return null;
    }

    @Override
    public void startlbsNaviImpl(String mapParam) throws RemoteException {

    }

    @Override
    public void createfenceImpl(final String mapParam, final String fenceParam)
            throws RemoteException {
        setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    mIlbsService.createfenceImpl(mapParam, fenceParam);
                } catch (RemoteException e) {
                    Log.e(TAG, "createfenceImpl error:" + e.getMessage());
                }
            }
        }, "createfenceImpl");
    }

    @Override
    public void updatefenceImpl(final String mapParam, final String fenceParam)
            throws RemoteException {
        setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    mIlbsService.updatefenceImpl(mapParam, fenceParam);
                } catch (RemoteException e) {
                    Log.e(TAG, "delFenceImpl error:" + e.getMessage());
                }
            }
        }, "updatefenceImpl");
    }

    @Override
    public void delFenceImpl(final int fenceId, final String fenceParam)
            throws RemoteException {
        setTask(new Runnable() {

            @Override
            public void run() {
                try {
                    mIlbsService.delFenceImpl(fenceId, fenceParam);
                } catch (RemoteException e) {
                    Log.e(TAG, "delFenceImpl error:" + e.getMessage());
                }
            }
        }, "delFenceImpl");
    }

    @Override
    public void queryFenceImpl(String monitoredPersons, int fenceId)
            throws RemoteException {

    }

    @Override
    public String getDistrictAddress() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDetailAddress() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

}
