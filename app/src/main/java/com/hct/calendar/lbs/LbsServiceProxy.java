package com.hct.calendar.lbs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public abstract class LbsServiceProxy {
    public final static String TAG = "LbsServiceProxy";
    protected Context mContext;
    protected Intent mServiceIntent;
    private Runnable mTask = null;
    private boolean mTaskSet = false;
    private String mName;

    public LbsServiceProxy(Context context, Intent serIntent) {
        mContext = context;
        mServiceIntent = serIntent;
    }

    protected abstract void holdBinder(IBinder binder);

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service != null) {
                holdBinder(service);
            }
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        mTask.run();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    } finally {
                        mContext.unbindService(mServiceConnection);
                    }
                    return null;
                }
            }.execute();
        }
    };

    public boolean setTask(Runnable task, String name) {
        if (mTaskSet) {
            return false;
        }
        mTaskSet = true;
        mName = name;
        mTask = task;
        return mContext.bindService(mServiceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

}
