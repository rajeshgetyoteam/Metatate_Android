package com.getyoteam.budamind.testaudioexohls

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.MyApplication
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util


class AudioService : Service(), Player.EventListener {

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
    }

    override fun onSeekProcessed() {
    }

    override fun onTracksChanged(
        trackGroups: TrackGroupArray?,
        trackSelections: TrackSelectionArray?
    ) {
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
    }

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == ExoPlayer.STATE_READY) {
            val intent = Intent("com.example.exoplayer.PLAYER_STATUS")
            if (playWhenReady) {
                intent.putExtra("state", PlaybackStateCompat.STATE_PLAYING)
                intent.putExtra("time", player!!.currentPosition)
            } else {
                intent.putExtra("state", PlaybackStateCompat.STATE_PAUSED)
                intent.putExtra("time", player!!.currentPosition)
            }
            broadcaster?.sendBroadcast(intent)
        } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
            val intent = Intent("com.example.exoplayer.PLAYER_STATUS")
            intent.putExtra("state", PlaybackStateCompat.STATE_BUFFERING)
            broadcaster?.sendBroadcast(intent)
        } else if (playbackState == ExoPlayer.STATE_ENDED) {
            MyApplication.prefs!!.prevChapter = ""
            val intent = Intent("com.example.exoplayer.PLAYER_STATUS")
            intent.putExtra("state", PlaybackStateCompat.STATE_STOPPED)
            intent.putExtra("player_state", ExoPlayer.STATE_ENDED)
            intent.putExtra("time", player!!.currentPosition)
            broadcaster?.sendBroadcast(intent)
        } else if (player?.playbackState == ExoPlayer.STATE_IDLE) {
            MyApplication.prefs!!.prevChapter = ""
            val intent = Intent("com.example.exoplayer.PLAYER_STATUS")
            intent.putExtra("state", PlaybackStateCompat.STATE_STOPPED)
            intent.putExtra("player_state", ExoPlayer.STATE_IDLE)
            intent.putExtra("time", player!!.currentPosition)
            broadcaster?.sendBroadcast(intent)
        }
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return VideoServiceBinder()
    }

    var player: SimpleExoPlayer? = null
    lateinit var context: Context
    private var playerNotificationManager: PlayerNotificationManager? = null

    private lateinit var mediaSession: MediaSessionCompat;
    private var broadcaster: LocalBroadcastManager? = null

    private var mediaSessionConnector: MediaSessionConnector?=null

    inner class VideoServiceBinder : Binder() {
        fun getExoPlayerInstance() = player
        fun getService(): AudioService {
            return this@AudioService
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        val adaptiveTrackSelection = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        player = ExoPlayerFactory.newSimpleInstance(
            DefaultRenderersFactory(this),
            DefaultTrackSelector(adaptiveTrackSelection),
            DefaultLoadControl()
        )

        player!!.addListener(this)
        broadcaster = LocalBroadcastManager.getInstance(this)

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter)
        val hlsUrl = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_1MG.mp3"
    }

    public fun play(
        chapterUrl: String,
        courseName: String?,
        chapterName: String?,
        downloadFileModelOld: DownloadFileModel?,
        chapterId: String,
        isRewarded: Boolean?
    ) {

        val prevUrl = MyApplication.prefs!!.prevChapter
        val uri = Uri.parse(chapterUrl)
        val largeIcon = BitmapFactory.decodeResource(
            resources,
            com.getyoteam.budamind.R.drawable.noti_icon
        )

        if (downloadFileModelOld != null) {
            val localPath =
                downloadFileModelOld.getAudioFilePath() + downloadFileModelOld.getFileName()
            if (!prevUrl.equals(localPath)) {
                val mediaUri =
                    Uri.parse(localPath)
                val mediaSource = buildLocalMediaSource(mediaUri)
                player?.prepare(mediaSource)
                player?.setPlayWhenReady(true)
                MyApplication.prefs!!.prevChapter = localPath
                if (playerNotificationManager != null)
                    playerNotificationManager?.setPlayer(null)
                if (mediaSessionConnector != null)
                    mediaSessionConnector?.setPlayer(null, null)
            }
        } else {
            if (!prevUrl.equals(chapterUrl)) {
                val mediaSource = buildMediaSource(uri);
                player?.prepare(mediaSource)
                player?.setPlayWhenReady(true)
                MyApplication.prefs!!.prevChapter = chapterUrl
                if (playerNotificationManager != null)
                    playerNotificationManager?.setPlayer(null)
                if (mediaSessionConnector != null)
                    mediaSessionConnector?.setPlayer(null, null)
            }
        }
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
            this,
            chapterId,
            R.string.exo_download_notification_channel_name,
            1,
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    val intent = Intent(context, PlayerExoActivity::class.java)
                    return PendingIntent.getActivity(
                        context,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return "Day " + chapterName
                }

                override fun getCurrentContentTitle(player: Player?): String {
                    return courseName!!
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {
                    return largeIcon
                }
            }
        )

        playerNotificationManager?.setSmallIcon(com.getyoteam.budamind.R.drawable.noti_icon)
        playerNotificationManager?.setUseNavigationActions(false)
        playerNotificationManager?.setFastForwardIncrementMs(0)
        playerNotificationManager?.setRewindIncrementMs(0)
        playerNotificationManager?.setColor(ContextCompat.getColor(context,com.getyoteam.budamind.R.color.color_blue))
        playerNotificationManager?.setColorized(true)
        playerNotificationManager?.setNotificationListener(object :
            PlayerNotificationManager.NotificationListener {
            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                startForeground(notificationId, notification)
            }
        })

        if (isRewarded!!) {
            playerNotificationManager?.setPlayer(player)
            mediaSession = MediaSessionCompat(context, "MEDIA SESSION_TAG")
            mediaSession.isActive = true
            playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector?.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player?,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    return MediaDescriptionCompat.Builder().setMediaId("6578123")
                        .setTitle(courseName).build()
                }
            })

            mediaSessionConnector?.setPlayer(player, null)

        } else {
            playerNotificationManager?.setPlayer(player)
            mediaSession = MediaSessionCompat(context, "MEDIA SESSION_TAG")
            mediaSession.isActive = true
            playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector?.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
                override fun getMediaDescription(
                    player: Player?,
                    windowIndex: Int
                ): MediaDescriptionCompat {

                    val extras = Bundle()
                    extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, -1)
                    return MediaDescriptionCompat.Builder().setMediaId("6578123")
                        .setExtras(extras)
                        .setTitle(courseName).build()
                }
            })
            playerNotificationManager?.setUseNavigationActions(false);
            mediaSessionConnector?.setPlayer(player, null)
        }


    }

    private class ActionReceiver : PlayerNotificationManager.CustomActionReceiver {
        override fun getCustomActions(player: Player?): MutableList<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun createCustomActions(context: Context?): MutableMap<String, NotificationCompat.Action> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onCustomAction(player: Player?, action: String?, intent: Intent?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private fun buildLocalMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource(
            uri,
            DefaultDataSourceFactory(this, "ua"),
            DefaultExtractorsFactory(), null, null
        );
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ExtractorMediaSource.Factory(
            DefaultHttpDataSourceFactory("exoplayer-codelab")
        ).createMediaSource(uri)
    }

    override fun onDestroy() {
        mediaSession.release()
        mediaSessionConnector?.setPlayer(null, null)
        playerNotificationManager?.setPlayer(null)
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
