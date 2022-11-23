package com.getyoteam.budamind.testaudioexohls

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util


class AudioPlayerService : Service(), Player.Listener {

    override fun onLoadingChanged(isLoading: Boolean) {
    }

    override fun onPositionDiscontinuity(reason: Int) {
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
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

    var player: ExoPlayer? = null
    lateinit var context: Context
    private var playerNotificationManager: PlayerNotificationManager? = null

    private lateinit var mediaSession: MediaSessionCompat;
    private var broadcaster: LocalBroadcastManager? = null

    private var mediaSessionConnector: MediaSessionConnector?=null

    inner class VideoServiceBinder : Binder() {
        fun getExoPlayerInstance() = player
        fun getService(): AudioPlayerService {
            return this@AudioPlayerService
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = this

        player = ExoPlayer.Builder(context).build()

        player!!.addListener(this)
        broadcaster = LocalBroadcastManager.getInstance(this)

        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory =
            DefaultDataSourceFactory(this, Util.getUserAgent(this, "Exo2"), defaultBandwidthMeter)
        val hlsUrl = "https://file-examples.com/wp-content/uploads/2017/11/file_example_MP3_1MG.mp3"
    }

    fun play(
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
//                val mediaUri = Uri.parse(localPath)
                val defaultDataSourceFactory = DefaultHttpDataSource.Factory()

                val concatenatingMediaSource = ConcatenatingMediaSource()

                val progressiveMediaSource =
                    ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(localPath))
                concatenatingMediaSource.addMediaSource(progressiveMediaSource)

                player?.let {
                    it.addListener(this)
                    it.setMediaSource(concatenatingMediaSource)
                    it.prepare()
                    it.playWhenReady = true
                }
                MyApplication.prefs!!.prevChapter = localPath
                if (playerNotificationManager != null)
                    playerNotificationManager?.setPlayer(null)
                if (mediaSessionConnector != null)
                    mediaSessionConnector?.setPlayer(null)
            }
        } else {
            if (!prevUrl.equals(chapterUrl)) {
                val defaultDataSourceFactory = DefaultHttpDataSource.Factory()

                val concatenatingMediaSource = ConcatenatingMediaSource()

                val progressiveMediaSource = ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(chapterUrl))
                concatenatingMediaSource.addMediaSource(progressiveMediaSource)

                player?.let {
                    it.addListener(this)
                    it.setMediaSource(concatenatingMediaSource)
                    it.prepare()
                    it.playWhenReady = true
                }

                MyApplication.prefs!!.prevChapter = chapterUrl
                if (playerNotificationManager != null)
                    playerNotificationManager?.setPlayer(null)
                if (mediaSessionConnector != null)
                    mediaSessionConnector?.setPlayer(null)
            }
        }

//        try {
//            val intent = Intent(context, PlayerExoActivity::class.java)
//            val pIntent = PendingIntent.getActivity(
//                context,
//                1,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )

//            playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
//                this,
//                chapterId,
//                R.string.exo_download_notification_channel_name,
//                1,
//                object : PlayerNotificationManager.MediaDescriptionAdapter {
//                    override fun createCurrentContentIntent(player: Player?): PendingIntent? {
//                        // Create the TaskStackBuilder and add the intent, which inflates the back stack
//                        val intent = Intent(context, PlayerExoActivity::class.java)
////                        return  PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//                        return  PendingIntent.getActivity(context, 1, intent, 0 or PendingIntent.FLAG_IMMUTABLE)
//
//                    }
//
//                    override fun getCurrentContentText(player: Player?): String? {
//                        return "Day " + chapterName
//                    }
//
//                    override fun getCurrentContentTitle(player: Player?): String {
//                        return courseName!!
//                    }
//
//                    override fun getCurrentLargeIcon(
//                        player: Player?,
//                        callback: PlayerNotificationManager.BitmapCallback?
//                    ): Bitmap? {
//                        return largeIcon
//                    }
//                }
//            )

//        }catch (e :Exception){
//            e.printStackTrace()
//        }

//         try {
        playerNotificationManager = PlayerNotificationManager.Builder(
            context,
            1, "playback_channel"
        ).setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelDescriptionResourceId(R.string.playback_channel_description)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(context, PlayerExoActivity::class.java)
                    return PendingIntent.getActivity(
                        context,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence {
                    return courseName!!
//                         return "Title"
                }

                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return "Day " + chapterName
//                         return "Day " + "1"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {

                    val icon = BitmapFactory.decodeResource(
                        context.resources,
                        com.getyoteam.budamind.R.drawable.noti_icon
                    )

                    return  icon
                }
            })
            .setNotificationListener(playerNotificationListener)
            .build()
//         }catch (e :Exception){
//             e.printStackTrace()
//         }



        playerNotificationManager?.setPlayer(player)
        playerNotificationManager?.setSmallIcon(com.getyoteam.budamind.R.drawable.noti_icon)
        playerNotificationManager?.setColor(ContextCompat.getColor(context,com.getyoteam.budamind.R.color.color_blue))
        playerNotificationManager?.setColorized(true)

        if (isRewarded!!) {
            playerNotificationManager?.setPlayer(player)
            mediaSession = MediaSessionCompat(context, "MEDIA SESSION_TAG")
            mediaSession.isActive = true
            playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector?.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    return MediaDescriptionCompat.Builder().setMediaId("6578123").setTitle(courseName).build()
                }
            })

            mediaSessionConnector?.setPlayer(player)

        } else {
            playerNotificationManager?.setPlayer(player)
            mediaSession = MediaSessionCompat(context, "MEDIA SESSION_TAG")
            mediaSession.isActive = true
            playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
            mediaSessionConnector = MediaSessionConnector(mediaSession)
            mediaSessionConnector?.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {

                override fun getMediaDescription(
                    player: Player,
                    windowIndex: Int
                ): MediaDescriptionCompat {
                    val extras = Bundle()
                    extras.putInt(MediaMetadataCompat.METADATA_KEY_DURATION, -1)
                    return MediaDescriptionCompat.Builder().setMediaId("6578123")
                        .setExtras(extras)
                        .setTitle(courseName).build()
                }
            })
            mediaSessionConnector?.setPlayer(player)
        }


    }


//    private fun buildLocalMediaSource(uri: Uri): MediaSource {
//        return MediaSource(
//            uri,
//            DefaultDataSourceFactory(this, "ua"),
//            DefaultExtractorsFactory(), null, null)
//    }
//
//    private fun buildMediaSource(uri: Uri): MediaSource {
//        return Factory(
//            DefaultDataSourceFactory(this,"exoplayer-codelab")
//        ).createMediaSource(uri)
//    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
//        try {
//            mediaSession.release()
//            mediaSessionConnector?.setPlayer(null)
//            playerNotificationManager?.setPlayer(null)
//            player?.release()
//            player = null
//        }catch (e :Exception){
//            e.printStackTrace()
//        }

        Log.d("@@@","onDestroy")
        try {
            player?.let {
                playerNotificationManager?.let {
                    it.setPlayer(null)
                    playerNotificationManager = null
                }
                it.removeListener(this)
                it.release()
                player = null
            }
        }catch (e :Exception){
            e.printStackTrace()
        }

        super.onDestroy()
    }

    //    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return START_STICKY
//    }
    fun getBitmap(context: Context, @DrawableRes bitmapResource: Int): Bitmap? {
        return (context.getResources().getDrawable(bitmapResource) as BitmapDrawable).bitmap
    }

    private var playerNotificationListener: PlayerNotificationManager.NotificationListener =
        object : PlayerNotificationManager.NotificationListener {

            override fun onNotificationCancelled(
                notificationId: Int,
                dismissedByUser: Boolean
            ) {
                stopSelf()
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                if (ongoing) {
                    startForeground(notificationId, notification)
                } else {
                    stopForeground(false)
                }

            }
        }
}