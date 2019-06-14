package com.mickey.mymvpapplication.audio;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Description:与服务端的链接管理类
 * Created by Ming on 2019-6-1.
 */
public class MediaSessionConnection {
    private static Context mContext;
    private MediaBrowserCompat mediaBrowser;
    private boolean isConnected;
    private PlaybackStateCompat playbackState = EMPTY_PLAYBACK_STATE;
    private MediaMetadataCompat nowPlaying = NOTHING_PLAYING;
    private MediaControllerCompat.TransportControls transportControls;
    private MediaControllerCompat mediaController;
    private MediaBrowserConnectionCallback mediaBrowserConnectionCallback;
    private MediaControllerCallback mMediaControllerCallback;

    public static void initConnection(Context context) {
        mContext = context;
    }

    private static volatile MediaSessionConnection sInstance;

    public static MediaSessionConnection getInstance() {
        if (sInstance == null) {
            synchronized (MediaSessionConnection.class) {
                if (sInstance == null) {
                    sInstance = new MediaSessionConnection(new ComponentName(mContext, AudioService.class));
                }
            }
        }
        return sInstance;
    }

    private MediaSessionConnection(ComponentName componentName) {
        mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaControllerCallback = new MediaControllerCallback();
        mediaBrowser = new MediaBrowserCompat(mContext,componentName,mediaBrowserConnectionCallback,null);
    }

    /**
     * 是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取当前播放的 PlaybackStateCompat
     */
    public PlaybackStateCompat getPlaybackState() {
        return playbackState;
    }

    /**
     * 获取当前播放的 MediaMetadataCompat
     */
    public MediaMetadataCompat getNowPlaying() {
        return nowPlaying;
    }

    /**
     * 获取播放控制器
     */
    public MediaControllerCompat.TransportControls getTransportControls() {
        return transportControls;
    }

    public MediaControllerCompat getMediaController() {
        return mediaController;
    }

    /**
     * 连接
     */
    public void connect() {
        if (!isConnected) {
            //进程被异常杀死时，App 被外部链接唤起时，connect 状态为 CONNECT_STATE_CONNECTING，
            //导致崩溃，所以要先执行 disconnect
            disconnectImpl();
            mediaBrowser.connect();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnected) {
            disconnectImpl();
            isConnected = false;
        }
    }

    private void disconnectImpl() {
        if (mediaController != null) {
            mediaController.unregisterCallback(mMediaControllerCallback);
        }
        mediaBrowser.disconnect();
    }

    /**
     * 连接回调
     */
    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mediaController = new MediaControllerCompat(mContext,mediaBrowser.getSessionToken());
                mediaController.registerCallback(mMediaControllerCallback);
                transportControls = mediaController.getTransportControls();
                isConnected = true;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            isConnected = false;
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            disconnect();
            isConnected = false;
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            playbackState = state != null ? state : EMPTY_PLAYBACK_STATE;
            //状态监听
            CopyOnWriteArrayList<PlayerEventListener> mPlayerEventListeners = AudioManager.getInstance().getPlayerEventListeners();
            if (state != null) {
                for (PlayerEventListener listener : mPlayerEventListeners) {
                    switch (state.getState()) {
                        case PlaybackStateCompat.STATE_PLAYING: // 正在播放
                            listener.onPlayerStart();
                            break;
                        case PlaybackStateCompat.STATE_PAUSED: //已暂停
                            listener.onPlayerPause();
                            break;
                        case PlaybackStateCompat.STATE_STOPPED: //已停止
                            listener.onPlayerStop();
                            break;
                        case PlaybackStateCompat.STATE_ERROR: //发送错误
                            listener.onError(state.getErrorCode(), state.getErrorMessage().toString());
                            break;
                        case PlaybackStateCompat.STATE_NONE: //默认播放状态，表示尚未添加媒体，或者表示已重置且无内容可播放。
                            String audioId = nowPlaying.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                            AudioInfo audioInfo = AudioProvider.getInstance().getAudioInfo(audioId);
                            listener.onPlayerCompletion(audioInfo);
                            break;
                        case PlaybackStateCompat.STATE_BUFFERING: //当前正在缓存
                            listener.onBuffering();
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            mediaBrowserConnectionCallback.onConnectionSuspended();
        }
    }

    private static PlaybackStateCompat EMPTY_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build();

    private static MediaMetadataCompat NOTHING_PLAYING = new MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build();
}
