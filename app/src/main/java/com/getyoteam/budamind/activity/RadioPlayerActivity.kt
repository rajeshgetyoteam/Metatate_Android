package com.getyoteam.budamind.activity

import android.animation.ObjectAnimator
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.SeekBar
import com.getyoteam.budamind.Model.ChapterListModel
import com.getyoteam.budamind.Model.CourseListModel
import com.getyoteam.budamind.Model.DownloadFileModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.playback.MusicNotificationManager
import com.getyoteam.budamind.playback.MusicService
import com.getyoteam.budamind.playback.PlayerAdapter
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.ManagePermissions
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.activity_radio_player.*


class RadioPlayerActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer
    private lateinit var mediaSource: MediaSource
    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private var ivAnimatedImage: ImageView? = null
    private val REQUEST_CODE: Int = 201
    private var downloadId: Int = 0
    private lateinit var courseModel: CourseListModel
    private var meditationStateModel: MeditationStateModel? = null
    private var downloadFileModelOld: DownloadFileModel? = null
    private var chapterModel: ChapterListModel? = null
    private var seekPer: Int = 0
    private lateinit var seekBar: SeekBar
    private lateinit var downloadFileModel: DownloadFileModel
    private var currentPossion: Long = 0
    private lateinit var filename: String
    private lateinit var modelName: String
    private var fileId: Int? = null
    private val PermissionsRequestCode = 123
    private val PermissionsRequestCodeAudio = 1234
    private lateinit var managePermissions: ManagePermissions
    private lateinit var managePermissionsAudio: ManagePermissions
    private var customerId: String = ""
    private var totalMin: Int = 0
    private var totalSession: Int = 0
    private var longestStreak: Int = 0
    private var currentStreak: Int = 0
    private var headerSubTitle: String = ""
    private var isAPICallingDone: Boolean = false
    private lateinit var intentData: Bundle
    private var audioPath: String = ""
    private var headerTitle: String = ""
    private lateinit var db: AppDatabase
    private lateinit var mediaPlayer: MediaPlayer
    private var mediaFileLengthInMilliseconds: Long = 0
    private var mPlayerAdapter: PlayerAdapter? = null
    private var mMusicService: MusicService? = null
    private var mMusicNotificationManager: MusicNotificationManager? = null
    private var mUserIsSeeking = false
    private var anim: ObjectAnimator? = null
    private var mIsBound: Boolean? = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_radio_player)

//        player = ExoPlayerFactory.newSimpleInstance(this)

        dataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoPlayerSample"))

        val gson = Gson()
        val jsonChapter = MyApplication.prefs!!.chapterModel
        val jsonCourse = MyApplication.prefs!!.courseModel
        val jsonMeditation = MyApplication.prefs!!.stateModel
        chapterModel = gson.fromJson(jsonChapter, ChapterListModel::class.java)
        courseModel = gson.fromJson(jsonCourse, CourseListModel::class.java)
        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel::class.java)
        headerTitle = chapterModel!!.getCourseName().toString()
        headerSubTitle = chapterModel!!.getChapterName().toString()
        audioPath = chapterModel!!.getAudioUrl().toString()
        fileId = chapterModel!!.getChapterId()
        audioPath = chapterModel!!.getAudioUrl().toString()
        modelName = "chapterModel"

//        mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(audioPath))

        anim = ObjectAnimator.ofFloat(ivAnimatedImage, View.ROTATION, 0f, 90f)
        anim!!.setDuration(3000);
        anim!!.setRepeatCount(Animation.INFINITE);
        anim!!.setInterpolator(LinearInterpolator());
        anim!!.start();

        with(player) {
            prepare(mediaSource)
            btnStart.setOnClickListener {
                playWhenReady = true
                anim!!.resume();
            }

            btnStop.setOnClickListener {
                playWhenReady = false
                anim!!.pause();
            }
        }

    }

    override fun onDestroy() {
        player.playWhenReady = false
        super.onDestroy()
    }

    companion object {
        const val RADIO_URL = "http://airspectrum.cdnstream1.com:8114/1648_128"
    }
}
