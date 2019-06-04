package com.mickey.mymvpapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lzx.starrysky.manager.MediaSessionConnection;
import com.lzx.starrysky.manager.MusicManager;
import com.lzx.starrysky.manager.OnPlayerEventListener;
import com.lzx.starrysky.model.SongInfo;
import com.lzx.starrysky.utils.TimerTaskManager;
import com.mickey.mymvpapplication.base.BaseMVPActivity;
import com.mickey.mymvpapplication.mvp.beans.User;
import com.mickey.mymvpapplication.mvp.contract.UserInfoContract;
import com.mickey.mymvpapplication.mvp.presenter.UserInfoPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseMVPActivity<UserInfoContract.View,UserInfoContract.Presenter> implements UserInfoContract.View, OnPlayerEventListener {

    private static final String TAG = "MainActivity";
    TextView textView;
    List<SongInfo> list = new ArrayList<>();
    MediaSessionConnection mediaSessionConnection;
    TimerTaskManager timerTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter.getUserInfo("MickeyQiong");

        mediaSessionConnection = MediaSessionConnection.getInstance();
        timerTaskManager = new TimerTaskManager();

        MusicManager.getInstance().addPlayerEventListener(this);

        SongInfo s1 = new SongInfo();
        s1.setSongId("111");
        s1.setSongUrl("http://music.163.com/song/media/outer/url?id=317151.mp3&a=我");
        s1.setSongCover("https://www.qqkw.com/d/file/p/2018/04-21/c24fd86006670f964e63cb8f9c129fc6.jpg");
        s1.setSongName("心雨");
        s1.setArtist("贤哥");
        list.add(s1);
    }

    @Override
    protected void initView() {
        super.initView();
        textView = findViewById(R.id.text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.getInstance().playMusic(list,0);
            }
        });
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
    protected void onStart() {
        super.onStart();
        mediaSessionConnection.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaSessionConnection.disconnect();
    }

    @Override
    public void onMusicSwitch(SongInfo songInfo) {

    }

    @Override
    public void onPlayerStart() {
        Log.e(TAG,"onPlayerStart");
    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPlayerStop() {

    }

    @Override
    public void onPlayCompletion(SongInfo songInfo) {

    }

    @Override
    public void onBuffering() {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {

    }
}
