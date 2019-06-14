package com.mickey.mymvpapplication.audio;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

/**
 * Describe:
 * Created by Administrator on 2019/6/2.
 */
class ExoPlayback implements Playback {

    private static final String TAG = "ExoPlayback";

    public static final String ACTION_CHANGE_VOLUME = "ACTION_CHANGE_VOLUME";
    public static final String ACTION_DERAILLEUR = "ACTION_DERAILLEUR";

    public static final String ABR_ALGORITHM_DEFAULT = "default";
    public static final String ABR_ALGORITHM_RANDOM = "random";
    public static String abrAlgorithm = ABR_ALGORITHM_DEFAULT;

    public static final String EXTENSION_RENDERER_MODE_ON = "EXTENSION_RENDERER_MODE_ON";
    public static final String EXTENSION_RENDERER_MODE_OFF = "EXTENSION_RENDERER_MODE_OFF";
    public static String rendererMode = EXTENSION_RENDERER_MODE_OFF;

    private final Context mContext;
    private boolean mPlayOnFocusGain;
    private Callback mCallback;
    private final AudioProvider mMusicProvider;
    private String mCurrentMediaId;

    private SimpleExoPlayer mExoPlayer;
    private final ExoPlayerEventListener mEventListener = new ExoPlayerEventListener();

    private boolean mExoPlayerNullIsStopped = false;

    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    public ExoPlayback(Context context, AudioProvider musicProvider) {
        this.mContext = context.getApplicationContext();
        this.mMusicProvider = musicProvider;
        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
    }

    @Override
    public void stop() {
        releaseResources(true);
    }

    @Override
    public int getState() {
        if (mExoPlayer == null) {
            return mExoPlayerNullIsStopped ? PlaybackStateCompat.STATE_STOPPED : PlaybackStateCompat.STATE_NONE;
        }
        switch (mExoPlayer.getPlaybackState()) {
            case Player.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case Player.STATE_READY:
                return mExoPlayer.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_NONE;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getBufferedPosition() {
        return mExoPlayer != null ? mExoPlayer.getBufferedPosition() : 0;
    }

    @Override
    public void play(MediaSessionCompat.QueueItem item, boolean isPlayWhenReady) {
        mPlayOnFocusGain = true;
        String mediaId = item.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentMediaId = mediaId;
        }
        if (mediaHasChanged || mExoPlayer == null) {
            releaseResources(false); // release everything except the player
            MediaMetadataCompat track = mMusicProvider.getMusic(item.getDescription().getMediaId());

            String source = track.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
            if (TextUtils.isEmpty(source)) {
                return;
            }
            source = source.replaceAll(" ", "%20"); // Escape spaces for URLs
            //缓存歌曲
//            if (ExoDownload.getInstance().isOpenCache()) {
//                ExoDownload.getInstance().getDownloadTracker().toggleDownload(mediaId, Uri.parse(source), "");
//            }

            if (mExoPlayer == null) {
                //轨道选择
                TrackSelection.Factory trackSelectionFactory;
                if (abrAlgorithm.equals(ABR_ALGORITHM_DEFAULT)) {
                    trackSelectionFactory = new AdaptiveTrackSelection.Factory();
                } else {
                    trackSelectionFactory = new RandomTrackSelection.Factory();
                }
                //使用扩展渲染器的模式
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        rendererMode.equals(EXTENSION_RENDERER_MODE_ON) ?
                                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON :
                                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF; //不使用
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext, extensionRendererMode);

                //轨道选择
                trackSelector = new DefaultTrackSelector(trackSelectionFactory);
                trackSelector.setParameters(trackSelectorParameters);

                DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

                mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory,
                        trackSelector, drmSessionManager);

                mExoPlayer.addListener(mEventListener);
                mExoPlayer.addAnalyticsListener(new EventLogger(trackSelector));
            }

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes, true); //第二个参数能使ExoPlayer自动管理焦点


//            DataSource.Factory dataSourceFactory = ExoDownload.getInstance().buildDataSourceFactory(mContext);
//
//            MediaSource mediaSource = buildMediaSource(dataSourceFactory, Uri.parse(source), null);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext,mContext.getApplicationContext().getPackageName()));

            MediaSource mediaSource = new ExtractorMediaSource
                    .Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(source));

            mExoPlayer.prepare(mediaSource);
        }
        if (isPlayWhenReady) {
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void pause() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        releaseResources(false);
    }

    @Override
    public void seekTo(long position) {
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    /**
     * 快进
     */
    @Override
    public void onFastForward() {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = currSpeed + 0.5f;
            mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
        }
    }

    /**
     * 倒带
     */
    @Override
    public void onRewind() {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = currSpeed - 0.5f;
            if (newSpeed <= 0) {
                newSpeed = 0;
            }
            mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
        }
    }

    /**
     * 指定语速 refer 是否已当前速度为基数  multiple 倍率
     */
    @Override
    public void onDerailleur(boolean refer, float multiple) {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = refer ? currSpeed * multiple : multiple;
            if (newSpeed > 0) {
                mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
            }
        }
    }

    /**
     * 获取时长
     */
    @Override
    public long getDuration() {
        if (mExoPlayer != null) {
            return mExoPlayer.getDuration();
        } else {
            return -1;
        }
    }

    private void releaseResources(boolean releasePlayer) {
        if (releasePlayer && mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;
            mExoPlayerNullIsStopped = true;
            mPlayOnFocusGain = false;
        }
    }

    private final class ExoPlayerEventListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            // Nothing to do.
        }

        @Override
        public void onTracksChanged(
                TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Nothing to do.
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Nothing to do.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                case Player.STATE_BUFFERING:
                case Player.STATE_READY:
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
                case Player.STATE_ENDED:
                    if (mCallback != null) {
                        mCallback.onCompletion();
                    }
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;
            }

            if (mCallback != null) {
                mCallback.onError("ExoPlayer error " + what);
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            // Nothing to do.
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Nothing to do.
        }

        @Override
        public void onSeekProcessed() {
            // Nothing to do.
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            // Nothing to do.
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            // Nothing to do.
        }
    }
}
