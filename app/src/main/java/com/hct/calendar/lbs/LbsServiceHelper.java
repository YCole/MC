package com.hct.calendar.lbs;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.CalendarEventModel;
import com.android.calendar.lbs.LocationBasedService;
import com.hct.calendar.event.EventItem;
import com.hct.calendar.lbs.LbsServiceHelper.LocationFenceOperation.Builder;
import com.hct.lbsApp.aidl.IlbsService;

public class LbsServiceHelper {

    public static final String TAG = "LbsServiceHelper";

    public static IlbsService getServiceProxy(Context context) {
        return BaiduLbsServiceProxy.newProxyInstance(context
                .getApplicationContext());
    }

    public static boolean isLbsAppAvilible(Context context) {
        return BaiduLbsServiceProxy.isLbsAppAvilible(context
                .getApplicationContext())
                || LocationBasedService.isLbsAppAvilible(context
                        .getApplicationContext());
    }

    public static void startHctLBSAPPForLocation(Activity activity,
            String location, String mapParam) {
        if (BaiduLbsServiceProxy.isLbsAppAvilible(activity)) {
            BaiduLbsServiceProxy.startHctLBSAPPForLocation(activity, location,
                    mapParam);
        } else {
            LocationBasedService.startHctLBSAPP(activity, location, null);
        }
    }

    public static void startHctLBSAPPForView(Activity activity,
            String location, String mapParam) {
        if (BaiduLbsServiceProxy.isLbsAppAvilible(activity)) {
            BaiduLbsServiceProxy.startHctLBSAPPForView(activity, location,
                    mapParam);
        } else {
            LocationBasedService.startHctLBSAPP(activity, location, mapParam);
        }
    }

    public static boolean isLbsLocationFenceAvilible(Context context) {
        return false;
    }

    public static boolean saveOrUpdateLocationFence(Activity activity,
            CalendarEventModel old, CalendarEventModel model) {
        if ((old != null && old.mEventType == EventItem.VIEW_EVENT_LOCATION)
                || (model != null && model.mEventType == EventItem.VIEW_EVENT_LOCATION)) {
            Builder builder = LocationFenceOperation.build();
            if (old != null) {
                builder.buildOriginalModel(old.mId, old.mEventType,
                        old.mActionData, old.mActionData1, old.mActionData2,
                        old.mTitle, old.mLocation, old.mDescription);
            }
            if (model != null) {
                builder.buildModel(model.mId, model.mEventType,
                        model.mActionData, model.mActionData1,
                        model.mActionData2, model.mTitle, model.mLocation,
                        model.mDescription);
            }
            return builder.build().performOperation(activity);
        } else {
            return false;
        }
    }

    public static CreateFenceResult parseCreateFenceResult(String jsonResult) {
        JSONObject jsonObject;
        CreateFenceResult result = new CreateFenceResult();
        try {
            jsonObject = new JSONObject(jsonResult);
            result.status = jsonObject.getInt(CreateFenceResult.KEY_STATUS);
            result.message = jsonObject
                    .getString(CreateFenceResult.KEY_MESSAGE);
            if (result.status == 0) {
                result.fencenId = jsonObject
                        .getInt(CreateFenceResult.KEY_FENCEN_ID);
                result.packageName = jsonObject
                        .getString(CreateFenceResult.KEY_PACKAGE_NAME);
                String eventIdStr = jsonObject
                        .getString(CreateFenceResult.KEY_EVENT_ID);
                if (TextUtils.isEmpty(eventIdStr)) {
                    result.eventId = -1;
                } else {
                    try {
                        result.eventId = Integer.parseInt(eventIdStr);
                    } catch (NumberFormatException ex) {
                        Log.w(TAG,
                                "parseCreateFenceResult error,NumberFormatException:"
                                        + ex.getMessage());
                    }
                }
            }
            return result;
        } catch (JSONException e) {
            Log.e(TAG, "parseCreateFenceResult error,json string is:"
                    + jsonResult + ",error:" + e.getMessage());
            result.status = -1;
            result.message = "";
            return result;
        }

    }

    public static class CreateFenceResult {
        public static final String KEY_STATUS = "status";
        public static final String KEY_MESSAGE = "message";
        public static final String KEY_FENCEN_ID = "fenceId";
        public static final String KEY_EVENT_ID = "event_info";
        public static final String KEY_PACKAGE_NAME = "packageName";
        public int status;
        public String message;
        public int fencenId;
        public long eventId;
        public String packageName;
    }

    public static class LocationFenceModel {
        public long eventId;//
        public int evenType;// EVENT_EXT_EVENT_TYPE
        public String radius;// EVENT_EXT_ACTION_DATA
        public String mapParam;// EVENT_EXT_ACTION_DATA1
        public String fencenId;// EVENT_EXT_ACTION_DATA2

        public String eventTitle;
        public String location;
        public String description;

        public LocationFenceModel() {
        }

        public LocationFenceModel(long eventId, int eventType, String radius,
                String mapParam, String fenceId, String eventTitle,
                String location, String description) {
            this.eventId = eventId;
            this.evenType = eventType;
            this.radius = radius;
            this.mapParam = mapParam;
            this.fencenId = fenceId;
            this.eventTitle = eventTitle;
            this.location = location;
            this.description = description;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o == this) {
                return true;
            }
            LocationFenceModel obj = (LocationFenceModel) o;
            if (obj.mapParam != null && obj.mapParam.equals(mapParam)
                    && obj.fencenId != null && obj.fencenId.equals(fencenId)) {
                return true;
            } else {
                return false;
            }
        }

        public static LocationFenceModel buildModel(long eventId,
                int eventType, String radius, String mapParam, String fenceId,
                String eventTitle, String location, String description) {
            return new LocationFenceModel(eventId, eventType, radius, mapParam,
                    fenceId, eventTitle, location, description);
        }

    }

    public static class LocationFenceOperation {
        private LocationFenceModel originalModel = null;
        private LocationFenceModel model = null;// now model
        public static final int DONE_UPDATE = 1 << 0;
        public static final int DONE_SAVE = 1 << 1;
        public static final int DONE_DELETE = 1 << 2;
        public static final int DONE_NONE = 1 << 3;

        public LocationFenceOperation() {
            // TODO Auto-generated constructor stub
        }

        private int preProcess() {
            if (originalModel == null) {
                return DONE_SAVE;
            } else {
                if (originalModel.evenType == EventItem.VIEW_EVENT_LOCATION
                        && originalModel.evenType != EventItem.VIEW_EVENT_LOCATION) {
                    return DONE_DELETE;
                } else if (originalModel.evenType != EventItem.VIEW_EVENT_LOCATION
                        && originalModel.evenType == EventItem.VIEW_EVENT_LOCATION) {
                    return DONE_SAVE;
                } else {
                    if (model.equals(originalModel)) {
                        return DONE_NONE;
                    }
                    if (TextUtils.isEmpty(model.fencenId)) {
                        return DONE_SAVE;
                    }
                    return DONE_UPDATE;
                }
            }
        }

        public boolean performOperation(Context context) {
            int opt = preProcess();
            boolean isSync = false;
            long radius = 1000l;
            if (!TextUtils.isEmpty(model.radius)) {
                try {
                    radius = Long.parseLong(model.radius.trim());
                } catch (NumberFormatException e) {
                    Log.e(TAG,
                            " LocationFenceOperation.performOperation location fence create failed ,beacuse "
                                    + e.getMessage());
                }
            }
            switch (opt) {
            case DONE_UPDATE:
                updateLocationFence(context, model.mapParam, model.eventId,
                        radius);
                break;
            case DONE_SAVE:
                createLocationFence(context, model.mapParam, model.eventId,
                        radius);
                BaiduLbsServiceProxy.startHctLBSAPPForCreateFence(
                        (Activity) context, model.mapParam, model.eventId,
                        radius);
                isSync = true;
                break;
            case DONE_DELETE:
                if (TextUtils.isEmpty(originalModel.fencenId)) {

                } else {
                    try {
                        deleteLocationFence(context,
                                Integer.parseInt(originalModel.fencenId),
                                originalModel.eventId);
                    } catch (NumberFormatException ex) {

                    }
                }
                break;
            case DONE_NONE:
                break;
            default:
                Log.w(TAG, "exception opt code #" + opt);
                break;
            }
            return isSync;
        }

        public static Builder build() {
            return new Builder();
        }

        public static class Builder {
            private LocationFenceOperation fenceOperation = null;

            public Builder() {
                fenceOperation = new LocationFenceOperation();
            }

            public Builder buildOriginalModel(long eventId, int eventType,
                    String radius, String mapParam, String fenceId,
                    String eventTitle, String location, String description) {
                fenceOperation.originalModel = LocationFenceModel.buildModel(
                        eventId, eventType, radius, mapParam, fenceId,
                        eventTitle, location, description);
                return this;
            }

            public Builder buildModel(long eventId, int eventType,
                    String radius, String mapParam, String fenceId,
                    String eventTitle, String location, String description) {
                fenceOperation.model = LocationFenceModel.buildModel(eventId,
                        eventType, radius, mapParam, fenceId, eventTitle,
                        location, description);
                return this;
            }

            public LocationFenceOperation build() {
                return fenceOperation;
            }
        }

    }

    // add by hct.gengbin
    public static void createLocationFence(Context context, String mapPara,
            long eventId, long radius) {
        try {
            IlbsService lbsService = getServiceProxy(context);
            if (lbsService != null) {
                try {
                    lbsService.createfenceImpl(mapPara,
                            genFenceJsonString(context, eventId, radius));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void updateLocationFence(Context context, String mapPara,
            long eventId, long radius) {
        try {
            IlbsService lbsService = getServiceProxy(context);
            if (lbsService != null) {
                try {
                    lbsService.updatefenceImpl(mapPara,
                            genFenceJsonString(context, eventId, radius));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void deleteLocationFence(Context context, int fencenId,
            long eventId) {
        try {
            IlbsService lbsService = getServiceProxy(context);
            if (lbsService != null) {
                try {
                    lbsService.delFenceImpl(fencenId,
                            genFenceJsonString(context, eventId, 0));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String genFenceJsonString(Context context, long eventId,
            long radius) throws JSONException {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put("packageName", context.getPackageName());
        jsonObject.put("event_info", eventId);
        jsonObject.put("radius", radius);
        return jsonObject.toString();
    }
}
