package com.mickey.mymvpapplication.base;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Description:
 * Created by Ming on 2019-5-18.
 * Email mingqiang@bailitop.com
 */
public class App extends MultiDexApplication {

    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context){
        App app = (App) context.getApplicationContext();
        return app.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }
}
