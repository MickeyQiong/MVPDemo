package com.mickey.mymvpapplication.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public class RetrofitHelper {
    private static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://api.github.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(OkHttpClientUtil.getInstance().getOkHttpClient())
                            .build();
                }
            }
        }
        return retrofit;
    }
}
