package com.getyoteam.budamind.fragment

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.*
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.downloader.*
import com.facebook.login.LoginManager
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.PlaySoundActivity
import com.getyoteam.budamind.activity.SignInActivity
import com.getyoteam.budamind.adapter.SoundAdapterNew
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.playback.MusicService
import com.getyoteam.budamind.testaudioexohls.AudioService
import com.getyoteam.budamind.utils.*
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.gson.Gson
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.fragment_sound.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class SoundFragmentN() : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener,
    SoundAdapterNew.OnSoundAdapterInteractionListener {
    var soundAdapter: SoundAdapterNew? = null

    private var isgetRewordDone: Boolean = false
    private var isFirstTime: Boolean? = false
    private var mIsBound: Boolean? = false
    private var isPlaying: Boolean = false
    private var downloadFileModelOld: DownloadFileModel? = null
    private var downloadFileModel: DownloadFileModel? = null
    private var filename: String? = ""
    private var audioPath: String? = ""
    private var downloadId: Int = 0
    private var prevIndexSound: Int = -1

    private var seekPer: Int = 0
    val BROADCAST_ACTION = "com.clarity.meditation.utils.displayevent"
    val Broadcast_PLAY_NEW_AUDIO = "com.valdioveliu.valdio.audioplayer.PlayNewAudio"
    private lateinit var managePermissions: ManagePermissions
    lateinit var mGoogleApiClient: GoogleApiClient
    var intent: Intent? = null
    val handler = Handler()
    private lateinit var mediaPlayer: MediaPlayer
    private var player: MediaPlayerService? = null
    internal var serviceBound = false
    var isItemClicked = false
    private var meditationStateModel: MeditationStateModel? = null
    private val PermissionsRequestCode = 123
    private var userId: String = ""
    private lateinit var db: AppDatabase
    private lateinit var soundArrayList: ArrayList<SoundListModel>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound, container, false)
    }

    fun SoundFragmentN() {
        // doesn't do anything special
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        tvHeader.text = getText(R.string.str_sound)
        userId = MyApplication.prefs!!.userId
        MyApplication.prefs!!.isItemClicked = false
        intent = Intent(BROADCAST_ACTION)
        db = AppDatabase.getDatabase(requireContext())
        val list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)
        meditationStateModel = MeditationStateModel()

        val gson = Gson()
        val jsonMeditation = MyApplication.prefs!!.stateModel
        meditationStateModel = gson.fromJson(jsonMeditation, MeditationStateModel::class.java)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient.connect()


        val c1 = ContextCompat.getColor(requireContext(), R.color.app_pink_color)
        swipeToRefresh.setColorSchemeColors(c1)

        mediaPlayer = MediaPlayer()
        soundArrayList = ArrayList<SoundListModel>()
        rvSoundList!!.layoutManager = LinearLayoutManager(requireContext())

        if (checkInternetConnection()) {
            cvInternetToast.visibility = View.GONE
        } else {
            cvInternetToast.visibility = View.VISIBLE
        }

        swipeToRefresh.setOnRefreshListener(this)
//        mediaPlayer.setOnBufferingUpdateListener(this)
    }


    fun purchaseDialog(soundId: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_purchase_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                android.R.color.transparent
            )
        )
        val close: ImageView
        val tvYes: TextView
        val tvNo: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvYes = dialog.findViewById(R.id.tvyes)
        tvNo = dialog.findViewById(R.id.tvNo)
        close.setOnClickListener {
            dialog.dismiss()
        }

        tvYes.setOnClickListener {
            callApiForPurchase(soundId)
            dialog.dismiss()
        }

        tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }


    fun callApiForPurchase(soundId: Int) {
        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().purchaseSounds(userId, soundId.toString())

        call.enqueue(object : Callback<responcePurchaseModel> {
            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<responcePurchaseModel>,
                response: Response<responcePurchaseModel>
            ) {
                if (response.code() == 200) {

                    val commonModel = response.body()
                    if (commonModel!!.status.equals(getString(R.string.str_success))) {
                        Toast.makeText(
                            requireContext(),
                            commonModel!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        getSoundDetail()

                    } else {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun signOut() {
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun logOutAfterDialog() {
        MyApplication.prefs?.userId = ""
        MyApplication.isSoundAPI = true
        MyApplication.isHomeAPI = true
        MyApplication.isLibraryAPI = true
        MyApplication.isProfileAPI = true
        MyApplication.prefs!!.profilePic = ""
        MyApplication.prefs!!.first_name = ""
        MyApplication.prefs!!.last_name = ""

        db.meditationStateDao().deleteAll()
        db.chapterPlayedDao().deleteAllData()
        db.goalDao().deleteAll()

        val loginThrough = MyApplication.prefs!!.loginType.toString()

        if (loginThrough.equals("Facebook", ignoreCase = true)) {
            LoginManager.getInstance().logOut()
            signOut()
        } else if (loginThrough.equals("gmail", ignoreCase = true)) {
            googleSignOut()
        } else {
            signOut()
        }
        requireActivity().finish()
    }

    private fun googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            object : ResultCallback<Status> {
                override fun onResult(p0: Status) {
                    signOut()
                }
            })
    }


    private fun downloadAudio() {
//        TODO("/data/user/0/com.clarity.meditation/76a86802-8f51-449d-94b2-f8d2f182cbf5.mp3")
        val dirPath = requireActivity().applicationInfo.dataDir
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
                }
            })
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                }

                override fun onDownloadComplete() {

                    db.downloadDao().insertDownloadFile(downloadFileModel!!)
                    filename = audioPath!!.substring(audioPath!!.lastIndexOf('/') + 1)
                    if (filename != null)
                        downloadFileModelOld = db.downloadDao().loadDownloadFile(filename!!)
                }

            })
    }


    fun wanCoinDialog(coin: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_congratulation_view)
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        window.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                android.R.color.transparent
            )
        )
        val close: ImageView
        val tvCoin: TextView
        val tvWallate: TextView
        close = dialog.findViewById(R.id.ivClose)
        tvCoin = dialog.findViewById(R.id.tvCoin)
        tvWallate = dialog.findViewById(R.id.tvWallate)  //You have earned 1B $CHI.
        tvCoin.text = "You have won " + coin.replace("$", "$" + "CHI")
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

    override fun onRefresh() {
        if (isAdded) {
            if (checkInternetConnection()) {
                cvInternetToast.visibility = View.GONE
                getSoundDetail()
            } else {
                cvInternetToast.visibility = View.VISIBLE
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (isAdded) {
            prevIndexSound = MyApplication.prefs!!.prevIndexSound
            isPlaying = MyApplication.prefs!!.isPlaying!!
            if (prevIndexSound > -1)
                serviceBound = true

            loadDataFromDB()

        }
    }

    override fun onStop() {
        super.onStop()
    }

    private fun getSoundDetail() {
        swipeToRefresh.isRefreshing = true
        val userId = MyApplication.prefs!!.userId
        val call = ApiUtils.getAPIService().getSoundList(userId)

        call.enqueue(object : Callback<SoundResponse> {
            override fun onFailure(call: Call<SoundResponse>, t: Throwable) {
                if (isAdded) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        activity,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }

            override fun onResponse(call: Call<SoundResponse>, response: Response<SoundResponse>) {
                if (response.code() == 200) {
                    if (isAdded) {
                        if (swipeToRefresh != null) {
                            swipeToRefresh.isRefreshing = false
                            MyApplication.isSoundAPI = false
                            val soundResponse = response.body()!!
                            if (soundResponse.status.equals(getString(R.string.str_success))) {
                                val data: List<SoundListModel> = soundResponse.soundList!!
                                soundArrayList.clear()
                                soundArrayList.addAll(data)
                                if (soundArrayList.size > 0) {
                                    db.soundDao().deleteAll()
                                }

                                db.soundDao().insertAll(soundArrayList)
//                                for (i in 0 until soundArrayList.size) {
//                                    val soundListModel = soundArrayList.get(i)
//                                    soundListModel.setPlaying(false)
//                                    soundListModel.setFirstTime(false)
//                                    db.soundDao().insertAll(soundListModel)
//                                }

                                loadDataFromDB()


                            } else if (soundResponse.status.equals(
                                    getString(R.string.str_invalid_token),
                                    ignoreCase = true
                                )
                            ) {
                                logOutAfterDialog()
                            } else {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.str_something_went_wrong),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        activity,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


    private fun loadDataFromDB() {
        try {
            if (soundArrayList.size > 0) {
                soundArrayList.clear()
            }
            val s = db.soundDao().getAll()
            if (s.size > 0){
                soundArrayList.addAll(s.reversed() as ArrayList<SoundListModel>)
                if (prevIndexSound != -1 && soundArrayList.size > 0)
                    if (isPlaying)
                        soundArrayList.get(prevIndexSound).setPlaying(true)

                soundAdapter = SoundAdapterNew(soundArrayList, requireContext(), this)
                rvSoundList.adapter = soundAdapter
            }
        }catch (e : Exception){
            e.printStackTrace()
        }



    }



    override fun onDestroy() {
        super.onDestroy()

    }


    fun isMomentServiceRunning(serviceName: String): Boolean {
        val manager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.className)) {
                return true
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()

    }


    //Binding this Client to the AudioPlayer Service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaPlayerService.LocalBinder
            player = binder.service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("serviceStatus", serviceBound)
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onSoundAdapterInteractionListener(
        soundModel: SoundListModel,
        index: Int,
        s: String
    ) {

        if (isMomentServiceRunning(MusicService::class.java.name)) {
            val intent = Intent(requireActivity(), MusicService::class.java)
            requireActivity().stopService(intent)
        }
        if (isMomentServiceRunning(AudioService::class.java.name)) {
            val intent = Intent(requireActivity(), AudioService::class.java)
            requireActivity().stopService(intent)
        }

        val momentModel = MomentListModel()

        momentModel.setMomentId(soundModel.getSoundId())
        momentModel.setTitle(soundModel.getTitle().toString())
        momentModel.setSubtitle(soundModel.getSubtitle().toString())
        momentModel.setImage(soundModel.image.toString())
        momentModel.setMomentId(soundModel.getSoundId())
        momentModel.setAudio(soundModel.getAudio().toString())
        momentModel.setFreePaid(soundModel.getFreePaid().toString())
        momentModel.purchased = soundModel.purchased
        momentModel.coins = soundModel.coins
        momentModel.coinForContent = soundModel.coinForContent
        momentModel.setMinutes(soundModel.getMinutes().toString())
        momentModel.setSeconds(soundModel.getSeconds().toString())
        momentModel.setSeconds(soundModel.getSeconds().toString())


        val gson = Gson()
        val jsonMoment = gson.toJson(momentModel)
        val jsonMeditation = gson.toJson(meditationStateModel)
        MyApplication.prefs!!.momentModel = jsonMoment
        MyApplication.prefs!!.stateModel = jsonMeditation



        if (soundModel.getFreePaid()
                .equals("Free", ignoreCase = true)
        ) {

            val intent = Intent(requireActivity(), PlaySoundActivity::class.java)
            intent.putExtra("m", "")
            startActivity(intent)
        } else {
            MyApplication.prefs!!.isItemClicked = false

            if (soundModel.purchased!!) {

                val intent = Intent(requireActivity(), PlaySoundActivity::class.java)
                intent.putExtra("m", "")
                startActivity(intent)
            } else {
                purchaseDialog(soundModel.getSoundId()!!)
            }
        }
    }

    override fun onClick(v: View?) {


    }

}
