//package com.getyoteam.budamind.activity
//
//import android.Manifest
//import android.animation.ObjectAnimator
//import android.app.ActivityManager
//import android.app.Dialog
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import android.content.pm.PackageManager
//import android.graphics.PorterDuff
//import android.graphics.drawable.Drawable
//import android.media.MediaPlayer
//import android.net.ConnectivityManager
//import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import androidx.core.content.ContextCompat
//import android.view.View
//import android.util.Log
//import com.getyoteam.budamind.R
//import java.util.concurrent.TimeUnit
//import android.os.Handler
//import android.os.IBinder
//import android.view.Window
//import com.google.android.material.snackbar.Snackbar
//import android.view.WindowManager
//import android.view.animation.Animation
//import android.view.animation.LinearInterpolator
//import android.widget.ImageView
//import android.widget.SeekBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import com.bumptech.glide.load.resource.gif.GifDrawable
//import com.bumptech.glide.request.target.DrawableImageViewTarget
//import com.bumptech.glide.request.transition.Transition
//import com.getyoteam.budamind.Model.*
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.interfaces.ClarityAPI
//import com.getyoteam.budamind.playback.MusicNotificationManager
//import com.getyoteam.budamind.playback.MusicService
//import com.getyoteam.budamind.playback.PlaybackInfoListener
//import com.getyoteam.budamind.playback.PlayerAdapter
//import com.getyoteam.budamind.utils.AppDatabase
//import com.getyoteam.budamind.utils.EqualizerUtils
//import com.getyoteam.budamind.utils.ManagePermissions
//import com.getyoteam.budamind.utils.MediaPlayerService
//import com.downloader.*
//import com.getyoteam.budamind.interfaces.ApiUtils
//import com.google.gson.Gson
//import com.mindfulness.greece.model.MeditationStateModel
//import kotlinx.android.synthetic.main.activity_exoplayer_player.*
//import kotlinx.android.synthetic.main.activity_play_course.*
//import kotlinx.android.synthetic.main.activity_play_course.circleProgressNormal
//import kotlinx.android.synthetic.main.activity_play_course.cvInternetToast
//import kotlinx.android.synthetic.main.activity_play_course.ivBackword
//import kotlinx.android.synthetic.main.activity_play_course.ivClose
//import kotlinx.android.synthetic.main.activity_play_course.ivDownload
//import kotlinx.android.synthetic.main.activity_play_course.ivForward
//import kotlinx.android.synthetic.main.activity_play_course.ivPause
//import kotlinx.android.synthetic.main.activity_play_course.ivPlay
//import kotlinx.android.synthetic.main.activity_play_course.parentLayout
//import kotlinx.android.synthetic.main.activity_play_course.song_progressbar
//import kotlinx.android.synthetic.main.activity_play_course.tvPlaySubTitle
//import kotlinx.android.synthetic.main.activity_play_course.tvPlayTitle
//import kotlinx.android.synthetic.main.activity_play_course.tvTime
//import kotlinx.android.synthetic.main.activity_play_course.tvTotalTime
//import okhttp3.internal.Util
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.File
//import java.io.IOException
//import java.io.InputStream
//import java.text.SimpleDateFormat
//import java.util.*
//
//class PlayCourseActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,
//    MediaPlayer.OnBufferingUpdateListener {
//
//
//    var playPauseGif: PlayPauseGif? = null
//    private var animation: Boolean? = true
//    private var ivAnimatedImage: ImageView? = null
//    private val REQUEST_CODE: Int = 201
//    private var downloadId: Int = 0
//    private lateinit var courseModel: CourseListModel
//    private var meditationStateModel: MeditationStateModel? = null
//    private var downloadFileModelOld: DownloadFileModel? = null
//    private var chapterModel: ChapterListModel? = null
//    private var seekPer: Int = 0
//    private lateinit var seekBar: SeekBar
//    private lateinit var downloadFileModel: DownloadFileModel
//    private var currentPossion: Long = 0
//    private lateinit var filename: String
//    private lateinit var modelName: String
//    private var fileId: Int? = null
//    private val PermissionsRequestCode = 123
//    private val PermissionsRequestCodeAudio = 1234
//    private lateinit var managePermissions: ManagePermissions
//    private lateinit var managePermissionsAudio: ManagePermissions
//    private var customerId: String = ""
//    private var totalMin: Int = 0
//    private var totalDailyMin: Int = 0
//    private var totalWeeklyMin: Int = 0
//    private var totalSession: Int = 0
//    private var longestStreak: Int = 0
//    private var currentStreak: Int = 0
//    private var headerSubTitle: String = ""
//    private var isAPICallingDone: Boolean = false
//    private lateinit var intentData: Bundle
//    private var audioPath: String = ""
//    private var headerTitle: String = ""
//    private lateinit var db: AppDatabase
//    private lateinit var mediaPlayer: MediaPlayer
//    private var mediaFileLengthInMilliseconds: Long = 0
//    private var mPlayerAdapter: PlayerAdapter? = null
//    private var mMusicService: MusicService? = null
//    private var mMusicNotificationManager: MusicNotificationManager? = null
//    private var mPlaybackListener: PlaybackListener? = null
//    private var mUserIsSeeking = false
//    private var anim: ObjectAnimator? = null
//    private var mIsBound: Boolean? = false
//
//
//    private val mConnection = object : ServiceConnection {
//        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
//
//            mMusicService = (iBinder as MusicService.LocalBinder).getInstance()
//            mPlayerAdapter = mMusicService!!.getMediaPlayerHolder()
//            mMusicNotificationManager = mMusicService!!.getMusicNotificationManager()
//
//
//            if (mPlaybackListener == null) {
//                mPlaybackListener = PlaybackListener()
//                mPlayerAdapter!!.setPlaybackInfoListener(mPlaybackListener)
//            }
//            if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
//                restorePlayerStatus()
//            }
//            if (!mPlayerAdapter!!.isPlaying()) {//Check Media is playing or not
//                song_progressbar.progress = 0
//                if (downloadFileModelOld != null)
//                    onSongSelected(null, downloadFileModelOld)
//                else
//                    onSongSelected(chapterModel, null)
//
//            } else {
//                if (chapterModel != null && mPlayerAdapter!!.currentSong.equals(
//                        "Day " + chapterModel!!.getChapterName(),
//                        ignoreCase = true
//                    )
//                ) {
//                    if (mPlayerAdapter!!.mediaPlayer != null) {
//                        song_progressbar.progress = mPlayerAdapter!!.mediaPlayer.currentPosition.toInt()
//                        loadSeekBar(mPlayerAdapter!!.mediaPlayer.duration.toLong())
//                    }
//                } else if (downloadFileModelOld != null && mPlayerAdapter!!.currentSong.equals(
//                        downloadFileModelOld!!.getTitle(),
//                        ignoreCase = true
//                    )
//                ) {
//                    if (mPlayerAdapter!!.mediaPlayer != null) {
//                        song_progressbar.progress = mPlayerAdapter!!.mediaPlayer.currentPosition.toInt()
//                        loadSeekBar(mPlayerAdapter!!.mediaPlayer.duration.toLong())
//                    }
//                } else {
//                    song_progressbar.progress = 0
//                    onSongSelected(chapterModel, downloadFileModelOld)
//                }
//            }
//            progressBar.visibility = View.GONE
//            tvLoading.visibility = View.GONE
//        }
//
//        override fun onServiceDisconnected(componentName: ComponentName) {
//            mMusicService = null
//        }
//    }
//
//    inner class PlaybackListener : PlaybackInfoListener() {
//
//        override fun onPositionChanged(position: Int) {
//            if (!mUserIsSeeking) {
//                val milliSec80Per = (mPlayerAdapter!!.mediaPlayer.duration.toLong() * 80) / 100
//                if (!isAPICallingDone) {
//                    if (position > milliSec80Per) {
//                        isAPICallingDone = true
//                        val min = TimeUnit.MILLISECONDS.toMinutes(mPlayerAdapter!!.mediaPlayer.duration.toLong())
//                        apiCallAfterAudioComleteAt90Per(min)
//                        callRewardsAPI(courseModel.getCourseId().toString())
//                    }
//                }
//
//
//                song_progressbar.setProgress(position)
//                val time = String.format(
//                    "%02d:%02d",
//                    TimeUnit.MILLISECONDS.toMinutes(position.toLong()),
//                    TimeUnit.MILLISECONDS.toSeconds(position.toLong()) - TimeUnit.MINUTES.toSeconds(
//                        TimeUnit.MILLISECONDS.toMinutes(
//                            position.toLong()
//                        )
//                    )
//                )
//                tvTime.text = time
//            }
//        }
//
//        override fun onStateChanged(@State state: Int) {
//            updatePlayingStatus()
//            if (state != State.RESUMED && state != State.PAUSED && state != State.COMPLETED && state != State.STOP) {
//                updatePlayingInfo(false, true, state)
//            }
//        }
//
//        override fun onBufferUpdate(position: Int) {
//            song_progressbar.setSecondaryProgress(position * seekPer)
//        }
//
//        override fun onPlaybackStarted(totalDuration: Long) {
//            loadSeekBar(totalDuration)
//        }
//
//        override fun onPlaybackCompleted() {
//            doUnbindService()
//            val chapterListPlayedModel = ChapterListPlayedModel()
//            chapterListPlayedModel.setAudioUrl(chapterModel!!.getAudioUrl()!!)
//            chapterListPlayedModel.setChapterId(chapterModel!!.getChapterId()!!)
//            chapterListPlayedModel.setChapterName(chapterModel!!.getChapterName()!!)
//            chapterListPlayedModel.setCourseId(chapterModel!!.getCourseId()!!)
//            chapterListPlayedModel.setFreePaid(chapterModel!!.getFreePaid()!!)
//            chapterListPlayedModel.setCourseName(chapterModel!!.getCourseName()!!)
//            db.chapterPlayedDao().insertAll(chapterListPlayedModel)
//            MyApplication.prefs!!.courseId = chapterModel!!.getCourseId()!!
//            val isFirtsTime = MyApplication.prefs!!.isFirstTime
//            if (isFirtsTime!!) {
//                val intent = Intent(this@PlayCourseActivity, ReminderActivity::class.java)
//                intent.putExtra("chapterModel", chapterModel)
//                intent.putExtra("courseModel", courseModel)
//                intent.putExtra("meditationStateModel", meditationStateModel)
//                startActivity(intent)
//            } else {
//                val intent = Intent(this@PlayCourseActivity, QuotesActivity::class.java)
//                intent.putExtra("chapterModel", chapterModel)
//                intent.putExtra("courseModel", courseModel)
//                intent.putExtra("meditationStateModel", meditationStateModel)
//                startActivity(intent)
//            }
//            finish()
//        }
//
//        override fun onPlaybackStop() {
//            super.onPlaybackStop()
//            doUnbindService()
//            finish()
//        }
//    }
//
//    private fun loadSeekBar(totalDuration: Long) {
//        val time = String.format(
//            "%02d:%02d",
//            TimeUnit.MILLISECONDS.toMinutes(totalDuration),
//            TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(
//                TimeUnit.MILLISECONDS.toMinutes(
//                    totalDuration
//                )
//            )
//        )
//        song_progressbar.max = totalDuration.toInt()
//        tvTotalTime.text = time
//        seekPer = (totalDuration / 100).toInt()
//        ivPlay.isClickable = true
//        ivForward.isClickable = true
//        ivBackword.isClickable = true
//        if (mPlayerAdapter!!.isPlaying()) {
//            song_progressbar.setSecondaryProgress(100 * seekPer)
//        }
//    }
//
//    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean, state: Int) {
//        if (startPlay) {
//            mPlayerAdapter!!.getMediaPlayer().start()
//            Handler().postDelayed({
//                mMusicService!!.startForeground(
//                    MusicNotificationManager.NOTIFICATION_ID,
//                    mMusicNotificationManager!!.createNotification()
//                )
//            }, 250)
//        }
//
//        song_progressbar.setMax(mPlayerAdapter!!.mediaPlayer.duration.toInt())
//
//        if (restore) {
//            song_progressbar.setProgress(mPlayerAdapter!!.getPlayerPosition())
//            updatePlayingStatus()
//            Handler().postDelayed({
//                //stop foreground if coming from pause state
//                if (mMusicService!!.isRestoredFromPause()) {
//                    mMusicService!!.stopForeground(false)
//                    mMusicService!!.getMusicNotificationManager().notificationManager
//                        .notify(MusicNotificationManager.NOTIFICATION_ID, mMusicService!!.getMusicNotificationManager().notificationBuilder.build())
//                    mMusicService!!.setRestoredFromPause(false)
//                }
//            }, 250)
//        }
//    }
//
//
//    private fun updatePlayingStatus() {
//        val drawable = if (mPlayerAdapter!!.getState() !== PlaybackInfoListener.State.PAUSED)
//            R.drawable.ic_pause_white
//        else
//            R.drawable.ic_play_white
//        ivPlay.post(Runnable {
//            ivPlay.setImageResource(drawable)
//        })
//
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        val autoDownload = MyApplication.prefs!!.autoDownload
//        if (checkInternetConnection()) {
//            doBindService()
//            if (downloadFileModelOld == null) {
//                if (autoDownload!!) {
//                    checkDownloadPermmission()
//                }
//            }
//        } else {
//            if (downloadFileModelOld == null) {
//                cvInternetToast.visibility = View.VISIBLE
//                progressBar.visibility = View.GONE
//                tvLoading.visibility = View.GONE
//            } else {
//                doBindService()
//            }
//        }
//        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying) {
//            restorePlayerStatus()
//        }
//    }
//
//
//
//
//    private fun doUnbindService() {
//        if (mIsBound!!) {
//            // Detach our existing connection.
//            unbindService(mConnection)
//            mIsBound = false
//        }
//    }
//
//    private fun restorePlayerStatus() {
//        song_progressbar.setEnabled(mPlayerAdapter!!.isMediaPlayer())
//
//        //if we are playing and the activity was restarted
//        //update the controls panel
//        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {
//
//            mPlayerAdapter!!.onResumeActivity()
//            updatePlayingInfo(true, false, 0)
//        }
//    }
//
//    private fun doBindService() {
//        // Establish a connection with the service.  We use an explicit
//        // class name because we want a specific service implementation that
//        // we know will be running in our own process (and thus won't be
//        // supporting component replacement by other applications).
//        bindService(
//            Intent(
//                this,
//                MusicService::class.java
//            ), mConnection, Context.BIND_AUTO_CREATE
//        )
//        mIsBound = true
//
//        val startNotStickyIntent = Intent(this, MusicService::class.java)
//        startService(startNotStickyIntent)
//    }
//
//    fun onSongSelected(
//        chapterListModel: ChapterListModel?,
//        downloadFileModelOld: DownloadFileModel?
//    ) {
//        if (!song_progressbar.isEnabled()) {
//            song_progressbar.setEnabled(true)
//        }
//        try {
//            mPlayerAdapter!!.setCurrentSong(null, null, chapterListModel, downloadFileModelOld)
//            mPlayerAdapter!!.initMediaPlayer()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//
//    private fun initializeSeekBar() {
//        song_progressbar.setOnSeekBarChangeListener(
//            object : SeekBar.OnSeekBarChangeListener {
//                internal var userSelectedPosition = 0
//
//                override fun onStartTrackingTouch(seekBar: SeekBar) {
//                    mUserIsSeeking = true
//                }
//
//                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//
//                    if (fromUser) {
//                        userSelectedPosition = progress
//                        mPlayerAdapter!!.seekTo(progress)
//
//                    }
//
//                }
//
//                override fun onStopTrackingTouch(seekBar: SeekBar) {
//
//                    if (mUserIsSeeking) {
//
//                    }
//                    mUserIsSeeking = false
////                    mPlayerAdapter!!.seekTo(userSelectedPosition)
//                }
//            })
//    }
//
//    private fun checkIsPlayer(): Boolean {
//        val isPlayer = mPlayerAdapter!!.isMediaPlayer
//        if (!isPlayer) {
//            EqualizerUtils.notifyNoSessionId(this)
//        }
//        return isPlayer
//    }
//
//
//    fun resumeOrPause() {
//            mPlayerAdapter!!.resumeOrPause()
//    }
//
//    override fun onStop() {
//        super.onStop()
////        ivClose.performClick()
//        if (isMomentServiceRunning()) {
//            val intent = Intent(this, MusicService::class.java)
//            stopService(intent)
//        }
//        finish()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (isMomentServiceRunning()) {
//            val intent = Intent(this, MusicService::class.java)
//            stopService(intent)
//        }
//        finish()
////        doUnbindService()
////        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer) {
////            mPlayerAdapter!!.onPauseActivity()
////        }
//
////        ivClose.performClick()
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_play_course)
//        val list = listOf<String>(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        val listAudio = listOf<String>(
//            Manifest.permission.RECORD_AUDIO
//        )
//
//        db = AppDatabase.getDatabase(this)
//
//        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
//        managePermissionsAudio = ManagePermissions(this, listAudio, PermissionsRequestCodeAudio)
//
//        customerId = MyApplication.prefs!!.userId
//
//        if (isSoundServiceRunning()) {
//            val intent = Intent(this, MediaPlayerService::class.java)
//            stopService(intent);
//        }
//
//        ivAnimatedImage = findViewById(R.id.ivAnimatedImage)
//
//        anim = ObjectAnimator.ofFloat(ivAnimatedImage, View.ROTATION, 0f, 90f)
//        anim!!.setDuration(3000);
//        anim!!.setRepeatCount(Animation.INFINITE);
//        anim!!.setInterpolator(LinearInterpolator());
//        anim!!.start();
////        Glide.with(this).asGif().load(R.drawable.fruits_apple).into(ivAnimatedImage!!)
////        playPauseGif = PlayPauseGif(ivAnimatedImage!!)
//
//        var `is`: InputStream? = null
//        var bytes = ByteArray(0)
//        try {
//            `is` = assets.open("GIFNew.gif")
//            bytes = ByteArray(`is`!!.available())
//            `is`.read(bytes)
//            `is`.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
////        ivAnimatedImage!!.setBytes(bytes)
////        ivAnimatedImage!!.startAnimation()
//
//        btnPlay.setOnClickListener {
////            if (ivAnimatedImage!!.isAnimating) {
////                Log.e(TAG, "pausing")
////                ivAnimatedImage!!.stopAnimation()
////            } else {
////                Log.e(TAG, "playing")
////                ivAnimatedImage!!.startAnimation()
////            }
//            if (animation!!) {
//                Log.e("****", "play")
//                anim!!.pause()
//                btnPlay.setText("Play")
//                animation = false
//            } else {
//                Log.e("****", "pause")
//                anim!!.resume()
//                btnPlay.setText("Pause")
//                animation = true
//            }
//            resumeOrPause()
//        }
//
//        //Retrieve Data from preference into ModelArrayList
//        val gson = Gson()
//        val jsonChapter = MyApplication.prefs!!.chapterModel
//        val jsonCourse = MyApplication.prefs!!.courseModel
//        val jsonMeditation = MyApplication.prefs!!.stateModel
//        chapterModel = gson.fromJson(jsonChapter, ChapterListModel::class.java)
//        courseModel = gson.fromJson(jsonCourse, CourseListModel::class.java)
//        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel::class.java)
//        headerTitle = chapterModel!!.getCourseName().toString()
//        headerSubTitle = chapterModel!!.getChapterName().toString()
//        audioPath = chapterModel!!.getAudioUrl().toString()
//        fileId = chapterModel!!.getChapterId()
//        audioPath = chapterModel!!.getAudioUrl().toString()
//        modelName = "chapterModel"
//
//
//        if (audioPath != null && !audioPath.contains("data/")) {
//            val fileSting = audioPath.substring(audioPath.lastIndexOf('/') + 1)
//            filename = fileSting.replace("%20", " ")
//            downloadFileModel = DownloadFileModel()
//            downloadFileModel.setFileId(fileId)
//            downloadFileModel.setFileName(filename)
//            downloadFileModel.setFileType("audio")
//            downloadFileModel.setImageFile("")
//            downloadFileModel.setModelName(modelName)
//            downloadFileModel.setMinute("")
//            downloadFileModel.setSubTitle(chapterModel!!.getChapterName()!!)
//            downloadFileModel.setTitle(chapterModel!!.getCourseName())
//            downloadFileModel.setAudioFilePath(getString(R.string.download_path) + packageName + "/")
//        }
//
//        val meditationStateModelArrayList = db.meditationStateDao().getAll()
//        var meditationStateModelTemp = MeditationStateModel()
//        if (meditationStateModelArrayList.size > 0)
//            meditationStateModelTemp = meditationStateModelArrayList.get(0)
//
//        currentStreak = meditationStateModelTemp.getCurrentStreak() as Int
//        longestStreak = meditationStateModelTemp.getLongestStreak() as Int
//        totalSession = meditationStateModelTemp.getTotalSessions() as Int
//        totalMin = meditationStateModelTemp.getMinuteMeditated() as Int
//        totalDailyMin = meditationStateModelTemp.getDailyMinutes() as Int
//        totalWeeklyMin = meditationStateModelTemp.getWeeklyMinutes() as Int
//
//        getWindow().setFlags(
//            WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING,
//            WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
//        )
//
//        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename)
//        if (downloadFileModelOld != null) {
//            ivDownload.setImageResource(R.drawable.ic_download_done)
//        }
//
//        seekBar = findViewById<SeekBar>(R.id.song_progressbar)
//
//        mediaPlayer = MediaPlayer()
//        mediaPlayer.setOnBufferingUpdateListener(this)
//
//        tvPlayTitle.text = headerTitle
//        tvPlaySubTitle.text = "Day\n" + headerSubTitle
//
//        seekBar.getThumb().setColorFilter(resources.getColor(R.color.color_icon_light), PorterDuff.Mode.SRC_IN);
//        ivBackword.setOnClickListener(this)
//        ivForward.setOnClickListener(this)
//        ivPlay.setOnClickListener(this)
//        ivDownload.setOnClickListener(this)
//        circleProgressNormal.setOnClickListener(this)
//        ivClose.setOnClickListener(this)
//        cvInternetToast.setOnClickListener { cvInternetToast.visibility = View.GONE }
//
////        ivPlay.isClickable = false
//        ivForward.isClickable = false
//        ivBackword.isClickable = false
//
//        seekBar.setOnSeekBarChangeListener(this)
//        ivPlay.setColorFilter(ContextCompat.getColor(applicationContext, R.color.color_blue));
//        ivPause.setColorFilter(ContextCompat.getColor(applicationContext, R.color.color_blue));
//        ivBackword.setColorFilter(ContextCompat.getColor(applicationContext, R.color.color_blue));
//        ivForward.setColorFilter(ContextCompat.getColor(applicationContext, R.color.color_blue));
//
//        seekBar.setProgressDrawable(resources.getDrawable(R.drawable.progress_blue_drawable))
//        seekBar.getThumb().setColorFilter(resources.getColor(R.color.color_blue), PorterDuff.Mode.SRC_IN);
//        progressBar.indeterminateDrawable.setColorFilter(
//            ContextCompat.getColor(this, R.color.color_blue),
//            PorterDuff.Mode.SRC_IN
//        )
//
//        initializeSeekBar()
//    }
//
//    fun isSoundServiceRunning(): Boolean {
//        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (MediaPlayerService::class.java.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    class PlayPauseGif(view: ImageView?) : DrawableImageViewTarget(view) {
//        var gif: GifDrawable? = null
//
//        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//            if (resource is GifDrawable) {
//                gif = resource
//            }
//            super.onResourceReady(resource, transition)
//        }
//
//        fun isPlaying(): Boolean {
//            gif?.let {
//                return it.isRunning
//            }
//            return false
//        }
//
//        fun play() {
//            gif?.start()
//        }
//
//        fun pause() {
//            gif?.stop()
//        }
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS),
//            REQUEST_CODE
//        )
//    }
//
//    private fun permissionsNotGranted() {
//        Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
//        finish()
//    }
//
//
//    private fun checkInternetConnection(): Boolean {
//        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnected
//    }
//
//    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
//        seekBar.setSecondaryProgress(percent * seekPer)
//    }
//
//    val mSeekbarUpdateHandler = Handler()
//    val mUpdateSeekbar = object : Runnable {
//        override fun run() {
//            currentPossion = mediaPlayer.getCurrentPosition().toLong();
//            val milliSec80Per = (mediaFileLengthInMilliseconds * 80) / 100
//            if (!isAPICallingDone) {
//                if (currentPossion > milliSec80Per) {
//                    if (MyApplication.prefs!!.firstMeditationId != 0) {
//                        MyApplication.prefs!!.firstMeditationId = courseModel.getCourseId()!!
//                    }
//                    isAPICallingDone = true
//                    val min = TimeUnit.MILLISECONDS.toMinutes(mediaFileLengthInMilliseconds)
//                    apiCallAfterAudioComleteAt90Per(min)
//                    callRewardsAPI(courseModel.getCourseId().toString())
//                }
//            }
//            seekBar.setProgress(mediaPlayer.getCurrentPosition())
//            val time = String.format(
//                "%02d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(currentPossion),
//                TimeUnit.MILLISECONDS.toSeconds(currentPossion) - TimeUnit.MINUTES.toSeconds(
//                    TimeUnit.MILLISECONDS.toMinutes(
//                        currentPossion
//                    )
//                )
//            )
//            tvTime.text = time
//            mSeekbarUpdateHandler.postDelayed(this, 50)
//        }
//    }
//
//    private fun apiCallAfterAudioComleteAt90Per(min: Long) {
//        val calendar = Calendar.getInstance()
//        val today = calendar.getTime()
//        calendar.add(Calendar.DAY_OF_YEAR, -1)
//        val prevDate = calendar.getTime()
//        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
//        val todayAsString = dateFormat.format(today)
//        val prevDateAsString = dateFormat.format(prevDate)
//        val previousDatePref = MyApplication.prefs!!.prevDate
//
//        if (previousDatePref.equals(prevDateAsString, ignoreCase = true)) {
//            currentStreak++
//            MyApplication.prefs!!.prevDate = todayAsString
//        } else {
//            if (!previousDatePref.equals(todayAsString, ignoreCase = true)) {
//                currentStreak = 1
//                MyApplication.prefs!!.prevDate = todayAsString
//            }
//        }
//
//        if (currentStreak > longestStreak) {
//            setMeditationState(min, currentStreak, longestStreak + 1, totalSession + 1)
//        } else {
//            setMeditationState(min, currentStreak, longestStreak, totalSession + 1)
//        }
//    }
//
//    private fun setMeditationState(min: Long, currentStreak: Int, longestStreak: Int, totalSession: Int) {
//        val userId = MyApplication.prefs!!.userId
//
//        val userMap: HashMap<String, String> = HashMap<String, String>()
//        userMap.put("userId", userId)
//        userMap.put("currentStreak", currentStreak.toString())
//        userMap.put("longestStreak", longestStreak.toString())
//        userMap.put("minuteMeditate", (min + totalMin).toString())
//        userMap.put("dailyMinutes", (min + totalDailyMin).toString())
//        userMap.put("totalSessions", totalSession.toString())
//
//        val call = ApiUtils.getAPIService().updateProfile(userMap)
//        val meditationStateModelTemp = MeditationStateModel()
//        meditationStateModelTemp.setUserId(userId.toInt())
//        meditationStateModelTemp.setCurrentStreak(currentStreak)
//        meditationStateModelTemp.setLongestStreak(longestStreak)
//        meditationStateModelTemp.setMinuteMeditated((min + totalMin).toInt())
//        meditationStateModelTemp.setDailyMinutes((min + totalDailyMin).toInt())
//        meditationStateModelTemp.setWeeklyMinutes((min + totalWeeklyMin).toInt())
//        meditationStateModelTemp.setTotalSessions(totalSession)
//
//        db.meditationStateDao().insertAll(meditationStateModelTemp)
//        meditationStateModel = meditationStateModelTemp
//
//        call.enqueue(object : Callback<CommanResponseModel> {
//            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {
////                Toast.makeText(this@PlayActivity, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
////                    .show()
//            }
//
//            override fun onResponse(call: Call<CommanResponseModel>, response: Response<CommanResponseModel>) {
//                if (response.code() == 200) {
//                }
//            }
//        })
//    }
//    private fun callRewardsAPI(id: String) {
//
//        val userId = MyApplication.prefs!!.userId
//
//
//        val call = ApiUtils.getAPIService().getRewardsOnPlay(userId, "course", id)
//
//        call.enqueue(object : Callback<responceRewarModel> {
//            override fun onFailure(call: Call<responceRewarModel>, t: Throwable) {
//
//                Toast.makeText(
//                    applicationContext,
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//
//            override fun onResponse(
//                call: Call<responceRewarModel>,
//                response: Response<responceRewarModel>
//            ) {
//                if (response.code() == 200) {
//
//                    val commonModel = response.body()
//                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
//
//                        wanCoinDialog(Util.format(commonModel.creditedCoins))
//
//                    }
//                }
//            }
//        })
//    }
//    fun wanCoinDialog(coin: String) {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_congratulation_view)
//        val window = dialog.window
//        window!!.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT
//        )
//        window.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                this,
//                android.R.color.transparent
//            )
//        )
//        val close: ImageView
//        val tvCoin: TextView
//        val tvWallate: TextView
//        close = dialog.findViewById(R.id.ivClose)
//        tvCoin = dialog.findViewById(R.id.tvCoin)
//        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
//        tvCoin.text = "You have won " +coin+" $"+"CHI."
//        close.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        tvWallate.setOnClickListener {
//            dialog.dismiss()
////
////            val intent = Intent(applicationContext, WalletActivity::class.java)
////            startActivity(intent)
//
//        }
//        dialog.show()
//    }
//
//    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//        if (mediaPlayer != null && fromUser) {
//            if (mediaPlayer.isPlaying)
//                mediaPlayer.seekTo(progress);
//        }
//    }
//
//    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//
//    }
//
//    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//
//    }
//
//
//    private fun stop() {
//        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
//        if (!isAPICallingDone) {
//            var min = TimeUnit.MILLISECONDS.toMinutes(currentPossion).toInt()
//            val sec = TimeUnit.MILLISECONDS.toSeconds(currentPossion) - TimeUnit.MINUTES.toSeconds(
//                TimeUnit.MILLISECONDS.toMinutes(
//                    currentPossion
//                )
//            )
//            if (min == 0) {
//                if (sec > 50) {
//                    setMeditationState(1, currentStreak, longestStreak, totalSession)
//                }
//            } else {
//                setMeditationState(min.toLong(), currentStreak, longestStreak, totalSession)
//            }
//        }
//
//        if (mediaPlayer.isPlaying) {
//            mediaPlayer.stop()
//        }
//        if (mediaPlayer != null) {
//            mediaPlayer.release()
//        }
//    }
//
//
//    companion object {
//        private val TAG = "DAMIT"
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        stop()
//
//        if (isTaskRoot) {
//            val intent = Intent(this@PlayCourseActivity, MainActivity::class.java)
//            startActivity(intent)
//        }
//    }
//
//    override fun onClick(v: View?) {
//        when (v!!.id) {
//            R.id.ivBackword -> {
//                mPlayerAdapter!!.onBackword()
//            }
//            R.id.circleProgressNormal -> {
//                if (downloadId != 0) {
//                    val status = PRDownloader.getStatus(downloadId);
//                    if (status.toString().equals("running", ignoreCase = true)) {
//                        PRDownloader.cancel(downloadId)
//                        circleProgressNormal.progress = 0
//                        circleProgressNormal.visibility = View.GONE
//                        ivDownload.visibility = View.VISIBLE
//                    }
//                }
//            }
//            R.id.ivDownload -> {
//                if (downloadFileModelOld != null) {
//                    val snackbar = Snackbar
//                        .make(parentLayout, getString(R.string.str_delete_a_file), Snackbar.LENGTH_INDEFINITE)
//                        .setAction(getString(R.string.str_yes), object : View.OnClickListener {
//                            override fun onClick(v: View?) {
//                                var path =
//                                    downloadFileModelOld!!.getAudioFilePath() + downloadFileModelOld!!.getFileName()
//                                var myFile = File(path)
//                                if (myFile.exists()) {
//                                    myFile.delete()
//                                    db.downloadDao().delete(downloadFileModelOld!!)
//                                    downloadFileModelOld = null
//                                    circleProgressNormal.progress = 0
//                                    ivDownload.setImageResource(R.drawable.ic_download_dark)
//
//                                } else
//                                    Toast.makeText(this@PlayCourseActivity, "file not found", Toast.LENGTH_SHORT).show()
//                            }
//
//                        })
//                    snackbar.view.setOnClickListener {
//                        snackbar.dismiss()
//                    }
//                    snackbar.show();
//                } else {
//                    if (audioPath != null && !audioPath.equals("null", ignoreCase = true))
//                        if (downloadFileModelOld == null) {
//                            if (checkInternetConnection()) {
//                                checkDownloadPermmission()
//                            } else {
//                                Toast.makeText(
//                                    this,
//                                    getString(R.string.str_check_internet_connection),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                }
//
//            }
//            R.id.ivForward -> {
//                mPlayerAdapter!!.onForward()
//            }
//            R.id.ivPlay -> {
//                if (animation!!) {
//                    Log.e("****", "play")
//                    anim!!.pause()
//                    btnPlay.setText("Play")
//                    animation = false
//                } else {
//                    Log.e("****", "pause")
//                    anim!!.resume()
//                    btnPlay.setText("Pause")
//                    animation = true
//                }
//                resumeOrPause()
//            }
//            R.id.ivClose -> {
//                if (isMomentServiceRunning()) {
//                    val intent = Intent(this, MusicService::class.java)
//                    stopService(intent)
//                }
//                finish()
//            }
//        }
//    }
//
//    fun isMomentServiceRunning(): Boolean {
//        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (MusicService::class.java.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private fun checkDownloadPermmission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    this@PlayCourseActivity,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                downloadAudio()
//            } else {
//                managePermissions.checkPermissions()
//            }
//        } else {
//            downloadAudio()
//        }
//    }
//
//    // Receive the permissions request result
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        when (requestCode) {
//            PermissionsRequestCode -> {
//                val isPermissionsGranted = managePermissions
//                    .processPermissionsResult(requestCode, permissions, grantResults)
//
////                if (isPermissionsGranted) {
//////                    downloadAudio()
////                    toast("Permissions granted.")
////                } else {
////                    toast("Permissions denied.")
////                }
//                return
//            }
//
//        }
//    }
//
//    private fun downloadAudio() {
////        TODO("/data/user/0/com.mindfulness.greece/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
//        val dirPath = getApplicationInfo().dataDir
//        circleProgressNormal.visibility = View.VISIBLE
//        ivDownload.visibility = View.GONE
//        downloadId = PRDownloader.download(audioPath, dirPath, filename)
//            .build()
//            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
//                override fun onStartOrResume() {
//
//                }
//
//            })
//            .setOnPauseListener(object : OnPauseListener {
//                override fun onPause() {
//
//                }
//            })
//            .setOnCancelListener(object : OnCancelListener {
//                override fun onCancel() {
//
//                }
//            })
//            .setOnProgressListener(object : OnProgressListener {
//                override fun onProgress(progress: Progress) {
//                    val progressPercent = progress.currentBytes * 100 / progress.totalBytes;
//
//                    circleProgressNormal.progress = progressPercent.toInt()
//
//                }
//            })
//            .start(object : OnDownloadListener {
//                override fun onError(error: com.downloader.Error?) {
//                    Log.e("@@@", error.toString() + "@@@")
//                }
//
//                override fun onDownloadComplete() {
//                    circleProgressNormal.visibility = View.GONE
//
//                    circleProgressNormal.progress = 0
//
//
//                    ivDownload.visibility = View.VISIBLE
//                    ivDownload.setImageResource(R.drawable.ic_download_done)
//
//                    //Todo: after download set course true to display in download
//                    courseModel.setIsDownloaded(true)
//                    val courseDownloadModel = CourseDownloadModel()
//                    courseDownloadModel.setBanner(courseModel.getBanner()!!)
//                    courseDownloadModel.setCourseId(courseModel.getCourseId()!!)
//                    courseDownloadModel.setCourseName(courseModel.getCourseName()!!)
//                    courseDownloadModel.setDescription(courseModel.getDescription()!!)
//                    courseDownloadModel.setFromMinutes(courseModel.getFromMinutes()!!)
//                    courseDownloadModel.setToMinutes(courseModel.getToMinutes()!!)
//                    courseDownloadModel.setColorCode(courseModel.getColorCode()!!)
//                    db.courseDownloadDao().insertAll(courseDownloadModel)
//
//                    db.downloadDao().insertDownloadFile(downloadFileModel)
//                    filename = audioPath.substring(audioPath.lastIndexOf('/') + 1);
//                    downloadFileModelOld = downloadFileModel
//                }
//
//            })
//    }
//
//}
