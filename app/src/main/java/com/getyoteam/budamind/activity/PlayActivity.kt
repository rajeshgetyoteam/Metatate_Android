package com.getyoteam.budamind.activity

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Outline
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.downloader.*
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.getyoteam.budamind.playback.MusicNotificationManager
import com.getyoteam.budamind.playback.MusicService
import com.getyoteam.budamind.playback.PlaybackInfoListener
import com.getyoteam.budamind.playback.PlayerAdapter
import com.getyoteam.budamind.testaudioexohls.AudioService
import com.getyoteam.budamind.utils.*
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.activity_play.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class PlayActivity : AppCompatActivity(), View.OnClickListener {

    private var meditationStateModel: MeditationStateModel? = null
    private lateinit var managePermissions: ManagePermissions
    private lateinit var downloadFileModel: DownloadFileModel
    private var downloadFileModelOld: DownloadFileModel? = null
    private var mIsBound: Boolean? = false

    var mConnection :ServiceConnection? = null
    private var isAPICallingDone: Boolean = false
    private var momentModel: MomentListModel? = null
    private var seekPer: Int = 0
    private var subTitle: String? = ""
    private var downloadId: Int = 0
    private var filename: String? = null
    private lateinit var modelName: String
    private var fileId: Int? = null
    private val PermissionsRequestCode = 123
    private var imageUrl: String? = null
    private var title: String? = null
    private var audioPath: String? = null
    private var mUserIsSeeking = false
    private var totalMin: Int = 0
    private var totalDailyMin: Int = 0
    private var totalWeeklyMin: Int = 0
    private var totalSession: Int = 0
    private var longestStreak: Int = 0
    private var currentStreak: Int = 0
    private lateinit var db: AppDatabase
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mMusicService: MusicService? = null
    private var mMusicNotificationManager: MusicNotificationManager? = null
    private var mPlaybackListener: PlaybackListener? = null




    inner class PlaybackListener : PlaybackInfoListener() {

        override fun onPositionChanged(position: Int) {
            if (!mUserIsSeeking) {
                val milliSec80Per = (mPlayerAdapter!!.mediaPlayer.duration.toLong() * 80) / 100
                val milliSec99Per = (mPlayerAdapter!!.mediaPlayer.duration.toLong() * 99) / 100
                if (!isAPICallingDone) {
                    if (position > milliSec80Per) {
                        isAPICallingDone = true
                        val min =
                            TimeUnit.MILLISECONDS.toMinutes(mPlayerAdapter!!.mediaPlayer.duration.toLong())
                        apiCallAfterAudioComleteAt90Per(min)
                        callRewardsAPI(momentModel!!.getMomentId().toString())
                    }
                }
                song_progressbar.progress = position
                val time = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(position.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(position.toLong()) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(
                            position.toLong()
                        )
                    )
                )
                tvTime.text = time
            }
        }

        override fun onStateChanged(state: Int) {
            updatePlayingStatus()
            if (state != State.RESUMED && state != State.PAUSED && state != State.COMPLETED && state != State.STOP) {
                updatePlayingInfo(false, true, state)
            }
        }

        override fun onBufferUpdate(position: Int) {
            song_progressbar.setSecondaryProgress(position * seekPer)
        }

        override fun onPlaybackStarted(totalDuration: Long) {
            loadSeekBar(totalDuration)
        }

        override fun onPlaybackCompleted() {

            var time :Long = 0
            val userId = MyApplication.prefs!!.userId
            if (mPlayerAdapter != null) {
                time =
                    TimeUnit.MILLISECONDS.toSeconds(mPlayerAdapter!!.mediaPlayer.currentPosition.toLong())
            }
            val jsonObj = JSONObject()
            if (time > 0){
//                    jsonObj.put("chapterId", chapterId)
                jsonObj.put("userId", userId)
                jsonObj.put("tabName", "Moment")
                jsonObj.put("tabId", momentModel!!.getMomentId())
                jsonObj.put("totalSeconds", time.toString())
                Utils.callUpdateSeconds("",jsonObj)
            }
            doUnbindService()
            val intent = Intent(this@PlayActivity, QuotesActivity::class.java)
            if (momentModel != null) {
                intent.putExtra("momentModel", momentModel)
            } else if (downloadFileModelOld != null) {
                intent.putExtra("downloadModel", downloadFileModelOld)
            }
            intent.putExtra("meditationStateModel", meditationStateModel)
            startActivity(intent)
            finish()
        }

        override fun onPlaybackStop() {
            super.onPlaybackStop()
            var time :Long = 0
            val userId = MyApplication.prefs!!.userId
            if (mPlayerAdapter != null) {
                time = TimeUnit.MILLISECONDS.toSeconds(mPlayerAdapter!!.mediaPlayer.currentPosition.toLong())
            }
            val jsonObj = JSONObject()
            if (time > 0){
//                    jsonObj.put("chapterId", chapterId)
                jsonObj.put("userId", userId)
                jsonObj.put("tabName", "Moment")
                jsonObj.put("tabId", momentModel!!.getMomentId())
                jsonObj.put("totalSeconds", time.toString())
                Utils.callUpdateSeconds("",jsonObj)
            }
            doUnbindService()
            finish()
        }
    }

    private fun callRewardsAPI(momentId: String) {

        val userId = MyApplication.prefs!!.userId


        val call = ApiUtils.getAPIService().getRewardsOnPlay(userId, "moment", momentId)

        call.enqueue(object : Callback<responceRewarModel> {
            override fun onFailure(call: Call<responceRewarModel>, t: Throwable) {

                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<responceRewarModel>,
                response: Response<responceRewarModel>
            ) {
                if (response.code() == 200) {

                    val commonModel = response.body()
                    if (commonModel!!.status.equals(getString(R.string.str_success))) {

                        wanCoinDialog(Utils.format(commonModel.creditedCoins!!.toBigInteger()))

                    }
                }
            }
        })
    }

    fun wanCoinDialog(coin: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_congratulation_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this,
                android.R.color.transparent
            )
        )
        val close: ImageView
        val tvCoin: TextView
        val tvWallate: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvCoin = dialog.findViewById(R.id.tvCoin)
        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
        tvCoin.text = "You have won " + coin.replace("$","$"+"CHI")
        close.setOnClickListener {
            dialog.dismiss()
        }

        tvWallate.setOnClickListener {
            dialog.dismiss()
//
//            val intent = Intent(applicationContext, WalletActivity::class.java)
//            startActivity(intent)

        }
        dialog.show()
    }

    private fun getQuote() {
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(ClarityAPI::class.java)
        val call = ApiUtils.getAPIService().getRandomQuotes()

        call.enqueue(object : Callback<QuoteModel> {
            override fun onFailure(call: Call<QuoteModel>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<QuoteModel>,
                response: Response<QuoteModel>
            ) {
                if (response.code() == 200) {
                    val quoteModel = response.body()!!
                    if (quoteModel.getStatus().equals(
                            getString(R.string.str_success),
                            ignoreCase = true
                        )
                    ) {
                        MyApplication.prefs!!.songQuote = quoteModel.getQuote()!!
                    }
                }
            }
        })
    }


    private fun loadSeekBar(totalDuration: Long) {
        val time = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(totalDuration),
            TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    totalDuration
                )
            )
        )
        song_progressbar.max = totalDuration.toInt()
        tvTotalTime.text = time
        seekPer = (totalDuration / 100).toInt()
        ivPlay.isClickable = true
        ivForward.isClickable = true
        ivBackword.isClickable = true
        if (mPlayerAdapter!!.isPlaying()) {
//            song_progressbar.setSecondaryProgress(100 * seekPer)
        }
    }


    private fun updatePlayingInfo(restore: Boolean, startPlay: Boolean, state: Int) {
        if (startPlay) {
            mPlayerAdapter!!.getMediaPlayer().start()
            Handler().postDelayed({
                mMusicService!!.startForeground(
                    MusicNotificationManager.NOTIFICATION_ID,
                    mMusicNotificationManager!!.createNotification()
                )
            }, 250)
        }

        song_progressbar.setMax(mPlayerAdapter!!.mediaPlayer.duration.toInt())

        if (restore) {
            song_progressbar.setProgress(mPlayerAdapter!!.getPlayerPosition())
            updatePlayingStatus()
            Handler().postDelayed({
                //stop foreground if coming from pause state
                if (mMusicService!!.isRestoredFromPause()) {
                    mMusicService!!.stopForeground(false)
                    mMusicService!!.getMusicNotificationManager().notificationManager
                        .notify(
                            MusicNotificationManager.NOTIFICATION_ID,
                            mMusicService!!.getMusicNotificationManager().notificationBuilder.build()
                        )
                    mMusicService!!.setRestoredFromPause(false)
                }
            }, 250)
        }
    }


    private fun updatePlayingStatus() {
        val drawable = if (mPlayerAdapter!!.getState() != PlaybackInfoListener.State.PAUSED)
            R.drawable.ic_pause_white
        else if (mPlayerAdapter!!.getState() != PlaybackInfoListener.State.STOP)
            R.drawable.ic_play_white
        else
            R.drawable.ic_play_white
        ivPlay.post(Runnable { ivPlay.setImageResource(drawable) })


    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {
            mPlayerAdapter!!.onPauseActivity()
        }
    }

    private fun doUnbindService() {
        if (mIsBound!!) {
            // Detach our existing connection.
            unbindService(mConnection!!)
            mIsBound = false
        }
    }

    private fun restorePlayerStatus() {
        song_progressbar.setEnabled(mPlayerAdapter!!.isMediaPlayer())

        //if we are playing and the activity was restarted
        //update the controls panel
        if (mPlayerAdapter != null && mPlayerAdapter!!.isMediaPlayer()) {

            mPlayerAdapter!!.onResumeActivity()
            updatePlayingInfo(true, false, 0)
        }
    }


    private fun doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(
            Intent(
                this,
                MusicService::class.java
            ), mConnection!!, Context.BIND_AUTO_CREATE
        )
        mIsBound = true

        val startNotStickyIntent = Intent(this, MusicService::class.java)
        startService(startNotStickyIntent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)


        runOnUiThread {
            mConnection = object : ServiceConnection {
                override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {

                    mMusicService = (iBinder as MusicService.LocalBinder).getInstance()
                    mPlayerAdapter = mMusicService!!.getMediaPlayerHolder()
                    mMusicNotificationManager = mMusicService!!.getMusicNotificationManager()

                    if (mPlaybackListener == null) {
                        mPlaybackListener = PlaybackListener()
                        mPlayerAdapter!!.setPlaybackInfoListener(mPlaybackListener)
                    }
                    if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
                        restorePlayerStatus()
                    }
                    if (!mPlayerAdapter!!.isPlaying()) {//Check Media is playing or not
                        song_progressbar.progress = 0
                        onSongSelected(momentModel, downloadFileModelOld)
                    } else {
                        if (momentModel != null && mPlayerAdapter!!.currentSong.equals(
                                momentModel!!.getTitle(),
                                ignoreCase = true
                            )
                        ) {
                            if (mPlayerAdapter!!.mediaPlayer != null) {
                                song_progressbar.progress =
                                    mPlayerAdapter!!.mediaPlayer.currentPosition.toInt()
                                loadSeekBar(mPlayerAdapter!!.mediaPlayer.duration.toLong())
                            }
                        } else if (downloadFileModelOld != null && mPlayerAdapter!!.currentSong.equals(
                                downloadFileModelOld!!.getTitle(),
                                ignoreCase = true
                            )
                        ) {
                            if (mPlayerAdapter!!.mediaPlayer != null) {
                                song_progressbar.progress =
                                    mPlayerAdapter!!.mediaPlayer.currentPosition.toInt()
                                loadSeekBar(mPlayerAdapter!!.mediaPlayer.duration.toLong())
                            }
                        } else {
                            song_progressbar.progress = 0
                            onSongSelected(momentModel, downloadFileModelOld)
                        }
                    }
                    progressBar.visibility = View.GONE
                    tvLoading.visibility = View.GONE
                }

                override fun onServiceDisconnected(componentName: ComponentName) {
                    mMusicService = null
                }
            }
        }




        db = AppDatabase.getDatabase(this)

        val list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)

        laymain.setOnClickListener {

            if (layTop.visibility == View.GONE) {
                layTop!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_in
                    )
                )

                layBottom!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_in
                    )
                )


                layTop.visibility = View.VISIBLE
                layBottom.visibility = View.VISIBLE

                ivPlay.isClickable = true
                ivForward.isClickable = true
                ivBackword.isClickable = true
                ivClose.isClickable = true
                ivDownload.isClickable = true
                song_progressbar.isEnabled = true


            } else {

                layTop!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_out
                    )
                )
                layBottom!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_out
                    )
                )


                layTop.visibility = View.GONE
                layBottom.visibility = View.GONE

                ivPlay.isClickable = false
                ivForward.isClickable = false
                ivBackword.isClickable = false
                ivClose.isClickable = false
                ivDownload.isClickable = false
                song_progressbar.isEnabled = false
            }


        }

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING,
            WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
        )

        if (isSoundServiceRunning(MediaPlayerService::class.java.getName())) {
            val intent = Intent(this, MediaPlayerService::class.java)
            stopService(intent);
        }

        if (isSoundServiceRunning(AudioService::class.java.getName())) {
            val intent = Intent(this, AudioService::class.java)
            stopService(intent);
        }

        //Retrieve Data from preference into ModelArrayList
        val gson = Gson()
        val jsonMoment = MyApplication.prefs!!.momentModel
        val jsonMeditation = MyApplication.prefs!!.stateModel

        momentModel = gson.fromJson(jsonMoment, MomentListModel::class.java)
        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel::class.java)


        if (momentModel!!.getRewarded() != null && momentModel!!.getRewarded()!!) {
            layReward.visibility = View.GONE
        } else {
            layReward.visibility = View.VISIBLE
        }
        layReward.setOnClickListener {

            Toast.makeText(
                this,
                "You cannot skip the content.",
                Toast.LENGTH_SHORT
            ).show()
        }

        if (momentModel != null) {
            title = momentModel!!.getTitle()
            subTitle = momentModel!!.getSubtitle()
            audioPath = momentModel!!.getAudio()
            imageUrl = momentModel!!.getImage()
            modelName = "momentModel"
        }

        if (intent.extras!!.containsKey("downloadModel")) {
            downloadFileModelOld = intent.extras!!.get("downloadModel") as DownloadFileModel
            meditationStateModel =
                intent.extras!!.get("meditationStateModel") as MeditationStateModel
            title = downloadFileModelOld!!.getTitle()
            subTitle = downloadFileModelOld!!.getSubTitle()
            audioPath = downloadFileModelOld!!.getAudioFilePath()
            imageUrl = downloadFileModelOld!!.getImageFile()
            filename = downloadFileModelOld!!.getFileName()
        }

//        if (downloadFileModelOld != null) {
//            title = downloadFileModelOld!!.getTitle()
//            subTitle = downloadFileModelOld!!.getSubTitle()
//            audioPath = downloadFileModelOld!!.getAudioFilePath()
//            imageUrl = downloadFileModelOld!!.getImageFile()
//            filename = downloadFileModelOld!!.getFileName()
//        }

        val image = findViewById<ImageView>(R.id.ivPlayBackground)
        val curveRadius = resources.getDimension(R.dimen._25sdp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            image.outlineProvider = object : ViewOutlineProvider() {

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View?, outline: Outline?) {
                    outline?.setRoundRect(
                        0,
                        0,
                        view!!.width,
                        (view.height + curveRadius).toInt(),
                        curveRadius
                    )
                }
            }

            image.clipToOutline = true

        }

        initializeSeekBar()
        Glide.with(this)
            .load(imageUrl)
            .placeholder(ColorDrawable(ContextCompat.getColor(this, R.color.colorPrimary)))
            .transition(DrawableTransitionOptions.withCrossFade())
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(ivPlayBackground)

        if (audioPath != null && !audioPath!!.contains("data/")) {
            val fileSting = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
            filename = fileSting.replace("%20", " ")
            downloadFileModel = DownloadFileModel()
            downloadFileModel.setFileId(fileId)
            downloadFileModel.setFileName(filename!!)
            downloadFileModel.setFileType("audio")
            downloadFileModel.setImageFile(imageUrl!!)
            downloadFileModel.setModelName(modelName)
            downloadFileModel.setMinute(momentModel!!.getMinutes()!!)
            downloadFileModel.setSubTitle(momentModel!!.getSubtitle()!!)
            downloadFileModel.setTitle(momentModel!!.getTitle())
            downloadFileModel.setAudioFilePath(getString(R.string.download_path) + packageName + "/")
        }

        if (filename != null) {
            downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
        }

        if (downloadFileModelOld != null) {
            ivDownload.visibility = View.VISIBLE
            ivDownload.setImageResource(R.drawable.ic_download_done_white)
            momentModel = null
        }

        tvPlayTitle.text = title
        tvPlaySubTitle.text = subTitle

        progressBar.indeterminateDrawable.setColorFilter(
            ContextCompat.getColor(this, R.color.color_black),
            PorterDuff.Mode.SRC_IN
        )

        val meditationStateModelArrayList = db.meditationStateDao().getAll()
        var meditationStateModelTemp = MeditationStateModel()
        if (meditationStateModelArrayList.size > 0)
            meditationStateModelTemp = meditationStateModelArrayList.get(0)

        currentStreak = meditationStateModelTemp.getCurrentStreak() as Int
        longestStreak = meditationStateModelTemp.getLongestStreak() as Int
        totalSession = meditationStateModelTemp.getTotalSessions() as Int
//        totalMin = meditationStateModelTemp.getMinuteMeditated() as Int
//        totalDailyMin = meditationStateModelTemp.getDailyMinutes() as Int
//        totalWeeklyMin = meditationStateModelTemp.getWeeklyMinutes() as Int

        ivBackword.setOnClickListener(this)
        ivForward.setOnClickListener(this)
        ivPlay.setOnClickListener(this)
        ivPlay2.setOnClickListener(this)
        ivClose.setOnClickListener(this)
        cvInternetToast.setOnClickListener(this)
        ivDownload.setOnClickListener(this)
        circleProgressNormal.setOnClickListener(this)

        ivPlay.isClickable = false
        ivForward.isClickable = false
        ivBackword.isClickable = false
        song_progressbar!!.getThumb()
            .setColorFilter(resources.getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        getQuote()

    }



    fun isSoundServiceRunning(serviceName: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    fun onSongSelected(
        song: MomentListModel?,
        downloadFileModelOld: DownloadFileModel?
    ) {
        if (!song_progressbar.isEnabled()) {
            song_progressbar.setEnabled(true)
        }
        try {
            mPlayerAdapter!!.setCurrentSong(song, null, null, downloadFileModelOld)
            mPlayerAdapter!!.initMediaPlayer()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    override fun onResume() {
        super.onResume()
        val autoDownload = MyApplication.prefs!!.autoDownload

        if (checkInternetConnection()) {
            doBindService()
            if (downloadFileModelOld == null) {
                if (autoDownload!!) {
                    checkDownloadPermmission()
                }
            }
        } else {
            if (downloadFileModelOld == null) {
                cvInternetToast.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                tvLoading.visibility = View.GONE
            } else {
                doBindService()
            }
        }
        if (mPlayerAdapter != null && mPlayerAdapter!!.isPlaying()) {
            restorePlayerStatus()
        }
    }


    fun resumeOrPause() {
        if (checkIsPlayer()) {
            mPlayerAdapter!!.resumeOrPause()
        }
    }


    private fun checkIsPlayer(): Boolean {

        val isPlayer = mPlayerAdapter!!.isMediaPlayer()
        if (!isPlayer) {
            EqualizerUtils.notifyNoSessionId(this)
        }
        return isPlayer
    }


    private fun initializeSeekBar() {
        song_progressbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                internal var userSelectedPosition = 0

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    mUserIsSeeking = true
                }

                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                    if (fromUser) {
                        userSelectedPosition = progress
                        mPlayerAdapter!!.seekTo(progress)

                    }

                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {

                    if (mUserIsSeeking) {

                    }
                    mUserIsSeeking = false
//                    mPlayerAdapter!!.seekTo(userSelectedPosition)
                }
            })
    }

    fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun apiCallAfterAudioComleteAt90Per(min: Long) {
        val calendar = Calendar.getInstance()
        val today = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val prevDate = calendar.getTime()
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val todayAsString = dateFormat.format(today)
        val prevDateAsString = dateFormat.format(prevDate)
        val previousDatePref = MyApplication.prefs!!.prevDate

        if (previousDatePref.equals(prevDateAsString, ignoreCase = true)) {
            currentStreak++
            MyApplication.prefs!!.prevDate = todayAsString
        } else {
            if (!previousDatePref.equals(todayAsString, ignoreCase = true)) {
                currentStreak = 1
                MyApplication.prefs!!.prevDate = todayAsString
            }
        }

        if (currentStreak > longestStreak) {
            setMeditationState(min, currentStreak, longestStreak + 1, totalSession + 1)
        } else {
            setMeditationState(min, currentStreak, longestStreak, totalSession + 1)
        }
    }

    private fun setMeditationState(
        min: Long,
        currentStreak: Int,
        longestStreak: Int,
        totalSession: Int
    ) {
        val userId = MyApplication.prefs!!.userId

        val userMap: HashMap<String, String> = HashMap<String, String>()
        userMap.put("userId", userId)
        userMap.put("currentStreak", currentStreak.toString())
        userMap.put("longestStreak", longestStreak.toString())
//        userMap.put("minuteMeditate", (min + totalMin).toString())
//        userMap.put("dailyMinutes", (min + totalDailyMin).toString())
        userMap.put("totalSessions", totalSession.toString())

        val call = ApiUtils.getAPIService().updateProfile(userMap)
        val meditationStateModelTemp = MeditationStateModel()
        meditationStateModelTemp.setUserId(userId.toInt())
        meditationStateModelTemp.setCurrentStreak(currentStreak)
        meditationStateModelTemp.setLongestStreak(longestStreak)
//        meditationStateModelTemp.setMinuteMeditated((min + totalMin).toFloat())
//        meditationStateModelTemp.setDailyMinutes((min + totalDailyMin).toFloat())
//        meditationStateModelTemp.setWeeklyMinutes((min + totalWeeklyMin).toFloat())
        meditationStateModelTemp.setTotalSessions(totalSession)

        db.meditationStateDao().insertAll(meditationStateModelTemp)
        meditationStateModel = meditationStateModelTemp


        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {
//                Toast.makeText(this@PlayActivity, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
//                    .show()
            }

            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {


                }
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivClose -> {
                var time :Long = 0
                val userId = MyApplication.prefs!!.userId
                if (mPlayerAdapter != null) {
                    time =
                        TimeUnit.MILLISECONDS.toSeconds(mPlayerAdapter!!.mediaPlayer.currentPosition.toLong())
                }
                val jsonObj = JSONObject()
                if (time > 0){
//                    jsonObj.put("chapterId", chapterId)
                    jsonObj.put("userId", userId)
                    jsonObj.put("tabName", "Moment")
                    jsonObj.put("tabId", momentModel!!.getMomentId())
                    jsonObj.put("totalSeconds", time.toString())
                    Utils.callUpdateSeconds("",jsonObj)
                }


                if (isMomentServiceRunning()) {
                    val intent = Intent(this, MusicService::class.java)
                    stopService(intent);
                }

                finish()
            }
            R.id.ivBackword -> {
                mPlayerAdapter!!.onBackword()
            }
            R.id.cvInternetToast -> {
                cvInternetToast.visibility = View.GONE
            }
            R.id.ivForward -> {
                mPlayerAdapter!!.onForward()
            }

            R.id.circleProgressNormal -> {
                if (downloadId != 0) {
                    val status = PRDownloader.getStatus(downloadId);
                    if (status.toString().equals("running", ignoreCase = true)) {
                        PRDownloader.cancel(downloadId)
                        circleProgressNormal.progress = 0
                        circleProgressNormal.visibility = View.GONE
                        ivDownload.visibility = View.VISIBLE
                    }
                }
            }
            R.id.ivDownload -> {
                if (downloadFileModelOld != null) {
                    val snackbar = Snackbar
                        .make(
                            parentLayout,
                            getString(R.string.str_delete_a_file),
                            Snackbar.LENGTH_INDEFINITE
                        )
                        .setAction(getString(R.string.str_yes), object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                val path =
                                    downloadFileModelOld!!.getAudioFilePath() + downloadFileModelOld!!.getFileName()
                                val myFile = File(path)
                                if (myFile.exists()) {
                                    myFile.delete()
                                    db.downloadDao().delete(downloadFileModelOld!!)
                                    downloadFileModelOld = null
                                    circleProgressNormal.progress = 0
                                    ivDownload.setImageResource(R.drawable.ic_download_light)

                                } else
                                    Toast.makeText(
                                        this@PlayActivity,
                                        "file not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }
                        })
                    snackbar.view.setOnClickListener {
                        snackbar.dismiss()
                    }
                    snackbar.show()
                } else {
                    if (audioPath != null && !audioPath.equals("null", ignoreCase = true))
                        if (downloadFileModelOld == null) {
                            if (checkInternetConnection()) {
                                checkDownloadPermmission()
                            }
                        }
                }
            }
            R.id.ivPlay -> {
                resumeOrPause()
            }
            R.id.ivPlay2 -> {
                resumeOrPause()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ivClose.performClick()
        if (isTaskRoot) {
            val intent = Intent(this@PlayActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun isMomentServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicService::class.java.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private fun checkDownloadPermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@PlayActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (checkInternetConnection())
                    downloadAudio()
            } else {
                managePermissions.checkPermissions()
            }
        } else {
            if (checkInternetConnection())
                downloadAudio()
        }
    }

    private fun downloadAudio() {
//        TODO("/data/user/0/com.clarity.meditation/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
        val dirPath = applicationInfo.dataDir
        circleProgressNormal.visibility = View.VISIBLE
        ivDownload.visibility = View.GONE
        downloadId = PRDownloader.download(audioPath, dirPath, filename)
            .build()
            .setOnStartOrResumeListener(object : OnStartOrResumeListener {
                override fun onStartOrResume() {

                }

            })
            .setOnPauseListener(object : OnPauseListener {
                override fun onPause() {

                }
            })
            .setOnCancelListener(object : OnCancelListener {
                override fun onCancel() {

                }
            })
            .setOnProgressListener(object : OnProgressListener {
                override fun onProgress(progress: Progress) {
                    val progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    circleProgressNormal.progress = progressPercent.toInt()
                }
            })
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    Log.e("@@@", error.toString() + "@@@")
                }

                override fun onDownloadComplete() {
                    circleProgressNormal.visibility = View.GONE
                    circleProgressNormal.progress = 0

                    ivDownload.visibility = View.VISIBLE
                    ivDownload.setImageResource(R.drawable.ic_download_done_white)

                    db.downloadDao().insertDownloadFile(downloadFileModel)
                    downloadFileModelOld = downloadFileModel
                }

            })
    }

    override fun onStop() {
        super.onStop()
        PRDownloader.cancel(downloadId)
    }


    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(requestCode, permissions, grantResults)
                if (isPermissionsGranted) {
                    downloadAudio()
                    toast("Permissions granted.")
                } else {
                    toast("Permissions denied.")
                }
                return
            }
        }
    }

}

