package com.mickey.mymvpapplication.audio;

import android.support.v4.media.session.MediaSessionCompat;

/**
 * Description:播放器接口
 * Created by Ming on 2019-6-1.
 */
public interface Playback {

    /**
     * 播放
     * @param item 播放列表里的单个音频，它包含音频的id和描述
     * @param isPlayWhenReady 是否已经准备好了去播放
     */
    void play(MediaSessionCompat.QueueItem item, boolean isPlayWhenReady);

    void stop();

    int getState();

    boolean isConnected();

    boolean isPlaying();

    long getCurrentStreamPosition();

    long getBufferedPosition();

    long getDuration();

    void pause();

    void seekTo(long position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();

    void onFastForward();

    void onRewind();

    void onDerailleur(boolean refer, float multiple);

    interface Callback {
        void onCompletion();

        void onPlaybackStatusChanged(int state);

        void onError(String error);

        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}
