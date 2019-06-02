package com.mickey.mymvpapplication.audio;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * Describe:
 * Created by Administrator on 2019/6/2.
 */
public class PlaybackManager implements Playback.Callback {
    private static final String TAG = "PlaybackManager";

    private Context mContext;
    private QueueManager mQueueManager;
    private Playback mPlayback;
    private PlaybackServiceCallback mServiceCallback;
    private MediaSessionCallback mMediaSessionCallback;
    private int currRepeatMode;
    private PlaybackStateCompat.Builder stateBuilder;

    public PlaybackManager(Context context, PlaybackServiceCallback serviceCallback, QueueManager queueManager,
                           Playback playback) {
        mContext = context;

        mServiceCallback = serviceCallback;
        mQueueManager = queueManager;
        mMediaSessionCallback = new MediaSessionCallback();
        mPlayback = playback;
        mPlayback.setCallback(this);
        AudioManager.getInstance().setPlayback(mPlayback);
        currRepeatMode = PlaybackStateCompat.REPEAT_MODE_NONE;
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public MediaSessionCompat.Callback getMediaSessionCallback() {
        return mMediaSessionCallback;
    }

    /**
     * 播放
     */
    public void handlePlayRequest(boolean isPlayWhenReady) {
        MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
        if (currentMusic != null) {
            if (isPlayWhenReady) {
                mServiceCallback.onPlaybackStart();
            }
            mPlayback.play(currentMusic, isPlayWhenReady);
        }
    }

    /**
     * 暂停
     */
    public void handlePauseRequest() {
        if (mPlayback.isPlaying()) {
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    /**
     * 停止
     */
    public void handleStopRequest(String withError) {
        mPlayback.stop();
        mServiceCallback.onPlaybackStop();
        updatePlaybackState(false, withError);
    }

    /**
     * 快进
     */
    public void handleFastForward() {
        mPlayback.onFastForward();
    }

    /**
     * 倒带
     */
    public void handleRewind() {
        mPlayback.onRewind();
    }

    /**
     * 指定语速 refer 是否已当前速度为基数  multiple 倍率
     */
    public void handleDerailleur(boolean refer, float multiple) {
        mPlayback.onDerailleur(refer, multiple);
    }

    /**
     * 更新播放状态
     */
    public void updatePlaybackState(boolean isOnlyUpdateActions, String error) {
        if (isOnlyUpdateActions && stateBuilder != null) {
            //单独更新 Actions
            stateBuilder.setActions(getAvailableActions());
            mServiceCallback.onPlaybackStateUpdated(stateBuilder.build(), null);
        } else {
            long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
            if (mPlayback != null && mPlayback.isConnected()) {
                position = mPlayback.getCurrentStreamPosition();
            }
            //构建一个播放状态对象
            stateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(getAvailableActions());
            //获取播放器播放状态
            int state = mPlayback.getState();
            //如果错误信息不为 null 的时候，播放状态设为 STATE_ERROR
            if (error != null) {
                stateBuilder.setErrorMessage(error);
                state = PlaybackStateCompat.STATE_ERROR;
            }
            //设置播放状态
            stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());
            //设置当前活动的 songId
            MediaSessionCompat.QueueItem currentMusic = mQueueManager.getCurrentMusic();
            MediaMetadataCompat currMetadata = null;
            if (currentMusic != null) {
                stateBuilder.setActiveQueueItemId(currentMusic.getQueueId());
                final String musicId = currentMusic.getDescription().getMediaId();
                currMetadata = AudioProvider.getInstance().getMusic(musicId);
            }
            //把状态回调出去
            mServiceCallback.onPlaybackStateUpdated(stateBuilder.build(), currMetadata);
            //如果是播放或者暂停的状态，更新一下通知栏
            if (state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_PAUSED) {
                mServiceCallback.onNotificationRequired();
            }
        }
    }

    /**
     * 获取状态
     */
    private long getAvailableActions() {
        long actions = PlaybackStateCompat.ACTION_PLAY_PAUSE |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH |
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE; //添加 ACTION_PAUSE
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY; //添加 ACTION_PLAY
        }
        return actions;
    }

    /**
     * 播放器播放完成回调
     */
    @Override
    public void onCompletion() {
        updatePlaybackState(false, null);
    }

    /**
     * 播放器播放状态改变回调
     */
    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState(false, null);
    }

    /**
     * 播放器发送错误回调
     */
    @Override
    public void onError(String error) {
        updatePlaybackState(false, error);
    }

    /**
     * 设置当前播放 id
     */
    @Override
    public void setCurrentMediaId(String mediaId) {
        mQueueManager.setQueueFromMusic(mediaId);
    }

    /**
     * MusicManager API 方法的具体实现
     */
    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPrepare() {
            super.onPrepare();
            handlePlayRequest(false);
        }

        @Override
        public void onPrepareFromMediaId(String mediaId, Bundle extras) {
            super.onPrepareFromMediaId(mediaId, extras);
            mQueueManager.setQueueFromMusic(mediaId);
            handlePlayRequest(false);
        }

        @Override
        public void onPlay() {
            if (mQueueManager.getCurrentMusic() == null) {
                mQueueManager.setRandomQueue();
            }
            handlePlayRequest(true);
        }

        @Override
        public void onSkipToQueueItem(long queueId) {
            mQueueManager.setCurrentQueueItem(String.valueOf(queueId));
            mQueueManager.updateMetadata();
        }

        @Override
        public void onSeekTo(long position) {
            mPlayback.seekTo((int) position);
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mQueueManager.setQueueFromMusic(mediaId);
            handlePlayRequest(true);
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest(null);
        }

        @Override
        public void onSkipToNext() {
        }

        @Override
        public void onSkipToPrevious() {
        }

        @Override
        public void onCustomAction(@NonNull String action, Bundle extras) {
            //updatePlaybackState(null);
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
            handleFastForward();
        }

        @Override
        public void onRewind() {
            super.onRewind();
            handleRewind();
        }

        @Override
        public void onSetShuffleMode(int shuffleMode) {
            super.onSetShuffleMode(shuffleMode);
            mQueueManager.setQueueByShuffleMode(shuffleMode);
            mServiceCallback.onShuffleModeUpdated(shuffleMode);
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
            currRepeatMode = repeatMode;
            mServiceCallback.onRepeatModeUpdated(repeatMode);
            updatePlaybackState(true, null);  //更新状态
        }

        /**
         * 自定义方法
         */
        @Override
        public void onCommand(String command, Bundle extras, ResultReceiver cb) {
            super.onCommand(command, extras, cb);
            if (command == null) {
                return;
            }
            if (ExoPlayback.ACTION_CHANGE_VOLUME.equals(command)) {
                float audioVolume = extras.getFloat("AudioVolume");
                //mPlayback.setVolume(audioVolume);
            }
            if (ExoPlayback.ACTION_DERAILLEUR.equals(command)) {
                boolean refer = extras.getBoolean("refer");
                float multiple = extras.getFloat("multiple");
                handleDerailleur(refer, multiple);
            }
        }
    }

    public interface PlaybackServiceCallback {
        void onPlaybackStart();

        void onNotificationRequired();

        void onPlaybackStop();

        void onPlaybackStateUpdated(PlaybackStateCompat newState, MediaMetadataCompat currMetadata);

        void onShuffleModeUpdated(int shuffleMode);

        void onRepeatModeUpdated(int repeatMode);
    }
}
