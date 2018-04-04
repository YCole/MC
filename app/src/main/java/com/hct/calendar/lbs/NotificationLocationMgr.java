package com.hct.calendar.lbs;

import com.android.calendar.alerts.AlertService;
import com.hct.calendar.lbs.LocationFenceService.NotificationWrapper;

public abstract class NotificationLocationMgr {
    public abstract void notify(int id, NotificationWrapper notification);

    public abstract void cancel(int id);

    /**
     * Don't actually use the notification framework's cancelAll since the
     * SyncAdapter might post notifications and we don't want to affect those.
     */
    public void cancelAll() {
        cancelAllBetween(0, AlertService.MAX_NOTIFICATIONS);
    }

    /**
     * Cancels IDs between the specified bounds, inclusively.
     */
    public void cancelAllBetween(int from, int to) {
        for (int i = from; i <= to; i++) {
            cancel(i);
        }
    }
}
