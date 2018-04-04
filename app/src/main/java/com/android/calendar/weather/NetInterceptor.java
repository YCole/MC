package com.android.calendar.weather;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import com.hct.calendar.utils.NetworkUtils;

public class NetInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!NetworkUtils.isNetworkAvailable()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE).build();
        }

        Response response = chain.proceed(request);

        CacheControl cacheControl = new CacheControl.Builder()
                // .noCache()
                // .noStore()
                .maxAge(10, TimeUnit.MINUTES).maxStale(1, TimeUnit.DAYS)
                .build();

        return response.newBuilder()

        .header("Cache-Control", cacheControl.toString())
                .removeHeader("Pragma").build();
    }
}
