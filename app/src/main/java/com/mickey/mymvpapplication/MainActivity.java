package com.mickey.mymvpapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mickey.mymvpapplication.audio.AudioInfo;
import com.mickey.mymvpapplication.audio.AudioManager;
import com.mickey.mymvpapplication.audio.MediaSessionConnection;
import com.mickey.mymvpapplication.base.BaseMVPActivity;
import com.mickey.mymvpapplication.jipaiqi.JiPaiQiActivity;
import com.mickey.mymvpapplication.mvp.beans.User;
import com.mickey.mymvpapplication.mvp.contract.UserInfoContract;
import com.mickey.mymvpapplication.mvp.presenter.UserInfoPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseMVPActivity<UserInfoContract.View,UserInfoContract.Presenter> implements UserInfoContract.View, View.OnClickListener {

    TextView textView;
    Button button;
    MediaSessionConnection sessionConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getUserInfo("MickeyQiong");
    }

    @Override
    protected void initView() {
        super.initView();
        button = findViewById(R.id.button);
        button.setOnClickListener(this);
        textView = findViewById(R.id.text);
        sessionConnection = MediaSessionConnection.getInstance();
        final List<AudioInfo> list = new ArrayList<>();
        final AudioInfo audioInfo = new AudioInfo();
        audioInfo.audioId = "1";
        audioInfo.audioName = "心雨";
        audioInfo.audioCover = "https://www.qqkw.com/d/file/p/2018/04-21/c24fd86006670f964e63cb8f9c129fc6.jpg";
        audioInfo.audioUrl = "http://music.163.com/song/media/outer/url?id=317151.mp3&a=我";
        list.add(audioInfo);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager.getInstance().playMusicByInfo(list);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionConnection.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sessionConnection.disconnect();
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

    @Override
    public void onClick(View v) {
//        startActivity(new Intent(this, JiPaiQiActivity.class));
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivity(intent);
    }
}
