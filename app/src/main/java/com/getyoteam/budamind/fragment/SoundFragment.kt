//package com.getyoteam.budamind.fragment
//
//import android.Manifest
//import android.app.ActivityManager
//import android.app.Dialog
//import android.content.*
//import android.content.pm.PackageManager
//import android.media.MediaPlayer
//import android.net.ConnectivityManager
//import android.os.*
//import android.util.Log
//import android.view.*
//import android.widget.ImageView
//import android.widget.SeekBar
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.downloader.*
//import com.facebook.login.LoginManager
//import com.getyoteam.budamind.Model.*
//import com.getyoteam.budamind.MyApplication
//import com.getyoteam.budamind.R
//import com.getyoteam.budamind.activity.SignInActivity
//import com.getyoteam.budamind.adapter.SoundAdapterNew
//import com.getyoteam.budamind.interfaces.ApiUtils
//import com.getyoteam.budamind.playback.MusicService
//import com.getyoteam.budamind.testaudioexohls.AudioService
//import com.getyoteam.budamind.utils.*
//import com.google.android.gms.auth.api.Auth
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.common.api.ResultCallback
//import com.google.android.gms.common.api.Status
//import kotlinx.android.synthetic.main.actionbar_layout.*
//import kotlinx.android.synthetic.main.fragment_sound.*
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.IOException
//import java.lang.IndexOutOfBoundsException
//import java.util.*
//
//
//class SoundFragment(t: String, id: String?, taskid: String?) : Fragment(),
//    SwipeRefreshLayout.OnRefreshListener,
//    View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnBufferingUpdateListener,
//    SoundAdapterNew.OnSoundAdapterInteractionListener {
//    var soundAdapter: SoundAdapterNew? = null
//
//    var type = t
//    var curuntId = id
//    var taskId = taskid
//    private var isgetRewordDone: Boolean = false
//    private var isFirstTime: Boolean? = false
//    private var mIsBound: Boolean? = false
//    private var isPlaying: Boolean = false
//    private var downloadFileModelOld: DownloadFileModel? = null
//    private var downloadFileModel: DownloadFileModel? = null
//    private var filename: String? = ""
//    private var audioPath: String? = ""
//    private var downloadId: Int = 0
//    private var prevIndexSound: Int = -1
//    private var curuntSongId: String = ""
//    private var counddownTimer: CountDownTimer? = null
//    private var seekPer: Int = 0
//    val BROADCAST_ACTION = "com.clarity.meditation.utils.displayevent"
//    val Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio"
//    private lateinit var managePermissions: ManagePermissions
//    lateinit var mGoogleApiClient: GoogleApiClient
//    var intent: Intent? = null
//    val handler = Handler()
//    private lateinit var mediaPlayer: MediaPlayer
//    private var player: MediaPlayerService? = null
//    internal var serviceBound = false
//    var isItemClicked = false
//    private val PermissionsRequestCode = 123
//    private var userId: String = ""
//    private lateinit var db: AppDatabase
//    private lateinit var soundArrayList: ArrayList<SoundListModel>
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_sound, container, false)
//    }
//
//    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//
//
//    }
//
//    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
////        tvHeader.text = getText(R.string.str_sound)
//        userId = MyApplication.prefs!!.userId
//        MyApplication.prefs!!.isItemClicked = false
//        intent = Intent(BROADCAST_ACTION)
//        db = AppDatabase.getDatabase(requireContext())
//        val list = listOf<String>(
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//        )
//        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)
//
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
//            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//            .build()
//        mGoogleApiClient.connect()
//
//
//        val c1 = ContextCompat.getColor(requireContext(), R.color.app_pink_color)
//        swipeToRefresh.setColorSchemeColors(c1)
//
//        mediaPlayer = MediaPlayer()
//        soundArrayList = ArrayList<SoundListModel>()
//        rvSoundList!!.layoutManager = LinearLayoutManager(requireContext())
//
//
//        if (checkInternetConnection()) {
//            cvInternetToast.visibility = View.GONE
//        } else {
//            cvInternetToast.visibility = View.VISIBLE
//        }
////        rvSoundList.addOnItemTouchListener(object : CustomTouchListener(requireContext(), object :
////            onItemClickListener {
////            override fun onClick(view: View?, index: Int) {
////
////                isItemClicked = MyApplication.prefs!!.isItemClicked!!
////                if (isItemClicked) {
////                    Toast.makeText(
////                        activity,
////                        "Player In Progress...",
////                        Toast.LENGTH_SHORT
////                    ).show()
////
////                } else {
////
////                    MyApplication.prefs!!.isItemClicked = true
////                    if (checkInternetConnection()) {
////                        cvInternetToast.visibility = View.GONE
////                    } else {
////                        cvInternetToast.visibility = View.VISIBLE
////                    }
////                    if (isMomentServiceRunning(MusicService::class.java.name)) {
////                        val intent = Intent(requireActivity(), MusicService::class.java)
////                        requireActivity().stopService(intent)
////                    }
////                    if (isMomentServiceRunning(AudioService::class.java.name)) {
////                        val intent = Intent(requireActivity(), AudioService::class.java)
////                        requireActivity().stopService(intent)
////                    }
////
////                    val soundListModel = soundArrayList.get(index)
////                    audioPath = soundListModel.getAudio()
////                    if (audioPath == null || soundListModel.getAudio().isNullOrEmpty()) {
////
////                        Toast.makeText(
////                            activity,
////                            getString(R.string.str_something_went_wrong),
////                            Toast.LENGTH_SHORT
////                        ).show()
////                        MyApplication.prefs!!.isItemClicked = false
////                    } else {
////                        filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
////                        downloadFileModel = DownloadFileModel()
////                        downloadFileModel!!.setFileId(soundListModel.getSoundId()!!)
////                        downloadFileModel!!.setFileName(filename!!)
////                        downloadFileModel!!.setFileType("audio")
////                        downloadFileModel!!.setImageFile("")
////                        downloadFileModel!!.setModelName("soundModel")
////                        downloadFileModel!!.setMinute(soundListModel.getMinutes()!!)
////                        downloadFileModel!!.setSubTitle(soundListModel.getSubtitle()!!)
////                        downloadFileModel!!.setTitle(soundListModel.getTitle())
////                        downloadFileModel!!.setAudioFilePath(getString(R.string.download_path) + context!!.packageName + "/")
////
////
////                        val autoDownload = MyApplication.prefs!!.autoDownload
////                        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
////                        if (downloadFileModelOld != null) {
////
////                        } else {
////                            if (autoDownload!!)
////                                checkDownloadPermmission()
////                        }
////
////                        if (checkInternetConnection() || downloadFileModelOld != null) {
////                            if (prevIndexSound != index) {
////                                if (isPlaying || isMyServiceRunning()) {
////                                    soundListModel.setPlaying(false)
////                                    if (prevIndexSound != -1) {
////                                        isItemClicked = false
////                                        soundArrayList.get(prevIndexSound).setPlaying(false)
////                                        rvSoundList.adapter!!.notifyDataSetChanged()
////                                    }
////                                }
////                                if (soundListModel.getFreePaid()
////                                        .equals("Free", ignoreCase = true)
////                                ) {
////                                    loadSound(index, soundListModel)
////                                } else {
////                                    MyApplication.prefs!!.isItemClicked = false
////
////                                    if (soundListModel.purchased!!) {
////                                        loadSound(index, soundListModel)
////                                    } else {
////                                        purchaseDialog(soundListModel.getSoundId()!!)
////                                    }
////                                }
////                                rvSoundList.adapter!!.notifyDataSetChanged()
////
////                            } else {
////                                isItemClicked = false
////                                handler.removeCallbacks(sendUpdatesToUI)
////                                handler.postDelayed(sendUpdatesToUI, 0)
////                                togglePlayPause()
////                            }
////                        }
////                    }
////                }
////            }
////        }) {})
//
//        swipeToRefresh.setOnRefreshListener(this)
//        mediaPlayer.setOnBufferingUpdateListener(this)
//    }
//
//    private fun callTaskRewardsAPI() {
//
//        val userId = MyApplication.prefs!!.userId
//
//
//        val call = ApiUtils.getAPIService().claimForTask(userId, taskId)
//
//        call.enqueue(object : Callback<responceTaskModel> {
//            override fun onFailure(call: Call<responceTaskModel>, t: Throwable) {
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//            override fun onResponse(
//                call: Call<responceTaskModel>,
//                response: Response<responceTaskModel>
//            ) {
//                if (response.code() == 200) {
//
//                    val commonModel = response.body()
//                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
//
//                        taskRewardDialog(Utils.format(commonModel.creditedCoins!!.toBigInteger()))
//
//                    }
//                }
//            }
//        })
//    }
//
//    fun taskRewardDialog(coin: String) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_congratulation_view)
//        val window = dialog.window
//        window!!.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT
//        )
//        window.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                requireContext(),
//                android.R.color.transparent
//            )
//        )
//        val close: ImageView
//        val tvCoin: TextView
//        val tvWallate: TextView
//        close = dialog.findViewById(R.id.ivClose)
//        tvCoin = dialog.findViewById(R.id.tvCoin)
//        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
//        tvCoin.text = "You have won " + coin.replace("$","$"+"CHI")
//        close.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        tvWallate.setOnClickListener {
//            dialog.dismiss()
////            val intent = Intent(applicationContext, WalletActivity::class.java)
////            startActivity(intent)
//
//        }
//        dialog.show()
//    }
//
//    fun purchaseDialog(soundId: Int) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_purchase_view)
//        val window = dialog.window
//        window!!.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT
//        )
//        window.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                requireContext(),
//                android.R.color.transparent
//            )
//        )
//        val close: ImageView
//        val tvYes: TextView
//        val tvNo: TextView
//        close = dialog.findViewById(R.id.ivClose)
//        tvYes = dialog.findViewById(R.id.tvyes)
//        tvNo = dialog.findViewById(R.id.tvNo)
//        close.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        tvYes.setOnClickListener {
//            callApiForPurchase(soundId)
//            dialog.dismiss()
//        }
//
//        tvNo.setOnClickListener {
//
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//
//    fun callApiForPurchase(soundId: Int) {
//        val userId = MyApplication.prefs!!.userId
//        swipeToRefresh.isRefreshing = true
//
//        val call = ApiUtils.getAPIService().purchaseSounds(userId, soundId.toString())
//
//        call.enqueue(object : Callback<responcePurchaseModel> {
//            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
//                if (swipeToRefresh != null)
//                    swipeToRefresh.isRefreshing = false
//                Toast.makeText(
//                    requireContext(),
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//
//            override fun onResponse(
//                call: Call<responcePurchaseModel>,
//                response: Response<responcePurchaseModel>
//            ) {
//                if (response.code() == 200) {
//
//                    val commonModel = response.body()
//                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
//                        Toast.makeText(
//                            requireContext(),
//                            commonModel!!.message,
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        getSoundDetail()
//
//                    } else {
//                        if (swipeToRefresh != null)
//                            swipeToRefresh.isRefreshing = false
//                        Toast.makeText(
//                            requireContext(),
//                            commonModel.message,
//                            Toast.LENGTH_SHORT
//                        )
//                            .show()
//                    }
//                } else {
//                    if (swipeToRefresh != null)
//                        swipeToRefresh.isRefreshing = false
//
//                    Toast.makeText(
//                        requireContext(),
//                        getString(R.string.str_something_went_wrong),
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
//            }
//        })
//    }
//
//    private fun signOut() {
//        val intent = Intent(requireActivity(), SignInActivity::class.java)
//        startActivity(intent)
//        requireActivity().finish()
//    }
//
//    private fun logOutAfterDialog() {
//        MyApplication.prefs?.userId = ""
//        MyApplication.isSoundAPI = true
//        MyApplication.isHomeAPI = true
//        MyApplication.isLibraryAPI = true
//        MyApplication.isProfileAPI = true
//        MyApplication.prefs!!.profilePic = ""
//        MyApplication.prefs!!.first_name = ""
//        MyApplication.prefs!!.last_name = ""
//
//        db.meditationStateDao().deleteAll()
//        db.chapterPlayedDao().deleteAllData()
//        db.goalDao().deleteAll()
//
//        val loginThrough = MyApplication.prefs!!.loginType.toString()
//
//        if (loginThrough.equals("Facebook", ignoreCase = true)) {
//            LoginManager.getInstance().logOut()
//            signOut()
//        } else if (loginThrough.equals("gmail", ignoreCase = true)) {
//            googleSignOut()
//        } else {
//            signOut()
//        }
//        requireActivity().finish()
//    }
//
//    private fun googleSignOut() {
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//            object : ResultCallback<Status> {
//                override fun onResult(p0: Status) {
//                    signOut()
//                }
//            })
//    }
//
//    private fun loadSound(index: Int, soundListModel: SoundListModel) {
//        try {
//            curuntSongId = soundListModel.getSoundId().toString()
//            prevIndexSound = index
//            MyApplication.prefs!!.prevIndexSound = prevIndexSound
//            soundArrayList.get(prevIndexSound).setPlaying(true)
//            for (i in 0..soundArrayList.size - 1) {
//                if (i == prevIndexSound) {
//                    if (downloadFileModelOld != null) {
//                        soundArrayList.get(i).setFirstTime(false)
//                    } else {
//                        soundArrayList.get(prevIndexSound).setFirstTime(true)
//                    }
//                } else {
//                    soundArrayList.get(i).setFirstTime(false)
//                }
//            }
//            rvSoundList.adapter!!.notifyDataSetChanged()
//            playAudio(index)
//
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
//
//    val sendUpdatesToUI = object : Runnable {
//        override fun run() {
//            DisplayLoggingInfo()
//        }
//    }
//
//    fun isMyServiceRunning(): Boolean {
//        val manager =
//            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (MediaPlayerService::class.java.name.equals(service.service.className)) {
//                return true
//            }
//        }
//        return false
//    }
//
//    fun DisplayLoggingInfo() {
//        val sendPlayingToBrodcast: Boolean
//        sendPlayingToBrodcast = !isPlaying
//        intent!!.putExtra("isPlaying", sendPlayingToBrodcast)
//        intent!!.putExtra("isClickedFromApp", true)
//        requireActivity().sendBroadcast(intent)
//    }
//
//    private fun checkDownloadPermmission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE
//                )
//                == PackageManager.PERMISSION_GRANTED
//            ) {
//                if (checkInternetConnection())
//                    downloadAudio()
//            } else {
//                managePermissions.checkPermissions()
//            }
//        } else {
//            if (checkInternetConnection())
//                downloadAudio()
//        }
//    }
//
//    private fun downloadAudio() {
////        TODO("/data/user/0/com.clarity.meditation/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
//        val dirPath = requireActivity().applicationInfo.dataDir
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
//                }
//            })
//            .start(object : OnDownloadListener {
//                override fun onError(error: com.downloader.Error?) {
//                }
//
//                override fun onDownloadComplete() {
//
//                    db.downloadDao().insertDownloadFile(downloadFileModel!!)
//                    filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
//                    if (filename != null)
//                        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
//                }
//
//            })
//    }
//
//
//    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//        if (mediaPlayer != null && fromUser) {
//            mediaPlayer.seekTo(progress)
//            Log.e("@@@", progress.toString() + " * " + seekPer)
////            if (progress > 80) {
////                callRewardsAPI(curuntSongId)
////            }
//        }
//    }
//
//    override fun onBufferingUpdate(mp: MediaPlayer?, percent: Int) {
////        song_progressbar.secondaryProgress = percent * seekPer
//        Log.e("@@@", percent.toString() + " * " + seekPer)
//    }
//
//    private fun callRewardsAPI(id: String) {
//
//        val userId = MyApplication.prefs!!.userId
//
//
//        val call = ApiUtils.getAPIService().getRewardsOnPlay(userId, "sound", id)
//
//        call.enqueue(object : Callback<responceRewarModel> {
//            override fun onFailure(call: Call<responceRewarModel>, t: Throwable) {
//
//            }
//
//            override fun onResponse(
//                call: Call<responceRewarModel>,
//                response: Response<responceRewarModel>
//            ) {
//                if (response.code() == 200) {
//                    val commonModel = response.body()
//                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
//                        isgetRewordDone = true
//                        wanCoinDialog(Utils.format(commonModel.creditedCoins!!.toBigInteger()))
//                    }
//                }
//            }
//        })
//    }
//
//    fun wanCoinDialog(coin: String) {
//        val dialog = Dialog(requireContext())
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.dialog_congratulation_view)
//        val window = dialog.window
//        window!!.setLayout(
//            WindowManager.LayoutParams.MATCH_PARENT,
//            WindowManager.LayoutParams.MATCH_PARENT
//        )
//        window.setBackgroundDrawable(
//            ContextCompat.getDrawable(
//                requireContext(),
//                android.R.color.transparent
//            )
//        )
//        val close: ImageView
//        val tvCoin: TextView
//        val tvWallate: TextView
//        close = dialog.findViewById(R.id.ivClose)
//        tvCoin = dialog.findViewById(R.id.tvCoin)
//        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
//        tvCoin.text = "You have won " + coin.replace("$","$"+"CHI")
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
//    override fun onRefresh() {
//        if (isAdded) {
//            if (checkInternetConnection()) {
//                cvInternetToast.visibility = View.GONE
//                getSoundDetail()
//            } else {
//                cvInternetToast.visibility = View.VISIBLE
//                if (swipeToRefresh != null)
//                    swipeToRefresh.isRefreshing = false
//            }
//        }
//    }
//
//    override fun onClick(v: View?) {
////        togglePlayPause()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (isAdded) {
//            prevIndexSound = MyApplication.prefs!!.prevIndexSound
//            isPlaying = MyApplication.prefs!!.isPlaying!!
//            if (prevIndexSound > -1)
//                serviceBound = true
//
//            loadDataFromDB()
//
//            requireActivity().registerReceiver(
//                broadcastReceiver,
//                IntentFilter("com.clarity.meditation.utils.displayevent")
//            )
//
////            swipeToRefresh.post(object : Runnable {
////                override fun run() {
////                    if (isAdded()) {
////                        if (checkInternetConnection()) {
////                            if (MyApplication.isSoundAPI)
////                                getSoundDetail()
////                        } else {
////                            cvInternetToast.visibility = View.VISIBLE
////                        }
////                    }
////                }
////            })
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        requireActivity().unregisterReceiver(broadcastReceiver)
//    }
//
//    private fun getSoundDetail() {
//        swipeToRefresh.isRefreshing = true
//        val userId = MyApplication.prefs!!.userId
//        val call = ApiUtils.getAPIService().getSoundList(userId)
//
//        call.enqueue(object : Callback<SoundResponse> {
//            override fun onFailure(call: Call<SoundResponse>, t: Throwable) {
//                if (isAdded) {
//                    if (swipeToRefresh != null)
//                        swipeToRefresh.isRefreshing = false
//
//                    Toast.makeText(
//                        activity,
//                        getString(R.string.str_something_went_wrong),
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                }
//            }
//
//            override fun onResponse(call: Call<SoundResponse>, response: Response<SoundResponse>) {
//                if (response.code() == 200) {
//                    if (isAdded) {
//                        if (swipeToRefresh != null) {
//                            swipeToRefresh.isRefreshing = false
//                            MyApplication.isSoundAPI = false
//                            val soundResponse = response.body()!!
//                            if (soundResponse.status.equals(getString(R.string.str_success))) {
//                                val data: List<SoundListModel> = soundResponse.soundList!!
//                                soundArrayList.clear()
//                                soundArrayList.addAll(data)
//                                if (soundArrayList.size > 0) {
//                                    db.soundDao().deleteAll()
//                                }
//                                for (i in 0 until soundArrayList.size) {
//                                    val soundListModel = soundArrayList.get(i)
//                                    soundListModel.setPlaying(false)
//                                    soundListModel.setFirstTime(false)
//                                    db.soundDao().insertAll(soundListModel)
//                                }
//
//                                loadDataFromDB()
//
//
//                            } else if (soundResponse.status.equals(
//                                    getString(R.string.str_invalid_token),
//                                    ignoreCase = true
//                                )
//                            ) {
//                                logOutAfterDialog()
//                            } else {
//                                Toast.makeText(
//                                    activity,
//                                    getString(R.string.str_something_went_wrong),
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//                    }
//                } else {
//                    if (swipeToRefresh != null)
//                        swipeToRefresh.isRefreshing = false
//
//                    Toast.makeText(
//                        activity,
//                        getString(R.string.str_something_went_wrong),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        })
//    }
//
//    private fun togglePlayPause() {
//        if (isPlaying) {
////            mediaPlayer.pause()
//
//            soundArrayList.get(prevIndexSound).setPlaying(false)
//            rvSoundList.adapter!!.notifyDataSetChanged()
//
//            if (counddownTimer != null) {
//                counddownTimer!!.cancel()
//            }
//
////            play_pause_btn.setImageResource(R.drawable.ic_play_black)
////            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar)
//        } else {
////            mediaPlayer.start()
//            soundArrayList.get(prevIndexSound).setPlaying(true)
//            rvSoundList.adapter!!.notifyDataSetChanged()
////            play_pause_btn.setImageResource(R.drawable.ic_pause_black)
////            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0)
//        }
//    }
//
//    private fun loadDataFromDB() {
//        if (soundArrayList.size > 0) {
//            soundArrayList.clear()
//        }
//        soundArrayList.addAll(db.soundDao().getAll().reversed() as ArrayList<SoundListModel>)
//        if (prevIndexSound != -1 && soundArrayList.size > 0)
//            if (isPlaying)
//                soundArrayList.get(prevIndexSound).setPlaying(true)
//
//        soundAdapter = SoundAdapterNew(soundArrayList, requireContext(), this)
//        rvSoundList.adapter = soundAdapter
//
//        if (type.equals("Task")) {
//
//            Handler().postDelayed({
//                context?.let {
//                    soundAdapter!!.performClick(curuntId!!.toInt())
//                }
//            }, 100)
//
//        }
//
//    }
//
//    val broadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent?) {
//
////            if (counddownTimer != null) {
////                counddownTimer!!.cancel()
////            }
//            updateUI(intent!!)
//        }
//    }
//
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (counddownTimer != null) {
//            counddownTimer!!.cancel()
//        }
//    }
//
//    fun updateUI(intent: Intent) {
//        isPlaying = intent.getBooleanExtra("isPlaying", false)
//        isFirstTime = intent.getBooleanExtra("isFirstTime", false)
//
//        try {
//            if (rvSoundList != null && soundArrayList.size > prevIndexSound) {
//
//                soundArrayList.get(prevIndexSound).setPlaying(isPlaying)
//                soundArrayList.get(prevIndexSound).setFirstTime(isFirstTime)
//                rvSoundList.adapter!!.notifyDataSetChanged()
//            }
//
//        }catch (e :Exception){
//            e.printStackTrace()
//        }catch (e : ArrayIndexOutOfBoundsException){
//            e.printStackTrace()
//        }
//
//        if(!isPlaying){
//            if (counddownTimer != null) {
//                counddownTimer!!.cancel()
//            }
//        }
//
//    }
//
//    fun isMomentServiceRunning(serviceName: String): Boolean {
//        val manager =
//            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceName.equals(service.service.className)) {
//                return true
//            }
//        }
//        return false
//    }
//
//    override fun onPause() {
//        super.onPause()
//        doUnbindService()
//    }
//
//    private fun doUnbindService() {
//        if (mIsBound!!) {
//            // Detach our existing connection.
//            requireActivity().unbindService(serviceConnection)
//            mIsBound = false
//        }
//    }
//
//
//    //Binding this Client to the AudioPlayer Service
//    private val serviceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            val binder = service as MediaPlayerService.LocalBinder
//            player = binder.service
//            serviceBound = true
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {
//            serviceBound = false
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putBoolean("serviceStatus", serviceBound)
//    }
//
//    private fun playAudio(audioIndex: Int) {
//
//        isgetRewordDone = false
//        //Check is service is active
//
//        if (counddownTimer != null) {
//            counddownTimer!!.cancel()
//        }
////        var audioUrl = soundArrayList.get(audioIndex).getAudio()
//
////        val uri: Uri = Uri.parse(audioUrl)
////        val mmr = MediaMetadataRetriever()
////        mmr.setDataSource(requireContext(), uri)
////        val durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
////        val millSecond = durationStr.toInt()
//
//        var m = soundArrayList.get(audioIndex).getMinutes()
//        var s = soundArrayList.get(audioIndex).getSeconds()
//
//        var t1 = m!!.toInt() * 60000
//        var t2 = s!!.toInt() * 1000
//
//        var time = t1 + t2
//
//        var percentage = (time / 100) * 80
//
//
//        if (!serviceBound) {
//            //Store Serializable audioList to SharedPreferences
//            val storage = StorageUtil(requireContext())
//            val arrayMoment = ArrayList<MomentListModel>()
//            storage.storeAudio(soundArrayList, arrayMoment)
//            storage.storeAudioIndex(audioIndex)
//
//            val playerIntent = Intent(requireActivity(), MediaPlayerService::class.java)
//            requireActivity().startService(playerIntent)
//            requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
//            mIsBound = true
//
//        } else {
//            //Store the new audioIndex to SharedPreferences
//            val storage = StorageUtil(requireContext())
//            storage.storeAudioIndex(audioIndex)
//
//            //Service is active
//            //Send a broadcast to the service -> PLAY_NEW_AUDIO
//            val broadcastIntent = Intent(Broadcast_PLAY_NEW_AUDIO)
//            requireActivity().sendBroadcast(broadcastIntent)
//
//        }
//
//        counddownTimer = object : CountDownTimer(percentage.toLong(), 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                Log.e("@@@", "seconds remaining: " + millisUntilFinished / 1000)
//            }
//
//            override fun onFinish() {
//                if (!isgetRewordDone) {
//
//                    if (type.equals("Task")) {
//                        callTaskRewardsAPI()
//                    } else {
//                        callRewardsAPI(soundArrayList.get(prevIndexSound).getSoundId().toString())
//                    }
//                }
//            }
//        }.start()
//    }
//
//    private fun checkInternetConnection(): Boolean {
//        val connectivityManager =
//            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val networkInfo = connectivityManager.activeNetworkInfo
//        return networkInfo != null && networkInfo.isConnected
//    }
//
//    override fun onSoundAdapterInteractionListener(
//        courseModel: SoundListModel,
//        index: Int,
//        s: String
//    ) {
//        type = s
//
//        if (counddownTimer != null) {
//            counddownTimer!!.cancel()
//        }
//
//        isItemClicked = MyApplication.prefs!!.isItemClicked!!
//        if (isItemClicked) {
//            Toast.makeText(
//                activity,
//                "Player In Progress...",
//                Toast.LENGTH_SHORT
//            ).show()
//
//        } else {
//
//            MyApplication.prefs!!.isItemClicked = true
//            if (checkInternetConnection()) {
//                cvInternetToast.visibility = View.GONE
//            } else {
//                cvInternetToast.visibility = View.VISIBLE
//            }
//            if (isMomentServiceRunning(MusicService::class.java.name)) {
//                val intent = Intent(requireActivity(), MusicService::class.java)
//                requireActivity().stopService(intent)
//            }
//            if (isMomentServiceRunning(AudioService::class.java.name)) {
//                val intent = Intent(requireActivity(), AudioService::class.java)
//                requireActivity().stopService(intent)
//            }
//
//            val soundListModel = soundArrayList.get(index)
//            audioPath = soundListModel.getAudio()
//            if (audioPath == null || soundListModel.getAudio().isNullOrEmpty()) {
//
//                Toast.makeText(
//                    activity,
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                ).show()
//                MyApplication.prefs!!.isItemClicked = false
//            } else {
//                filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
//                downloadFileModel = DownloadFileModel()
//                downloadFileModel!!.setFileId(soundListModel.getSoundId()!!)
//                downloadFileModel!!.setFileName(filename!!)
//                downloadFileModel!!.setFileType("audio")
//                downloadFileModel!!.setImageFile("")
//                downloadFileModel!!.setModelName("soundModel")
//                downloadFileModel!!.setMinute(soundListModel.getMinutes()!!)
//                downloadFileModel!!.setSubTitle(soundListModel.getSubtitle()!!)
//                downloadFileModel!!.setTitle(soundListModel.getTitle())
//                downloadFileModel!!.setAudioFilePath(getString(R.string.download_path) + context!!.packageName + "/")
//
//
//                val autoDownload = MyApplication.prefs!!.autoDownload
//                downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
//                if (downloadFileModelOld != null) {
//
//                } else {
//                    if (autoDownload!!)
//                        checkDownloadPermmission()
//                }
//
//                if (checkInternetConnection() || downloadFileModelOld != null) {
//                    if (prevIndexSound != index) {
//                        if (isPlaying || isMyServiceRunning()) {
//                            soundListModel.setPlaying(false)
//                            if (prevIndexSound != -1) {
////                                        isItemClicked = false
//                                soundArrayList.get(prevIndexSound).setPlaying(false)
//                                rvSoundList.adapter!!.notifyDataSetChanged()
//                            }
//                        }
//                        if (soundListModel.getFreePaid()
//                                .equals("Free", ignoreCase = true)
//                        ) {
//                            loadSound(index, soundListModel)
//                        } else {
//                            MyApplication.prefs!!.isItemClicked = false
//
//                            if (soundListModel.purchased!!) {
//                                loadSound(index, soundListModel)
//                            } else {
//                                purchaseDialog(soundListModel.getSoundId()!!)
//                            }
//                        }
//                        rvSoundList.adapter!!.notifyDataSetChanged()
//
//                    } else {
////                                isItemClicked = false
//                        handler.removeCallbacks(sendUpdatesToUI)
//                        handler.postDelayed(sendUpdatesToUI, 0)
//                        togglePlayPause()
//                    }
//                }
//            }
//        }
//    }
//
//}
