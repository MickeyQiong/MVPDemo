package com.mickey.mymvpapplication.http;

import com.mickey.mymvpapplication.BuildConfig;
import com.mickey.mymvpapplication.base.App;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Description:
 * Created by Ming on 2019-5-20.
 * Email mingqiang@bailitop.com
 */
class OkHttpClientUtil {
    private static OkHttpClientUtil mInstance;
    private OkHttpClient okHttpClient;

    private OkHttpClientUtil() {

    }

    static OkHttpClientUtil getInstance(){
        if (mInstance == null){
            synchronized (OkHttpClientUtil.class){
                if (mInstance == null){
                    mInstance = new OkHttpClientUtil();
                }
            }
        }
        return mInstance;
    }

    OkHttpClient getOkHttpClient(){
        if (okHttpClient == null){
            return initOkHttpClient();
        }else {
            return okHttpClient;
        }
    }

    private OkHttpClient initOkHttpClient(){
        // log用拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        // 开发模式记录整个body，否则只记录基本信息如返回200，http协议版本等
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        }
        int DEFAULT_TIMEOUT = 15;
        okHttpClient = new OkHttpClient.Builder()
                .cache(initCache())
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new CacheInterceptor())
                .addInterceptor(new HeadInterceptor())
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        return okHttpClient;
    }

    private Cache initCache(){
        long MAX_CACHE_SIZE = 1024 * 1024 * 50;
        String CACHE_NAME = "http_cache";
        //设置缓存目录
        File cacheFile = new File(App.mAppContext.getExternalCacheDir(), CACHE_NAME);
        //生成缓存，50M
        return new Cache(cacheFile, MAX_CACHE_SIZE);
    }

}
