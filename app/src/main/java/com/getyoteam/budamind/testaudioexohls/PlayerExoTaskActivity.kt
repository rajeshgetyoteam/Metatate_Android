package com.getyoteam.budamind.testaudioexohls

import android.Manifest
import android.animation.ObjectAnimator
import android.app.ActivityManager
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.os.*
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.downloader.*
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.MainActivity
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import com.getyoteam.budamind.playback.MusicService
import com.getyoteam.budamind.utils.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.activity_exoplayer_player.*
import kotlinx.android.synthetic.main.custom_playback_control_minimal.*
import okhttp3.OkHttpClient
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


class PlayerExoTaskActivity : AppCompatActivity(), View.OnClickListener {

    private var isFirstTime: Boolean = true
    private val TAG = "MainActivity"
    private val animation = false
    var handler: Handler? = null
    private var isgetRewordDone: Boolean = false
    private var counddownTimer: CountDownTimer? = null
    private val PermissionsRequestCode = 123
    private var db: AppDatabase? = null
    private var mUserIsSeeking = false
    private var musicService: AudioService? = null
    private val streamUrl = "http://uk3.internet-radio.com:8021/listen.pls&t=.m3u"
    private val READ_PHONE_STATE_REQUEST_CODE = 22
    private var anim: ObjectAnimator? = null
    private var managePermissions: ManagePermissions? = null
    private var mMyPrefManager: MyPreferenceManager? = null
    private var courseModel: CourseListModel? = null
    private var meditationStateModel: MeditationStateModel? = null
    private var downloadFileModelOld: DownloadFileModel? = null
    private var chapterModel: ChapterListModel? = null
    private var audioPath: String? = ""
    private var headerSubTitle: String? = ""
    private var headerTitle: String? = ""
    private var modelName: String? = null
    private var fileId: Int = 0
    private var curuntDuration = 0
    private val dragging: Boolean = false
    private val customerId = ""
    private var isRewarded: Boolean? = null
    private var totalMin :Float = 0f
    private var totalDailyMin :Float  = 0f
    private var totalWeeklyMin:Float  = 0f
    private var taskId: String? = ""
    private var totalSession = 0
    private var longestStreak = 0
    private var currentStreak = 0
    private var isAPICallingDone = false
    private val seekTo15000: Long = 15000
    private var filename: String? = null
    private var downloadFileModel: DownloadFileModel? = null
    private var downloadId = 0
    lateinit var playerView: PlayerView
    private var broadcastReceiver: BroadcastReceiver? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exoplayer_player)
        playerView = findViewById(R.id.video_view)

        db = AppDatabase.getDatabase(this)
        mMyPrefManager = MyPreferenceManager(this)
        val list = Arrays.asList(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (intent.extras!!.containsKey("taskid")) {
            taskId = intent.extras!!.getString("taskid")

        }
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        ivClose!!.setOnClickListener(this)
        ivDownload!!.setOnClickListener(this)
        ivForward!!.setOnClickListener(this)
        circleProgressNormal!!.setOnClickListener(this)
        anim = ObjectAnimator.ofFloat(ivAnimatedImage, View.ROTATION, 0f, 90f)
        anim!!.setDuration(8000)
        anim!!.setRepeatCount(Animation.INFINITE)
        anim!!.setInterpolator(LinearInterpolator())
//        anim!!.start()

        //Retrieve Data from preference into ModelArrayList
        val gson = Gson()
        val jsonChapter = MyApplication.prefs!!.chapterModel
        val jsonCourse = MyApplication.prefs!!.courseModel
        val jsonMeditation = mMyPrefManager!!.getMeditationState()

        chapterModel = gson.fromJson(jsonChapter, ChapterListModel::class.java)
        courseModel = gson.fromJson(jsonCourse, CourseListModel::class.java)
        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel::class.java)
        headerTitle = courseModel!!.getCourseName()
        headerSubTitle = chapterModel!!.getChapterName()
        audioPath = chapterModel!!.getAudioUrl()
        fileId = chapterModel!!.getChapterId()!!
        audioPath = chapterModel!!.getAudioUrl()
        modelName = "chapterModel"

        isRewarded = false
        layRewarded.visibility = View.VISIBLE
//        if (isRewarded!!) {
//            layRewarded.visibility = View.GONE
//
//        } else {
//            layRewarded.visibility = View.VISIBLE
//        }

        laybacword.setOnClickListener {

//            Toast.makeText(
//                this,
//                "You cannot skip the content.",
//                Toast.LENGTH_SHORT
//            )
//                .show()
        }

        layForword.setOnClickListener {

//            Toast.makeText(
//                this,
//                "You cannot skip the content.",
//                Toast.LENGTH_SHORT
//            )
//                .show()
        }

        layProgress.setOnClickListener {

//            Toast.makeText(
//                this,
//                "You cannot skip the content.",
//                Toast.LENGTH_SHORT
//            )
//                .show()
        }

//        layRewarded.setOnClickListener {
//
//            Toast.makeText(
//                this,
//                "You cannot skip the content.",
//                Toast.LENGTH_SHORT
//            )
//                .show()
//        }

        tvPlayTitle!!.setText(headerTitle)
        tvPlaySubTitle!!.setText("Day " + headerSubTitle)

        if (audioPath != null && !audioPath!!.contains("data/")) {
            val fileSting = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
            filename = fileSting.replace("%20", " ")
            downloadFileModel = DownloadFileModel()
            downloadFileModel!!.setFileId(fileId)
            downloadFileModel!!.setFileName(filename!!)
            downloadFileModel!!.setFileType("audio")
            downloadFileModel!!.setImageFile("")
            downloadFileModel!!.setModelName(modelName!!)
            downloadFileModel!!.setMinute("")
            downloadFileModel!!.setSubTitle(chapterModel!!.getChapterName()!!)
            downloadFileModel!!.setTitle(courseModel!!.getCourseName())
            downloadFileModel!!.setAudioFilePath(getString(R.string.download_path) + packageName + "/")
        }

        val meditationStateModelArrayList = db!!.meditationStateDao().getAll()
        var meditationStateModelTemp = MeditationStateModel()
        if (meditationStateModelArrayList.size > 0)
            meditationStateModelTemp = meditationStateModelArrayList.get(0)

        currentStreak = meditationStateModelTemp.getCurrentStreak() as Int
        longestStreak = meditationStateModelTemp.getLongestStreak() as Int
        totalSession = meditationStateModelTemp.getTotalSessions() as Int
//        totalMin = meditationStateModelTemp.getMinuteMeditated() as Float
//        totalDailyMin = meditationStateModelTemp.getDailyMinutes() as Float
//        totalWeeklyMin = meditationStateModelTemp.getWeeklyMinutes() as Float

        downloadFileModelOld = db!!.downloadDao().loadDownloadFile(filename!!)
        if (downloadFileModelOld != null) {
            ivDownload.setImageResource(R.drawable.ic_download_done)
        }

        if (isSoundServiceRunning(MediaPlayerService::class.java.getName())) {
            val intent = Intent(this, MediaPlayerService::class.java)
            stopService(intent);
        }


        if (isSoundServiceRunning(MusicService::class.java.getName())) {
            val intent = Intent(this, MusicService::class.java)
            stopService(intent);
        }

        if (isSoundServiceRunning(AudioService::class.java.getName())) {
            anim!!.start()
            isFirstTime = false
        } else {
            MyApplication.prefs!!.prevChapter = ""
        }


        if (courseModel!!.getFocus() != null && courseModel!!.getFocus() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_yellow,
                R.color.color_card_ebb96b,
                R.drawable.ic_animation_yellow
            )
        } else if (courseModel!!.getStress() != null && courseModel!!.getStress() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_green,
                R.color.color_card_6bcfb0,
                R.drawable.ic_animation_green
            )
        } else if (courseModel!!.getSleep() != null && courseModel!!.getSleep() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_red,
                R.color.color_card_ec8c8c,
                R.drawable.ic_animation_red
            )
        } else if (courseModel!!.getSleep() != null && courseModel!!.getSleep() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_green,
                R.color.color_card_6bcfb0,
                R.drawable.ic_animation_green
            )
        } else if (courseModel!!.getHappiness() != null && courseModel!!.getHappiness() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_blue,
                R.color.color_card_7eb1e2,
                R.drawable.ic_animation_blue
            )
        } else if (courseModel!!.getAnxiety() != null && courseModel!!.getAnxiety() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_yellow,
                R.color.color_card_ebb96b,
                R.drawable.ic_animation_yellow
            )
        } else if (courseModel!!.getGratitute() != null && courseModel!!.getGratitute() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_green,
                R.color.color_card_6bcfb0,
                R.drawable.ic_animation_green
            )
        } else if (courseModel!!.getSelfEsteem() != null && courseModel!!.getSelfEsteem() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_red,
                R.color.color_card_ec8c8c,
                R.drawable.ic_animation_red
            )
        } else if (courseModel!!.getMeditate() != null && courseModel!!.getMeditate() == 1) {
            changeColorAsPerTheme(
                R.color.color_card_blue,
                R.color.color_card_7eb1e2,
                R.drawable.ic_animation_blue
            )
        } else {
            changeColorAsPerTheme(
                R.color.color_card_blue,
                R.color.color_card_7eb1e2,
                R.drawable.ic_animation_blue
            )
        }


        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val state = intent.getIntExtra("state", 0)
                val playerState = intent.getIntExtra("player_state", 0)
                if (state == PlaybackStateCompat.STATE_BUFFERING) {
                    Log.e("@@@", "STATE_BUFFERING")
                    progressBar.setVisibility(View.VISIBLE)
//                    anim!!.pause()
                } else if (state == PlaybackStateCompat.STATE_PLAYING) {
                    Log.e("@@@", "STATE_PLAYING")
                    if (isFirstTime) {
                        anim!!.start()
                        isFirstTime = false
                    } else {
                        anim!!.resume()
                    }
                    progressBar.setVisibility(View.INVISIBLE)

                    handler = Handler()
//                    handler.postDelayed({ //Write whatever to want to do after delay specified (1 sec)
//                        Log.d("Handler", "Running Handler")
//                    }, 1000)

                    handler!!.postDelayed(r, 1000);
//                    val min = TimeUnit.MILLISECONDS.toMinutes(playerView.player.duration).toInt()
//                    var f = min - TimeUnit.MILLISECONDS.toMinutes(playerView.player.currentPosition).toInt()
//
//                    var t = f * 60000
//
//                    var percentage = (t / 80) * 100
//
//                    if (counddownTimer != null) {
//                        counddownTimer!!.cancel()
//                    }
//                    counddownTimer =  object : CountDownTimer(percentage.toLong(), 1000) {
//                        override fun onTick(millisUntilFinished: Long) {
//                            Log.e("@@@", "seconds remaining: " + millisUntilFinished / 1000)
//                        }
//
//                        override fun onFinish() {
//                            if (!isgetRewordDone) {
//                                callRewardsAPI(chapterModel!!.getChapterId().toString())
//                            }
//
//                        }
//                    }.start()

                } else if (state == PlaybackStateCompat.STATE_PAUSED) {
                    Log.e("@@@", "STATE_PAUSED")
                    progressBar.setVisibility(View.INVISIBLE)
                    anim!!.pause()
                    if (handler != null) {
                        handler!!.removeCallbacks(r)
                    }

                } else if (state == PlaybackStateCompat.STATE_STOPPED) {
                    Log.e("@@@", "STATE_STOPPED")
                    var time: Long = 0
                    val userId = MyApplication.prefs!!.userId
                    if (playerView.player != null) {
//                        time = TimeUnit.MILLISECONDS.toSeconds(playerView.player.currentPosition).toLong()

                    }
                    time =  TimeUnit.MILLISECONDS.toSeconds(curuntDuration.toLong())
                    val jsonObj = JSONObject()
                    if (time > 0) {
                        jsonObj.put("chapterId", chapterModel!!.getChapterId())
                        jsonObj.put("userId", userId)
                        jsonObj.put("tabName", "Library")
                        jsonObj.put("tabId", chapterModel!!.getCourseId())
                        jsonObj.put("totalSeconds", time.toString())
                        Utils.callUpdateSeconds("", jsonObj)
                    }
                    if (playerState == ExoPlayer.STATE_ENDED) {
                        if (handler != null) {
                            handler!!.removeCallbacks(r)
                        }
//                        if (!isAPICallingDone) {
                        if (MyApplication.prefs!!.firstMeditationId == 0) {
                            MyApplication.prefs!!.firstMeditationId =
                                chapterModel!!.getCourseId()!!
                        }
                        val min =
                            TimeUnit.MILLISECONDS.toMinutes(playerView.player!!.getCurrentPosition())
                                .toInt()
                        val sec =
                            TimeUnit.MILLISECONDS.toSeconds(playerView.player!!.getCurrentPosition()) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    playerView.player!!.getCurrentPosition()
                                )
                            )
                        apiCallAfterAudioComleteAt90Per(min)
                            callRewardsAPI(chapterModel!!.getChapterId().toString())
//                        }
                        val chapterListPlayedModel = ChapterListPlayedModel()
                        chapterListPlayedModel.setAudioUrl(chapterModel!!.getAudioUrl()!!)
                        chapterListPlayedModel.setChapterId(chapterModel!!.getChapterId())
                        chapterListPlayedModel.setChapterName(chapterModel!!.getChapterName()!!)
                        chapterListPlayedModel.setCourseId(chapterModel!!.getCourseId()!!)
                        chapterListPlayedModel.setFreePaid(chapterModel!!.getFreePaid()!!)
                        chapterListPlayedModel.setCourseName(courseModel!!.getCourseName()!!)
                        db!!.chapterPlayedDao().insertAll(chapterListPlayedModel)
                        MyApplication.prefs!!.courseId = chapterModel!!.getCourseId()!!
//                        val isFirtsTime = MyApplication.prefs!!.isFirstTime
//                        if (isFirtsTime!!) {
//                            val intentReminderActivity =
//                                Intent(this@PlayerExoTaskActivity, ReminderActivity::class.java)
//                            intentReminderActivity.putExtra("chapterModel", chapterModel)
//                            intentReminderActivity.putExtra("courseModel", courseModel)
//                            intentReminderActivity.putExtra(
//                                "meditationStateModel",
//                                meditationStateModel
//                            )
//                            startActivity(intentReminderActivity)
//                        } else {
//                            val intentQuotesActivity =
//                                Intent(this@PlayerExoTaskActivity, QuotesActivity::class.java)
//                            intentQuotesActivity.putExtra("chapterModel", chapterModel)
//                            intentQuotesActivity.putExtra("courseModel", courseModel)
//                            intentQuotesActivity.putExtra(
//                                "meditationStateModel",
//                                meditationStateModel
//                            )
//                            startActivity(intentQuotesActivity)
//                        }
                    }
                    stopService()
                } else {
                    Log.e("@@@", "STATE_" + state)

                }
            }
        }

        getQuote()

    }

    private fun updateProgress() {

        val min = playerView.player!!.duration.toInt()
        val curntpos = playerView.player!!.currentPosition.toInt()
        curuntDuration = curntpos
        val percentage = (min / 100) * 80

        Log.e("@@@", "percentage: " + percentage)
        Log.e("@@@", "Totalmin: " + min)
        Log.e("@@@", "currntPosition: " + curntpos)

        if (curntpos >= percentage) {
            if (!isgetRewordDone) {
                callRewardsAPI(chapterModel!!.getChapterId().toString())
            }
        }


    }

    val r: Runnable = object : Runnable {
        override fun run() {
            handler!!.postDelayed(this, 1000)
            updateProgress()
        }
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
        val clarityAPI = retrofit.create(API::class.java)
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


    fun isSoundServiceRunning(serviceName: String): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private fun stopService() {
        val serviceIntent = Intent(this@PlayerExoTaskActivity, AudioService::class.java)
        stopService(serviceIntent)
        finish()
    }

    private fun changeColorAsPerTheme(
        colorCardYellow: Int,
        colorCardEbb96b: Int,
        icAnimationYellow: Int
    ) {
        exo_play.setColorFilter(
            resources.getColor(colorCardYellow),
            PorterDuff.Mode.SRC_IN
        )
        exo_pause.setColorFilter(
            resources.getColor(colorCardYellow),
            PorterDuff.Mode.SRC_IN
        )
//        exo_ffwd.setColorFilter(
//            resources.getColor(colorCardYellow),
//            PorterDuff.Mode.SRC_IN
//        )
//        exo_rew.setColorFilter(
//            resources.getColor(colorCardYellow),
//            PorterDuff.Mode.SRC_IN
//        )
        progressBar.indeterminateDrawable.setColorFilter(
            resources.getColor(colorCardYellow),
            PorterDuff.Mode.SRC_IN
        )
        exo_progress.setScrubberColor(resources.getColor(colorCardYellow))
        exo_progress.setPlayedColor(resources.getColor(colorCardYellow))
        exo_progress.setBufferedColor(resources.getColor(colorCardYellow))
        exo_progress.setUnplayedColor(resources.getColor(colorCardEbb96b))
        ivAnimatedImage.setImageResource(icAnimationYellow)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.ivClose -> {
                MyApplication.prefs!!.prevChapter = ""
                val userId = MyApplication.prefs!!.userId
                var time: Long = 0

                if (playerView.player != null) {
                    time = TimeUnit.MILLISECONDS.toSeconds(playerView.player!!.currentPosition).toLong()
                }
                val jsonObj = JSONObject()
                if (time > 0) {
                    jsonObj.put("chapterId", chapterModel!!.getChapterId())
                    jsonObj.put("userId", userId)
                    jsonObj.put("tabName", "Library")
                    jsonObj.put("tabId", chapterModel!!.getCourseId())
                    jsonObj.put("totalSeconds", time.toString())
                    Utils.callUpdateSeconds("", jsonObj)
                }
                stopService()
            }
            R.id.circleProgressNormal -> {
                if (downloadId != 0) {
                    val status = PRDownloader.getStatus(downloadId);
                    if (status.toString().equals("running", ignoreCase = true)) {
                        PRDownloader.cancel(downloadId)
                        circleProgressNormal!!.progress = 0
                        circleProgressNormal!!.visibility = View.GONE
                        ivDownload.visibility = View.VISIBLE
                    }
                }
            }
            R.id.ivDownload -> {
                if (downloadFileModelOld != null) {
                    val snackbar = Snackbar
                        .make(
                            parentLayout!!,
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
                                    db!!.downloadDao().delete(downloadFileModelOld!!)
                                    downloadFileModelOld = null
                                    circleProgressNormal!!.progress = 0
                                    ivDownload.setImageResource(R.drawable.ic_download_dark)

                                } else
                                    Toast.makeText(
                                        this@PlayerExoTaskActivity,
                                        "file not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                            }

                        })
                    snackbar.view.setOnClickListener {
                        snackbar.dismiss()
                    }
                    snackbar.show();
                } else {
                    if (audioPath != null && !audioPath.equals("null", ignoreCase = true))
                        if (downloadFileModelOld == null) {
                            if (checkInternetConnection()) {
                                checkDownloadPermmission()
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.str_check_internet_connection),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

            }
        }
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkDownloadPermmission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this@PlayerExoTaskActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                downloadAudio()
            } else {
                managePermissions!!.checkPermissions()
            }
        } else {
            downloadAudio()
        }
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions!!
                    .processPermissionsResult(requestCode, permissions, grantResults)

//                if (isPermissionsGranted) {
////                    downloadAudio()
//                    toast("Permissions granted.")
//                } else {
//                    toast("Permissions denied.")
//                }
                return
            }

        }
    }

    private fun downloadAudio() {
//        TODO("/data/user/0/com.mindfulness.greece/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
        val dirPath = getApplicationInfo().dataDir
        circleProgressNormal!!.visibility = View.VISIBLE
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

                    circleProgressNormal!!.progress = progressPercent.toInt()

                }
            })
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    Log.e("@@@", error.toString() + "@@@")
                }

                override fun onDownloadComplete() {
                    circleProgressNormal!!.visibility = View.GONE

                    circleProgressNormal!!.progress = 0


                    ivDownload.visibility = View.VISIBLE
                    ivDownload.setImageResource(R.drawable.ic_download_done)

                    //Todo: after download set course true to display in download
                    courseModel!!.setIsDownloaded(true)
                    val courseDownloadModel = CourseDownloadModel()
                    courseDownloadModel.setBanner(courseModel!!.getBanner()!!)
                    courseDownloadModel.setCourseId(courseModel!!.getCourseId()!!)
                    courseDownloadModel.setCourseName(courseModel!!.getCourseName()!!)
                    courseDownloadModel.setDescription(courseModel!!.getDescription()!!)
                    courseDownloadModel.setFromMinutes(courseModel!!.getFromMinutes()!!)
                    courseDownloadModel.setToMinutes(courseModel!!.getToMinutes()!!)
                    courseDownloadModel.setColorCode(courseModel!!.getColorCode()!!)
                    courseDownloadModel.setFocus(courseModel!!.getFocus()!!)
                    courseDownloadModel.setAnxiety(courseModel!!.getAnxiety()!!)
                    courseDownloadModel.setHappiness(courseModel!!.getHappiness()!!)
                    courseDownloadModel.setGratitute(courseModel!!.getGratitute()!!)
                    courseDownloadModel.setMeditate(courseModel!!.getMeditate()!!)
                    courseDownloadModel.setSelfEsteem(courseModel!!.getSelfEsteem()!!)
                    courseDownloadModel.setSleep(courseModel!!.getSleep()!!)
                    courseDownloadModel.setStress(courseModel!!.getStress()!!)
                    db!!.courseDownloadDao().insertAll(courseDownloadModel)

                    db!!.downloadDao().insertDownloadFile(downloadFileModel!!)
                    filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1);
                    downloadFileModelOld = downloadFileModel
                }

            })
    }


    override fun onBackPressed() {
        super.onBackPressed()
        if (isTaskRoot) {
            val intent = Intent(this@PlayerExoTaskActivity, MainActivity::class.java)
            startActivity(intent)
        }
        ivClose.performClick()

        if (handler != null) {
            handler!!.removeCallbacks(r)
        }
    }


    private fun apiCallAfterAudioComleteAt90Per(min: Int) {
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val prevDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val todayAsString = dateFormat.format(today)
        val prevDateAsString = dateFormat.format(prevDate)
        val previousDatePref = MyApplication.getPref().prevDate

        if (previousDatePref == prevDateAsString) {
            currentStreak++
            MyApplication.getPref().prevDate = todayAsString
        } else {
            if (previousDatePref != todayAsString) {
                currentStreak = 1
                MyApplication.getPref().prevDate = todayAsString
            }
        }

        if (currentStreak > longestStreak) {
            setMeditationState(min, currentStreak, longestStreak + 1, totalSession + 1)
        } else {
            setMeditationState(min, currentStreak, longestStreak, totalSession + 1)
        }
    }
    private fun callRewardsAPI(id: String) {

        val userId = MyApplication.prefs!!.userId

        val call = ApiUtils.getAPIService().getRewardsOnPlay(userId, "course", id)

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
                        isRewarded = true

                        try {
                            wanCoinDialog(Utils.format(commonModel.creditedCoins!!.toBigInteger()))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


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
        tvCoin.text = "You have won" +coin.replace("$","$"+"CHI")
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

    private fun setMeditationState(
        min: Int,
        currentStreak: Int,
        longestStreak: Int,
        totalSession: Int
    ) {
        val userId = MyApplication.getPref().userId

        val userMap = HashMap<String, String>()
        userMap["userId"] = userId
        userMap["currentStreak"] = currentStreak.toString()
        userMap["longestStreak"] = longestStreak.toString()
//        userMap["minuteMeditate"] = (min + totalMin).toString()
//        userMap["dailyMinutes"] = (min + totalDailyMin).toString()
        userMap["totalSessions"] = totalSession.toString().toString()

        val call = ApiUtils.getAPIService().updateProfile(userMap)
        val meditationStateModelTemp = MeditationStateModel()
        meditationStateModelTemp.setUserId(Integer.valueOf(userId))
        meditationStateModelTemp.setCurrentStreak(currentStreak)
        meditationStateModelTemp.setLongestStreak(longestStreak)
//        meditationStateModelTemp.setMinuteMeditated(min + totalMin.toFloat())
//        meditationStateModelTemp.setDailyMinutes(min + totalDailyMin.toFloat())
//        meditationStateModelTemp.setWeeklyMinutes(min + totalWeeklyMin.toFloat())
        meditationStateModelTemp.setTotalSessions(totalSession)

        db!!.meditationStateDao().insertAll(meditationStateModelTemp)
        meditationStateModel = meditationStateModelTemp

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {

            }

            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        val autoDownload = MyApplication.prefs!!.autoDownload
        if (checkInternetConnection()) {
            val intent = Intent(this, AudioService::class.java)
            startForegroundService(this, intent)
            if (downloadFileModelOld == null) {
                if (autoDownload!!) {
                    checkDownloadPermmission()
                }
            }
        } else {
            if (downloadFileModelOld == null) {
                cvInternetToast.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            } else {
                val intent = Intent(this, AudioService::class.java)
                startForegroundService(this, intent)
            }
        }

    }

    private fun startForegroundService(context: Context, intent: Intent): ComponentName? {
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver!!,
            IntentFilter("com.example.exoplayer.PLAYER_STATUS")
        )
        return if (Util.SDK_INT >= 26) {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            Util.startForegroundService(this,intent)
        } else {
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
            context.startService(intent)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is AudioService.VideoServiceBinder) {
                print("service audio service player set")
                val mServiceBinder = service
                musicService = mServiceBinder.getService()
                musicService!!.play(
                    audioPath!!,
                    headerTitle,
                    chapterModel!!.getChapterName(),
                    downloadFileModelOld,
                    chapterModel!!.getChapterId().toString(),
                    isRewarded
                )


                playerView.player = service.getExoPlayerInstance()
                playerView.controllerAutoShow = true
                playerView.showController()
                playerView.controllerHideOnTouch = false
            }
        }
    }

    override fun onDestroy() {
        if (checkInternetConnection()) {
            unbindService(connection)
            if (broadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver!!)
            }

        }

        if (handler != null) {
            handler!!.removeCallbacks(r)
        }

        super.onDestroy()

    }
}