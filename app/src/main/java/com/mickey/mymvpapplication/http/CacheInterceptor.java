package com.mickey.mymvpapplication.http;

import com.mickey.mymvpapplication.base.App;
import com.mickey.mymvpapplication.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        //网络不可用
        if (!NetworkUtils.isAvailable(App.mAppContext)) {
            //在请求头中加入：强制使用缓存，不访问网络
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        Response response = chain.proceed(request);
        //网络可用
        if (NetworkUtils.isAvailable(App.mAppContext)) {
            int maxAge = 0;
            // 有网络时 在响应头中加入：设置缓存超时时间0个小时
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("pragma")
                    .build();
        } else {
            // 无网络时，在响应头中加入：设置超时为4周
            int maxStale = 60 * 60 * 24 * 28;
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("pragma")
                    .build();
        }
        return response;
    }
}
