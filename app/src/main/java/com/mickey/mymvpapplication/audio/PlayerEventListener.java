package com.mickey.mymvpapplication.audio;

/**
 * Description:播放监听
 * Created by Ming on 2019-6-1.
 */
public interface PlayerEventListener {
    /**
     * 切换音频播放
     * 目前不支持
     * 等以后可以把想听的音频建立成一个列表的时候可以用到
     */
    void onAudioSwitch(AudioInfo audioInfo);

    /**
     * 开始播放
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 停止播放
     */
    void onPlayerStop();

    /**
     * 播放完成
     */
    void onPlayerCompletion(AudioInfo audioInfo);

    /**
     * 播放错误
     */
    void onError(String errorMsg);
}
