package com.mickey.mymvpapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Description:网络监测
 * Created by Ming on 2019-5-20.
 */
public class NetworkUtils {

    public static boolean isAvailable(Context context) {
        ConnectivityManager connectivityService = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityService == null) return false;
        NetworkInfo networkInfo = connectivityService.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
