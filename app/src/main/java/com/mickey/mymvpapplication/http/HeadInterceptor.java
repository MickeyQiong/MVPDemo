package com.mickey.mymvpapplication.http;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Description:设置请求头
 * Created by Ming on 2019-5-20.
 * Email mingqiang@bailitop.com
 */
public class HeadInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        builder.addHeader("Content-type", "application/json; charset=utf-8");

//        String host = request.url().host();
//        String url = request.url().toString();

        return chain.proceed(builder.build());
    }
}
