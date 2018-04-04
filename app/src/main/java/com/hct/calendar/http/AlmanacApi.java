package com.hct.calendar.http;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hct.calendar.domain.AlmanacBody;
import com.hct.calendar.domain.AlmanacBody.DataBean.JiBean;
import com.hct.calendar.domain.AlmanacBody.DataBean.YiBean;
import com.hct.calendar.domain.AlmanacItem;
import android.text.TextUtils;

public class AlmanacApi {

    public static boolean checkNetWork(@NonNull Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean canAccess = networkInfo != null && networkInfo.isConnected();
        return canAccess;

    }

    public static String urlTamplate(String day) {
        final String urlStr = String
                .format("http://zhwnlapi.etouch.cn/Ecalender/openapi/huangli/%s?key=%s",
                        day, "c40fbdbd2f6d413195f6045ce03e49dc");
        return urlStr;
    }

    public static AlmanacItem json2Item(@NonNull String json) {
        Log.e("test_AlmanacApi", "json2Item json >>> " + json);
        AlmanacItem item = new AlmanacItem();
        if (TextUtils.isEmpty(json)) {
            return item;
        }
        try {
            final AlmanacBody body = new Gson().fromJson(json,
                    AlmanacBody.class);
            if (body == null) {
                return null;
            }
            List<YiBean> yii = body.data.yi;
            StringBuilder yString = new StringBuilder();
            Iterator<YiBean> itY = yii.iterator();
            while (itY.hasNext()) {
                yString.append(itY.next().old).append(" ");
            }
            List<JiBean> jii = body.data.ji;
            StringBuilder jString = new StringBuilder();
            Iterator<JiBean> itJ = jii.iterator();
            while (itJ.hasNext()) {
                jString.append(itJ.next().old).append(" ");
            }
            item.jii = jString.toString();
            item.yii = yString.toString();
            item.lunarDate = body.data.tgdz.split(",")[0] + body.data.nongli;
        } catch (NullPointerException e) {
            Log.e("test_AlmanacApi", e.getMessage());
        } catch (IllegalStateException e) {
            Log.e("AlmanacApi_fromjson", e.getMessage());
        } catch (JsonSyntaxException e) {
            Log.e("AlmanacApi_fromjson",
                    "JsonSyntaxException error: " + e.getMessage(), e.fillInStackTrace());
        }
        return item;
    }
}
