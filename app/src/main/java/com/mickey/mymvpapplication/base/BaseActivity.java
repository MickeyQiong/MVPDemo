package com.mickey.mymvpapplication.base;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Description:
 * Created by Ming on 2019-5-20.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes());
        initView();
    }

    protected abstract void initView();

    protected abstract int layoutRes();


    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getRefWatcher(this).watch(this);
    }


}
