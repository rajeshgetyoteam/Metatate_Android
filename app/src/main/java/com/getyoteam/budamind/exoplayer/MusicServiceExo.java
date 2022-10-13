/*
package com.getyoteam.budamind.exoplayer;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.support.v4.media.session.PlaybackStateCompat;

import com.getyoteam.budamind.R;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class MusicServiceExo extends Service {

    private static String TAG = "MusicService";

    private SimpleExoPlayer player;
    private LocalBroadcastManager broadcaster;
    private final Binder mBinder = new MusicBinder();
    private PlayerNotificationManager playerNotificationManager;
    private MediaSource mediaSource;

    @Override
    public void onCreate() {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        broadcaster = LocalBroadcastManager.getInstance(this);

        player.addListener(new SimpleExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Intent intent = new Intent("com.example.exoplayer.PLAYER_STATUS");
                    intent.putExtra("state", PlaybackStateCompat.STATE_BUFFERING);
                    broadcaster.sendBroadcast(intent);
                } else if (playbackState == ExoPlayer.STATE_READY) {
                    Intent intent = new Intent("com.example.exoplayer.PLAYER_STATUS");
                    if (playWhenReady) {
                        intent.putExtra("state", PlaybackStateCompat.STATE_PLAYING);
                        intent.putExtra("time",player.getCurrentPosition());
                    } else {
                        intent.putExtra("state", PlaybackStateCompat.STATE_PAUSED);
                        intent.putExtra("time",player.getCurrentPosition());
                    }
                    broadcaster.sendBroadcast(intent);
                }else if (playbackState == ExoPlayer.STATE_ENDED) {
                    Intent intent = new Intent("com.example.exoplayer.PLAYER_STATUS");
                    intent.putExtra("state", PlaybackStateCompat.STATE_STOPPED);
                    intent.putExtra("time",player.getCurrentPosition());
                    broadcaster.sendBroadcast(intent);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
        createNotification();

        super.onCreate();
    }

    public void createNotification() {
        Bitmap largeIcon = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.logo_splash
        );
        playerNotificationManager = new PlayerNotificationManager(
                this,
                "radio_playback_channel",
                100, new DescriptionAdapter(this));
        playerNotificationManager.setPlayer(player);
        // omit skip previous and next actions
        playerNotificationManager.setUseNavigationActions(false);
        // omit fast forward action by setting the increment to zero
        playerNotificationManager.setFastForwardIncrementMs(0);
        // omit rewind action by setting the increment to zero
        playerNotificationManager.setRewindIncrementMs(0);
        // omit the stop action
        playerNotificationManager.setStopAction(null);

        playerNotificationManager.setColor(Color.BLACK);
        playerNotificationManager.setColorized(true);
        playerNotificationManager.setUseChronometer(true);
        playerNotificationManager.setSmallIcon(R.drawable.noti_icon);
        playerNotificationManager.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);
    }

    public class MusicBinder extends Binder {
        public MusicServiceExo getService() {
            return MusicServiceExo.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        playerNotificationManager.setPlayer(null);
        return super.onUnbind(intent);
    }

    public void play(String channelUrl) {
        if(player==null){
            onCreate();
        }
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), "ExoPlayerDemo");
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        Handler mainHandler = new Handler();
        if (mediaSource == null) {
            mediaSource = new ExtractorMediaSource(Uri.parse(channelUrl), dataSourceFactory, extractorsFactory, mainHandler, null);
            player.prepare(mediaSource);
        }
        player.setPlayWhenReady(true);
    }

    public void seekTo(final int position) {
        if (player != null) {
            player.seekTo(position);
            player.setPlayWhenReady(true);
        }
    }

    public SimpleExoPlayer getPlayer() {
        if (player != null) {
            return player;
        }
        return null;
    }


    public void stop() {
        player.setPlayWhenReady(false);
    }

    public boolean isPlaying() {
        return player != null
                && player.getPlaybackState() != Player.STATE_ENDED
                && player.getPlaybackState() != Player.STATE_IDLE
                && player.getPlayWhenReady();
    }

    @Override
    public void onDestroy() {
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;
        super.onDestroy();
    }
}
*/
