package com.mickey.mymvpapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.mickey.mymvpapplication.base.BaseMVPActivity;
import com.mickey.mymvpapplication.mvp.beans.User;
import com.mickey.mymvpapplication.mvp.contract.UserInfoContract;
import com.mickey.mymvpapplication.mvp.presenter.UserInfoPresenter;

public class MainActivity extends BaseMVPActivity<UserInfoContract.View,UserInfoContract.Presenter> implements UserInfoContract.View {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getUserInfo("MickeyQiong");
    }

    @Override
    protected void initView() {
        super.initView();
        textView = findViewById(R.id.text);
    }

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected UserInfoContract.Presenter createPresenter() {
        return new UserInfoPresenter();
    }

    @Override
    public void getSuccess(User user) {
        textView.setText(user.url);
    }

    @Override
    public void getError(String msg) {
        Log.e("MainActivity",msg);
    }

    @Override
    public void showProgressDialog() {

    }

    @Override
    public void hideProgressDialog() {

    }
}
