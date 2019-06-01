package com.mickey.mymvpapplication.audio;

import android.content.Context;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description:音频管理类
 * Created by Ming on 2019-6-1.
 */
public class AudioManager {
    private static Context mContext;
    private CopyOnWriteArrayList<PlayerEventListener> mPlayerEventListeners = new CopyOnWriteArrayList<>();
    private Playback mPlayback;

    public static AudioManager getInstance(){
        return new AudioManager();
    }

    public static void initAudioManager(Context context){
        mContext = context;

    }
}
