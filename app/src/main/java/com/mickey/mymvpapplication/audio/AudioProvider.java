package com.mickey.mymvpapplication.audio;

import android.graphics.Bitmap;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Describe:媒体信息提供类
 * Created by Administrator on 2019/6/2.
 */
class AudioProvider {
    //使用Map在查找方面会效率高一点
    private LinkedHashMap<String, AudioInfo> mAudioInfoListById;
    private LinkedHashMap<String, MediaMetadataCompat> mMusicListById;

    public static AudioProvider getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final AudioProvider sInstance = new AudioProvider();
    }

    private AudioProvider() {
        mAudioInfoListById = new LinkedHashMap<>();
        mMusicListById = new LinkedHashMap<>();
    }

    /**
     * 获取List#AudioInfo
     */
    public List<AudioInfo> getAudioInfos() {
        return new ArrayList<>(mAudioInfoListById.values());
    }

    /**
     * 设置播放列表
     */
    public synchronized void setAudioInfos(List<AudioInfo> AudioInfos) {
        mAudioInfoListById.clear();
        for (AudioInfo info : AudioInfos) {
            mAudioInfoListById.put(info.audioId, info);
        }
        mMusicListById = toMediaMetadata(AudioInfos);
    }

    /**
     * 添加一首歌
     */
    public synchronized void addAudioInfo(AudioInfo AudioInfo) {
        mAudioInfoListById.put(AudioInfo.audioId, AudioInfo);
        mMusicListById.put(AudioInfo.audioId, toMediaMetadata(AudioInfo));
    }

    /**
     * 根据检查是否有某首音频
     */
    public boolean hasAudioInfo(String songId) {
        return mAudioInfoListById.containsKey(songId);
    }

    /**
     * 根据songId获取AudioInfo
     */
    public AudioInfo getAudioInfo(String songId) {
        if (mAudioInfoListById.containsKey(songId)) {
            return mAudioInfoListById.get(songId);
        } else {
            return null;
        }
    }

    /**
     * 根据songId获取索引
     */
    public int getIndexByAudioInfo(String songId) {
        AudioInfo AudioInfo = getAudioInfo(songId);
        return AudioInfo != null ? getAudioInfos().indexOf(AudioInfo) : -1;
    }

    /**
     * 获取List#MediaMetadataCompat
     */
    public List<MediaMetadataCompat> getMusicList() {
        return new ArrayList<>(mMusicListById.values());
    }

    /**
     * 获取 List#MediaBrowserCompat.MediaItem 用于 onLoadChildren 回调
     */
    public List<MediaBrowserCompat.MediaItem> getChildrenResult() {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();
        List<MediaMetadataCompat> list = new ArrayList<>(mMusicListById.values());
        for (MediaMetadataCompat metadata : list) {
            MediaBrowserCompat.MediaItem mediaItem = new MediaBrowserCompat.MediaItem(
                    metadata.getDescription(),
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    /**
     * 获取乱序列表
     */
    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        shuffled.addAll(mMusicListById.values());
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * 根据id获取对应的MediaMetadataCompat对象
     */
    public MediaMetadataCompat getMusic(String songId) {
        return mMusicListById.containsKey(songId) ? mMusicListById.get(songId) : null;
    }

    /**
     * 更新封面art
     */
    public synchronized void updateMusicArt(String songId, MediaMetadataCompat changeData, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder(changeData)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)
                .build();
        mMusicListById.put(songId, metadata);
    }

    /**
     * List<AudioInfo> 转 ConcurrentMap<String, MediaMetadataCompat>
     */
    private synchronized static LinkedHashMap<String, MediaMetadataCompat> toMediaMetadata(List<AudioInfo> AudioInfos) {
        LinkedHashMap<String, MediaMetadataCompat> map = new LinkedHashMap<>();
        for (AudioInfo info : AudioInfos) {
            MediaMetadataCompat metadataCompat = toMediaMetadata(info);
            map.put(info.audioId, metadataCompat);
        }
        return map;
    }

    /**
     * AudioInfo 转 MediaMetadataCompat
     */
    private synchronized static MediaMetadataCompat toMediaMetadata(AudioInfo info) {
        String audioTitle = info.audioName;
        String audioCover = info.audioCover;
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, info.audioId);
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, info.audioUrl);

        if (info.duration != -1) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.duration);
        }
        if (!TextUtils.isEmpty(audioCover)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, audioCover);
        }
        if (!TextUtils.isEmpty(audioTitle)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, audioTitle);
        }
        return builder.build();
    }
}
