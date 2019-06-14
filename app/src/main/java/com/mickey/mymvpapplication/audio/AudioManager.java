package com.mickey.mymvpapplication.audio;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;

import java.util.List;
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
        MediaSessionConnection.initConnection(mContext);
    }

    private AudioManager(){
    }

    /**
     * 释放资源，关闭程序时调用
     */
    public void onRelease(){
        clearPlayerEventListener();
        mContext = null;
        mPlayback = null;
    }

    public Playback getPlayback() {
        return mPlayback;
    }

    public void setPlayback(Playback playback) {
        mPlayback = playback;
    }

    /**
     * 播放，实际也是根据 audioId 播放
     */
    public void playMusicByInfo(List<AudioInfo> info) {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            AudioProvider.getInstance().setAudioInfos(info);
            MediaControllerCompat.TransportControls transportControls = connection.getTransportControls();
            transportControls.playFromMediaId(info.get(0).audioId, null);
        }
    }

    /**
     * 暂停
     */
    public void pauseMusic() {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().pause();
        }
    }

    /**
     * 恢复播放
     */
    public void playMusic() {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().play();
        }
    }

    /**
     * 停止播放
     */
    public void stopMusic() {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().stop();
        }
    }

    /**
     * 准备播放，根据songId
     */
    public void prepareFromSongId(String songId) {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().prepareFromMediaId(songId, null);
        }
    }

    /**
     * 开始快进，每调一次加 0.5 倍
     */
    public void fastForward() {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().fastForward();
        }
    }

    /**
     * 开始倒带 每调一次减 0.5 倍，最小为 0
     */
    public void rewind() {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().rewind();
        }
    }

    /**
     * 移动到媒体流中的新位置,以毫秒为单位。
     */
    public void seekTo(long pos) {
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            connection.getTransportControls().seekTo(pos);
        }
    }

    /**
     * 获取当前播放的歌曲信息
     */
    public AudioInfo getNowPlayingSongInfo() {
        AudioInfo songInfo = null;
        MediaSessionConnection connection = MediaSessionConnection.getInstance();
        if (connection.isConnected()) {
            MediaMetadataCompat metadataCompat = connection.getNowPlaying();
            if (metadataCompat != null) {
                String songId = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                songInfo = AudioProvider.getInstance().getAudioInfo(songId);
                //播放列表改变了或者清空了，如果还在播放歌曲，这时候 getSongInfo 就会获取不到，
                //此时需要从 metadataCompat 中获取
                if (songInfo == null && !TextUtils.isEmpty(songId)) {
                    songInfo = getAudioInfoFromMediaMetadata(metadataCompat);
                }
            }
        }
        return songInfo;
    }

    private AudioInfo getAudioInfoFromMediaMetadata(MediaMetadataCompat metadata) {
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.audioId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
        audioInfo.audioUrl = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
        audioInfo.duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        audioInfo.audioCover = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);
        audioInfo.audioName = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
        return audioInfo;
    }

    /**
     * 添加一个状态监听
     */
    public void addPlayerEventListener(PlayerEventListener listener) {
        if (listener != null) {
            if (!mPlayerEventListeners.contains(listener)) {
                mPlayerEventListeners.add(listener);
            }
        }
    }

    /**
     * 删除一个状态监听
     */
    public void removePlayerEventListener(PlayerEventListener listener) {
        if (listener != null) {
            mPlayerEventListeners.remove(listener);
        }
    }

    /**
     * 删除所有状态监听
     */
    public void clearPlayerEventListener() {
        mPlayerEventListeners.clear();
    }

    public CopyOnWriteArrayList<PlayerEventListener> getPlayerEventListeners() {
        return mPlayerEventListeners;
    }
}
