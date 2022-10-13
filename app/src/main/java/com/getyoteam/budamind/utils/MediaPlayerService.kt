package com.getyoteam.budamind.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.session.MediaSessionManager
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.Model.SoundListModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import java.io.IOException
import java.util.*

class MediaPlayerService : Service(), MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {

    private val CHANNEL_ID = "action.CHANNEL_ID"
    val NOTIFICATION_ID = 2000
    private var mNotificationManager: NotificationManager?=null
//    private var mNotificationBuilder: NotificationCompat.Builder? = null

    private var stopReloading: Boolean = true
    private var isFirstTime: Boolean = false
    private lateinit var db: AppDatabase
    private var isClickedFromApp: Boolean? = true
    private var isPlaying: Boolean? = false
    private var mediaPlayer: MediaPlayer? = null
    val Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio"
    val BROADCAST_ACTION = "com.clarity.meditation.utils.displayevent";

    //MediaSession
    private var mediaSessionManager: MediaSessionManager? = null
    private var mediaSession: MediaSessionCompat? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    var intent: Intent? = null
    val handler = Handler();
    //Used to pause/resume MediaPlayer
    private var resumePosition: Int = 0

    //AudioFocus
    private var audioManager: AudioManager? = null

    // Binder given to clients
    private val iBinder = LocalBinder()

    //List of available Audio files
    private var audioList: ArrayList<SoundListModel>? = null
    private var audioIndex = -1
    private var activeAudio: SoundListModel? = null //an object on the currently playing audio


    //Handle incoming phone calls
    private var ongoingCall = false
    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null

    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia()
            buildNotification(PlaybackStatus.PAUSED)
        }
    }


    /**
     * Play new Audio
     */
    private val playNewAudio = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            //Get the new media index form SharedPreferences
            audioIndex = StorageUtil(applicationContext).loadAudioIndex()
            if (audioIndex != -1 && audioIndex < audioList!!.size) {
                //index is in a valid range
                activeAudio = audioList!![audioIndex]
            } else {
                stopSelf()
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopReloading=true
            if (mediaPlayer!!.isPlaying) {
                MyApplication.prefs!!.isPlaying = false
                isPlaying = false
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
            }
            initMediaPlayer()
            updateMetaData()
            buildNotification(PlaybackStatus.PLAYING)
        }
    }


    /**
     * Service lifecycle methods
     */
    override fun onBind(intent: Intent): IBinder? {
        return iBinder
    }

    override fun onCreate() {
        super.onCreate()
        // Perform one-time setup procedures
        intent = Intent(BROADCAST_ACTION);
        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.

        try {
            registerReceiver(
                broadcastReceiver,
                IntentFilter("com.clarity.meditation.utils.displayevent")
            )
        }catch (e : Exception){
            e.printStackTrace()
        }

        callStateListener()
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver()
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio()
    }


    //The system calls this method when an activity, requests the service be started
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        try {

            //Load data from SharedPreferences
            val storage = StorageUtil(applicationContext)
            audioList = storage.loadAudio()
            audioIndex = storage.loadAudioIndex()

            if (audioIndex != -1 && audioIndex < audioList!!.size) {
                //index is in a valid range
                activeAudio = audioList!![audioIndex]
            } else {
                stopSelf()
            }
        } catch (e: NullPointerException) {
            stopSelf()
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf()
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession()
                mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                initMediaPlayer()
            } catch (e: RemoteException) {
                e.printStackTrace()
                stopSelf()
            }

            buildNotification(PlaybackStatus.PLAYING)
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent)
        return START_NOT_STICKY
    }

    val sendUpdatesToUI = object : Runnable {
        override fun run() {
            DisplayLoggingInfo();
        }
    }



    fun DisplayLoggingInfo() {
        intent!!.putExtra("isPlaying", isPlaying);
        intent!!.putExtra("isFirstTime", isFirstTime);
        sendBroadcast(intent);
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateUI(intent!!);
        }
    }

    fun updateUI(intent: Intent) {
        isPlaying = intent.getBooleanExtra("isPlaying", false)
        isClickedFromApp = false
        if (isPlaying!!) {
            resumeMedia()
            buildNotification(PlaybackStatus.PLAYING)
        } else {
            if(stopReloading) {
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }
        }
    }

    override fun onUnbind(intent: Intent): Boolean {
        mediaSession!!.release()
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        handler.removeCallbacks(sendUpdatesToUI)
        super.onDestroy()
        MyApplication.prefs!!.prevIndexSound = -1
        if (mediaPlayer != null) {
            stopMedia()
            mediaPlayer!!.release()
        }
        removeAudioFocus()
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager!!.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        }

        removeNotification()

        //unregister BroadcastReceivers
        unregisterReceiver(becomingNoisyReceiver)
        unregisterReceiver(playNewAudio)
        Log.e("@@@", "Destroyed......####")
        //clear cached playlist
        StorageUtil(applicationContext).clearCachedAudioPlaylist()
    }

    /**
     * Service Binder
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        val service: MediaPlayerService
            get() = this@MediaPlayerService
    }


    /**
     * MediaPlayer callback methods
     */
    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    override fun onCompletion(mp: MediaPlayer) {
        //Invoked when playback of a media source has completed.
        stopMedia()

        removeNotification()
        //stop the service
        stopSelf()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked when there has been an error during an asynchronous operation
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra"
            )
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR SERVER DIED $extra"
            )
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d(
                "MediaPlayer Error",
                "MEDIA ERROR UNKNOWN $extra"
            )
        }
        return false
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        //Invoked to communicate some info
        return false
    }

    override fun onPrepared(mp: MediaPlayer) {
        //Invoked when the media source is ready for playback.
        playMedia()
        isPlaying = true
        isFirstTime = false
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 10); // 1 second
    }

    override fun onSeekComplete(mp: MediaPlayer) {
        //Invoked indicating the completion of a seek operation.
    }

    override fun onAudioFocusChange(focusState: Int) {

        //Invoked when the audio focus of the system is updated.
        when (focusState) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // resume playback
                if (mediaPlayer == null)
                    initMediaPlayer()
                else if (!mediaPlayer!!.isPlaying) mediaPlayer!!.start()
//                mediaPlayer!!.setVolume(1.0f, 1.0f)
            }
        }
    }


    /**
     * AudioFocus
     */
    private fun requestAudioFocus(): Boolean {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager!!.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
        return if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            true
        } else false
        //Could not gain focus
    }

    private fun removeAudioFocus(): Boolean {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager!!.abandonAudioFocus(this)
    }


    /**
     * MediaPlayer actions
     */
    private fun initMediaPlayer() {
        db = AppDatabase.getDatabase(applicationContext)
        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()//new MediaPlayer instance

        val audioPath = activeAudio!!.getAudio()
        var downloadFileModelOld: DownloadFileModel? = null
        val filename: String
        if (audioPath != null) {
            filename = audioPath.substring(audioPath.lastIndexOf('/') + 1)
            downloadFileModelOld = db.downloadDao().loadDownloadFile(filename)
        }

        //Set up MediaPlayer event listeners
        mediaPlayer!!.setOnCompletionListener(this)
        mediaPlayer!!.setOnErrorListener(this)
        mediaPlayer!!.setOnPreparedListener(this)
        mediaPlayer!!.setOnBufferingUpdateListener(this)
        mediaPlayer!!.setOnSeekCompleteListener(this)
        mediaPlayer!!.setOnInfoListener(this)
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer!!.reset()
        mediaPlayer!!.isLooping = true

        try {
            if (downloadFileModelOld != null) {
                mediaPlayer!!.setDataSource(
                    applicationContext,
                    Uri.parse("file://" + downloadFileModelOld.getAudioFilePath() + downloadFileModelOld.getFileName())
                )
            } else {
                mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
                // Set the data source to the mediaFile location
                mediaPlayer!!.setDataSource(activeAudio!!.getAudio())
            }
        } catch (e: IOException) {
            e.printStackTrace()
            stopSelf()
        }


        mediaPlayer!!.prepareAsync()
    }

    private fun playMedia() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
            MyApplication.prefs!!.isPlaying = true
            MyApplication.prefs!!.isItemClicked = false
        }
    }

     fun stopMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            MyApplication.prefs!!.isPlaying = false
            MyApplication.prefs!!.isItemClicked = false
            isPlaying = false
            isFirstTime = false
            mediaPlayer!!.pause()
            stopReloading=false
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 0); // 1 second
        }
    }

     fun pauseMedia() {
        if (mediaPlayer == null) return
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
            MyApplication.prefs!!.isPlaying = false
            MyApplication.prefs!!.isItemClicked = false
            resumePosition = mediaPlayer!!.currentPosition
            isPlaying = false
            isFirstTime = false
            if (isClickedFromApp!!) {
                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 0); // 1 second
            }
        }
    }

    private fun resumeMedia() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.seekTo(resumePosition)
            mediaPlayer!!.start()
            MyApplication.prefs!!.isPlaying = true
            MyApplication.prefs!!.isItemClicked = false
            isPlaying = true
            isFirstTime = false
            if (isClickedFromApp!!) {
                handler.removeCallbacks(sendUpdatesToUI);
                handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
            }
        }
    }

    private fun registerBecomingNoisyReceiver() {
        //register after getting audio focus
        val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        registerReceiver(becomingNoisyReceiver, intentFilter)
    }

    /**
     * Handle PhoneState changes
     */
    private fun callStateListener() {
        // Get the telephony manager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        //Starting listening for PhoneState changes
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                when (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    TelephonyManager.CALL_STATE_OFFHOOK, TelephonyManager.CALL_STATE_RINGING -> if (mediaPlayer != null) {
                        pauseMedia()
                        ongoingCall = true
                    }
                    TelephonyManager.CALL_STATE_IDLE ->
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false
                                resumeMedia()
                            }
                        }
                }
            }
        }
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager!!.listen(
            phoneStateListener,
            PhoneStateListener.LISTEN_CALL_STATE
        )
    }

    /**
     * MediaSession and Notification actions
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Throws(RemoteException::class)
    private fun initMediaSession() {
        if (mediaSessionManager != null) return  //mediaSessionManager exists

        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager?
        // Create a new MediaSession
        mediaSession = MediaSessionCompat(applicationContext, "AudioPlayer")
        //Get MediaSessions transport controls
        transportControls = mediaSession!!.controller.transportControls
        //set MediaSession -> ready to receive media commands
        mediaSession!!.isActive = true
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

        //Set mediaSession's MetaData
        updateMetaData()

        // Attach Callback to receive MediaSession updates
        mediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
            // Implement callbacks
            override fun onPlay() {
                super.onPlay()
                isClickedFromApp = true
                resumeMedia()
                buildNotification(PlaybackStatus.PLAYING)
            }

            override fun onPause() {
                super.onPause()
                isClickedFromApp = true
                pauseMedia()
                buildNotification(PlaybackStatus.PAUSED)
            }

            override fun onStop() {
                super.onStop()
                stopMedia()
                removeNotification()
                //Stop the service
                stopSelf()
            }

        })
    }

    private fun updateMetaData() {
        val albumArt = BitmapFactory.decodeResource(
            resources,
            R.drawable.noti_icon
        ) //replace with medias albumArt
        // Update the current metadata
        mediaSession!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio!!.getSubtitle())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio!!.getTitle())
                .build()
        )
    }

    private fun buildNotification(playbackStatus: PlaybackStatus) {

        /**
         * Notification actions -> playbackAction()
         * 0 -> Play
         * 1 -> Pause
         * 2 -> Next track
         * 3 -> Previous track
         */

        var notificationAction = R.drawable.ic_pause_white//needs to be initialized
        val stopNotificationAction = R.drawable.ic_stop//needs to be initialized
        var play_pauseAction: PendingIntent? = null
        val closeAction = playbackAction(4)


        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus === PlaybackStatus.PLAYING) {
            notificationAction = R.drawable.ic_pause_white
            //create the pause action
            play_pauseAction = playbackAction(1)
        } else if (playbackStatus === PlaybackStatus.PAUSED) {
            notificationAction = R.drawable.ic_play_white
            //create the play action
            play_pauseAction = playbackAction(0)
        }

        val largeIcon = BitmapFactory.decodeResource(
            resources,
            R.drawable.noti_icon
        ) //replace with your own image

        // Create a new Notification
        val CHANNEL_ID = "my_channel_01"// The id of the channel.
        val name = getString(R.string.app_name)// The user-visible name of the channel.
        val importance = NotificationManager.IMPORTANCE_LOW

        var notificationBuilder_O = NotificationCompat.Builder(this, CHANNEL_ID)
        var notificationBuilder = NotificationCompat.Builder(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder_O = NotificationCompat.Builder(this, CHANNEL_ID)
                .setShowWhen(false)
//                .setStyle(
//                    NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession!!.sessionToken)
//                        .setShowActionsInCompactView(0)
//                )
                .setColor(ContextCompat.getColor(applicationContext, R.color.color_yellow))
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.noti_icon)
                .setContentText(activeAudio!!.getSubtitle())
                .setContentTitle(activeAudio!!.getTitle())
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setChannelId(CHANNEL_ID)
                .addAction(notificationAction, "Play/Pause", play_pauseAction)
                .addAction(stopNotificationAction, "Stop", closeAction)
                    as NotificationCompat.Builder
        } else {
            notificationBuilder = NotificationCompat.Builder(this)
                .setShowWhen(false)
//                .setStyle(
//                    NotificationCompat.MediaStyle()
//                        .setMediaSession(mediaSession!!.sessionToken)
//                        .setShowActionsInCompactView(0)
//                )
                .setColor(ContextCompat.getColor(applicationContext, R.color.color_yellow))
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.noti_icon)

                .setContentText(activeAudio!!.getSubtitle())
                .setContentTitle(activeAudio!!.getTitle())
                .addAction(notificationAction, "Play/Pause", play_pauseAction)
                .addAction(stopNotificationAction, "Stop", closeAction) as NotificationCompat.Builder
        }

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.enableLights(false)
            mChannel.setShowBadge(false)
            mNotificationManager.createNotificationChannel(mChannel);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder_O.setOngoing(true)
            mNotificationManager.notify(
                NOTIFICATION_ID,
                notificationBuilder_O.build()
            )
        } else {
            notificationBuilder.setOngoing(true)
            mNotificationManager.notify(
                NOTIFICATION_ID,
                notificationBuilder.build()
            )
        }
    }

    private fun playbackAction(actionNumber: Int): PendingIntent? {
        val playbackAction = Intent(this, MediaPlayerService::class.java)
        when (actionNumber) {
            0 -> {
                // Play
                playbackAction.action = ACTION_PLAY
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            1 -> {
                // Pause
                playbackAction.action = ACTION_PAUSE
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            2 -> {
                // Next track
                playbackAction.action = ACTION_NEXT
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            3 -> {
                // Previous track
                playbackAction.action = ACTION_PREVIOUS
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            4 -> {
                // Stop track
                playbackAction.action = ACTION_STOP
                return PendingIntent.getService(this, actionNumber, playbackAction, 0)
            }
            else -> {
            }
        }
        return null
    }

     fun removeNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun handleIncomingActions(playbackAction: Intent?) {
        if (playbackAction == null || playbackAction.action == null) return

        val actionString = playbackAction.action
        if (actionString!!.equals(ACTION_PLAY, ignoreCase = true)) {
            transportControls!!.play()
        } else if (actionString.equals(ACTION_PAUSE, ignoreCase = true)) {
            transportControls!!.pause()
        } else if (actionString.equals(ACTION_NEXT, ignoreCase = true)) {
            transportControls!!.skipToNext()
        } else if (actionString.equals(ACTION_PREVIOUS, ignoreCase = true)) {
            transportControls!!.skipToPrevious()
        } else if (actionString.equals(ACTION_STOP, ignoreCase = true)) {
            transportControls!!.stop()
        }
    }

    private fun register_playNewAudio() {
        //Register playNewMedia receiver
        val filter = IntentFilter(Broadcast_PLAY_NEW_AUDIO)
        registerReceiver(playNewAudio, filter)
    }

    companion object {


        val ACTION_PLAY = "ACTION_PLAY"
        val ACTION_STOP = "ACTION_STOP"
        val ACTION_PAUSE = "ACTION_PAUSE"
        val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        val ACTION_NEXT = "ACTION_NEXT"

        //AudioPlayer notification ID
        private val NOTIFICATION_ID = 101
    }


}