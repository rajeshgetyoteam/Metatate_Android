package com.getyoteam.budamind.fragment

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.facebook.login.LoginManager
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.ChapterActivity
import com.getyoteam.budamind.activity.MainActivity
import com.getyoteam.budamind.activity.PlayActivity
import com.getyoteam.budamind.activity.SignInActivity
import com.getyoteam.budamind.adapter.CourseHomeAdapter
import com.getyoteam.budamind.adapter.HomeTaskAdapter
import com.getyoteam.budamind.adapter.MomentHomeAdapter
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.ManagePermissions
import com.getyoteam.budamind.utils.MediaPlayerService
import com.getyoteam.budamind.utils.Utils
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mindfulness.greece.model.MeditationStateModel
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), CourseHomeAdapter.OnCourseHomeAdapterInteractionListener,
    View.OnClickListener,
    MomentHomeAdapter.OnMomentHomeAdapterInteractionListener,
    SwipeRefreshLayout.OnRefreshListener {
    private var authToken: String? = ""
    private lateinit var profile: Profile
    private lateinit var momentModel: MomentListModel
    private lateinit var courseModel: CourseListModel
    private var totalSession: Int = 0
    private var longestStreak: Int = 0
    lateinit var mGoogleApiClient: GoogleApiClient
    private var currentStreak: Int = 0
    private var totalMin: Float = 0f
    private var totalDailyMin: Float = 0f
    private var totalWeeklyMin: Float = 0f
    private var meditationStateModel: MeditationStateModel? = null
    private var currentCourseId: Int = 0
    private lateinit var soundArrayList: ArrayList<SoundListModel>
    private lateinit var momentArrayList: ArrayList<MomentListModel>
    private lateinit var coursArrayList: ArrayList<CourseListModel>
    private lateinit var goalArrayList: ArrayList<GoalModel>
    private lateinit var courseSortArrayList: ArrayList<CourseListModel>
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var db: AppDatabase
    private lateinit var userId: String
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    private var skuListSubPurchase: List<PurchaseModel>? = null
    private var skuList = mutableListOf<SkuDetails>()
    var billingClient: BillingClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        private const val LOG_TAG = "BillingRepository"
    }

    private object GameSku {
        val GAS = "gas"
        val PREMIUM_CAR = "premium_car"
        val GOLD_MONTHLY = "gold_monthly1"
        val GOLD_YEARLY = "gold_yearly"
        val INAPP_SKUS = listOf(GAS, PREMIUM_CAR)
        val SUBS_SKUS = listOf(GOLD_MONTHLY, GOLD_YEARLY)
        val CONSUMABLE_SKUS = listOf(GAS)
        val GOLD_STATUS_SKUS = SUBS_SKUS // coincidence that there only gold_status is a sub
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient.connect()

        val c1 = ContextCompat.getColor(requireContext(), R.color.app_pink_color)
        swipeToRefresh.setColorSchemeColors(c1)

        authToken = MyApplication.prefs!!.authToken
        userId = MyApplication.prefs!!.userId
        courseSortArrayList = ArrayList()
        coursArrayList = ArrayList()
        momentArrayList = ArrayList()
        soundArrayList = ArrayList()
        meditationStateModel = MeditationStateModel()
        db = AppDatabase.getDatabase(requireContext())
        goalArrayList = db.goalDao().getAll() as ArrayList<GoalModel>
        swipeToRefresh.setOnRefreshListener(this)

        tvSubscribe.setOnClickListener(this)
        cvInternetToast.setOnClickListener(this)
        if (!isMyServiceRunning(MediaPlayerService::class.java))
            MyApplication.prefs!!.prevIndexSound = -1

        rvLessions!!.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun checkDownloadPermmissionFirstTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                managePermissions.checkPermissions()
            }
        }
        MyApplication.prefs!!.askPermissionFirst = false
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
            }
        }
    }

    private fun callRefferealAPI(referalId: String) {

        val call = ApiUtils.getAPIService().referralRewards(userId, referalId)

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {

                    MyApplication.prefs!!.referalId = ""

                    Log.d("myTag", response.body()!!.getStatus())

                }
            }
        })
    }


    override fun onResume() {
        super.onResume()

        currentCourseId = MyApplication.prefs!!.courseId
        MyApplication.prefs!!.subPurchase = false
        MyApplication.prefs!!.subscriptionType = ""

        lblName.text = MyApplication.prefs!!.first_name
        try {
//            val count =
            val count:Double = String.format("%.2f", MyApplication.prefs!!.weeklyMinute).toDouble()
            if (count!!.toInt() > 100) {
                circularProgressBar.progress = (100f)
                tvProgress.text = "100"
            } else {
                circularProgressBar.progress = (count!!.toFloat())
                tvProgress.text = count.toString()
            }
        }catch (e :Exception){
            e.printStackTrace()
        }


//        val meditationStateModelArrayList = db.meditationStateDao().getAll()
//        val m = homeResponse.getProfile()?.getMinuteMeditate()!!.toFloat()
//        if (meditationStateModelArrayList.isNotEmpty()) {
//            val mStateModel: MeditationStateModel = meditationStateModelArrayList[0]
//            try {
//                if (mStateModel != null) {
//                    val totalMin = mStateModel?.getWeeklyMinutes() as Int
//                    if (totalMin > 100) {
//                        circularProgressBar.progress = (100f)
//                        tvProgress.text = "100"
//                    } else {
//                        circularProgressBar.progress = (totalMin.toFloat())
//                        tvProgress.text = totalMin.toString()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        } else {
//            circularProgressBar.progress = (0f)
//            tvProgress.text = "0"
//        }
        getCountDetail()
//                if (totalWeeklyMin > 100){
//                    circularProgressBar.progress = (100f)
//                    tvProgress.text = "100"
//                }else{
//                    circularProgressBar.progress = (totalWeeklyMin.toFloat())
//                    tvProgress.text = totalWeeklyMin.toString()
//                }

        if (!MyApplication.prefs!!.referalId.isNullOrBlank()) {
            callRefferealAPI(MyApplication.prefs!!.referalId)
        }

        val data = MyApplication.prefs!!.taskResponce

        if (data.isNullOrBlank()) {
            getTasks()
        } else {
            setData()
        }

        if (isAdded)

            loadData()
        swipeToRefresh.post(object : Runnable {
            override fun run() {
                if (isAdded) {
                    if (checkInternetConnection()) {
                        if (MyApplication.isHomeAPI) {
                            getSleepDetail()
                        }
                    } else {
                        cvInternetToast.visibility = View.VISIBLE
                    }
                }

            }
        })
    }

    fun isMyServiceRunning(calssObj: Class<MediaPlayerService>): Boolean {
        val manager =
            requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (calssObj.name.equals(service.service.className)) {
                return true
            }
        }
        return false
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.cvInternetToast -> {
                cvInternetToast.visibility = View.GONE
            }
            R.id.tvCountinue -> {
                if (context is MainActivity) {
                    (context as MainActivity).ChangeToTask()
                }
            }
        }
    }

    override fun onMomentHomeAdapterInteractionListener(
        momentListModel: MomentListModel,
        possion: Int
    ) {
        momentModel = momentListModel
        if (momentModel.getFreePaid().equals("free", ignoreCase = true)) {
            loadMomentScreen(possion)
        } else {
            if (momentModel!!.purchased!!) {
                loadMomentScreen(possion)
            } else {
                purchaseDialogMoment(momentModel.getMomentId())
            }
        }
    }

    fun purchaseDialogMoment(momentId: Int?) {
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
            callApiForPurchase(momentId!!)
            dialog.dismiss()
        }

        tvNo.setOnClickListener {

            dialog.dismiss()
        }
        dialog.show()
    }

    fun callApiForPurchase(momentId: Int) {
        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().purchaseMoments(userId, momentId.toString())

        call.enqueue(object : Callback<responcePurchaseModel> {
            override fun onFailure(call: Call<responcePurchaseModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
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
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        getSleepDetail()

                    } else {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false
                        Toast.makeText(
                            requireContext(),
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        ).show()
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


    private fun loadCourseScreen() {
        val intent = Intent(requireActivity(), ChapterActivity::class.java)
        intent.putExtra("courseModel", courseModel);
        intent.putExtra("firstCourse", coursArrayList[0])
        startActivity(intent)
    }

    private fun loadMomentScreen(possion: Int) {
        val gson = Gson()
        val jsonMoment = gson.toJson(momentModel)
        val jsonMeditation = gson.toJson(meditationStateModel)
        MyApplication.prefs!!.momentModel = jsonMoment
        MyApplication.prefs!!.stateModel = jsonMeditation

        val intent = Intent(requireActivity(), PlayActivity::class.java)
        intent.putExtra("mo", "");
//        intent.putExtra("meditationStateModel", meditationStateModel)
//        intent.putExtra("possion", possion)
        startActivity(intent)
    }

    private fun getSleepDetail() {
//        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().getHomeDetail(authToken!!, userId)

        call.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                if (isAdded) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.code() == 200) {
                    if (isAdded) {
                        if (swipeToRefresh != null) {
                            swipeToRefresh.isRefreshing = false
                            MyApplication.isHomeAPI = false

                            val homeResponse = response.body()!!
                            if (homeResponse.getStatus().equals(getString(R.string.str_success))) {

                                MyApplication.prefs!!.myBalance = homeResponse.balance.toString()

                                val bal = MyApplication.prefs!!.myBalance.toBigDecimal()

                                val usdBal = Utils.formatBal(bal!!.toBigInteger())
                                tvBalance.text = usdBal.replace("$", "$" + "CHI")
                                coursArrayList =
                                    homeResponse.getHome()!!
                                        .getCourseList() as ArrayList<CourseListModel>
                                momentArrayList =
                                    homeResponse.getHome()!!
                                        .getMomentList() as ArrayList<MomentListModel>
                                soundArrayList =
                                    homeResponse.getHome()!!
                                        .getSoundList() as ArrayList<SoundListModel>

                                if (coursArrayList.size > 0) {
                                    db.courseDao().deleteAll()
                                }
                                if (momentArrayList.size > 0) {
                                    db.momentDao().deleteAll()
                                }
                                if (coursArrayList.size > 0) {
                                    db.soundDao().deleteAll()
                                }

                                for (i in 0..momentArrayList.size - 1) {
                                    db.momentDao().insertAll(momentArrayList.get(i))
                                }
                                for (i in 0..coursArrayList.size - 1) {
                                    db.courseDao().insertAll(coursArrayList.get(i))
                                }
                                for (i in 0..soundArrayList.size - 1) {
                                    val soundListModel = soundArrayList.get(i)
                                    soundListModel.setPlaying(false)
                                    soundListModel.setFirstTime(false)
                                    db.soundDao().insertAll(soundListModel)
                                }

                                loadData()
                            } else if (homeResponse.getStatus().equals(
                                    getString(R.string.str_invalid_token),
                                    ignoreCase = true
                                )
                            ) {
                                logOutAfterDialog()
                            } else {
                                swipeToRefresh.isRefreshing = false
                                Toast.makeText(
                                    activity,
                                    getString(R.string.str_something_went_wrong),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            swipeToRefresh.isRefreshing = false
                        }
                    }
                }
            }
        })
    }

    private fun getTasks() {

        val userId = MyApplication.prefs!!.userId
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().getTask(userId)

        call.enqueue(object : Callback<TaskResponceModel> {
            override fun onFailure(call: Call<TaskResponceModel>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("onFailure", t.message)
            }

            override fun onResponse(
                call: Call<TaskResponceModel>,
                response: Response<TaskResponceModel>
            ) {
                if (response.code() == 200) {
                    try {
                        if (swipeToRefresh != null)
                            swipeToRefresh.isRefreshing = false;

                        val commonModel = response.body()

                        if (commonModel!!.status.equals(getString(R.string.str_success))) {
                            var data: ArrayList<TaskDataModel>? = null

                            val gson = Gson()
                            val meditationStateModel = commonModel.meditationState

                            val jsonMeditation = gson.toJson(meditationStateModel)

                            MyApplication.prefs!!.stateModel = jsonMeditation

                            val taskData = gson.toJson(commonModel.tasks)
                            MyApplication.prefs!!.taskResponce = taskData
                            setData()

                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }

    fun setData() {
        val gson = Gson()

        val taskdata = MyApplication.prefs!!.taskResponce.toString()
        val type = object : TypeToken<ArrayList<TaskDataModel?>?>() {}.type
        val data = gson.fromJson<java.util.ArrayList<TaskDataModel>>(
            taskdata,
            type
        ) as ArrayList<TaskDataModel>


        if (data!!.size > 0) {
//            if (data.size > 10) {
//                tvHeader.text = data.size.toString()
//            } else {
//                tvHeader.text = "0" + data.size.toString()
//            }

//            tvNoTask.visibility = View.GONE
            rvTasks.adapter =
                HomeTaskAdapter(data, requireContext())
        } else {
//            tvHeader.text = "00"
//            tvNoTask.visibility = View.VISIBLE
        }

    }

    private fun getCountDetail() {
//        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().getHomeDetail(authToken!!, userId)

        call.enqueue(object : Callback<HomeResponse> {
            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {

            }

            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.code() == 200) {
                    if (isAdded) {
                            MyApplication.isHomeAPI = false

                            val homeResponse = response.body()!!
                            if (homeResponse.getStatus().equals(getString(R.string.str_success))) {

                                meditationStateModel!!.setUserId(userId.toInt())
                                meditationStateModel!!.setCurrentStreak(
                                    homeResponse.getProfile()?.getCurrentStreak()
                                )
                                meditationStateModel!!.setLongestStreak(
                                    homeResponse.getProfile()?.getLongestStreak()
                                )
                                meditationStateModel!!.setMinuteMeditated(
                                    homeResponse.getProfile()?.getMinuteMeditate()!!.toFloat()
                                )
                                meditationStateModel!!.setTotalSessions(
                                    homeResponse.getProfile()?.getTotalSessions()
                                )
                                meditationStateModel!!.setDailyMinutes(
                                    homeResponse.getProfile()?.getDailyMinuteMeditate()!!.toFloat()
                                )

                                meditationStateModel!!.setWeeklyMinutes(
                                    homeResponse.getProfile()?.getWeeklyMinuteMeditate()!!.toFloat()
                                )

                                if (homeResponse.getProfile()?.getProfilePic() != null) {
                                    MyApplication.prefs!!.profilePic =
                                        homeResponse.getProfile()?.getProfilePic()!!
                                }
                                if (homeResponse.getProfile()?.getFirstName() != null) {
                                    MyApplication.prefs!!.first_name =
                                        homeResponse.getProfile()?.getFirstName()!!
                                }
                                if (homeResponse.getProfile()?.getLastName() != null) {
                                    MyApplication.prefs!!.last_name =
                                        homeResponse.getProfile()?.getLastName()!!
                                }

                                if (homeResponse.getProfile()?.getEmail() != null) {
                                    MyApplication.prefs!!.email =
                                        homeResponse.getProfile()?.getEmail()!!
                                }
                                val meditationStateModelArrayList = db.meditationStateDao().getAll()
                                var meditationStateModelTemp = MeditationStateModel()
                                if (meditationStateModelArrayList.size > 0)
                                    meditationStateModelTemp = meditationStateModelArrayList.get(0)

                                if (meditationStateModel!!.getMinuteMeditated() != meditationStateModelTemp.getMinuteMeditated()) {
                                    if (meditationStateModelTemp.getMinuteMeditated() != null) {
                                        setMeditationState(
                                            meditationStateModelTemp.getMinuteMeditated()!!,
                                            meditationStateModelTemp.getCurrentStreak()!!,
                                            meditationStateModelTemp.getLongestStreak()!!,
                                            meditationStateModelTemp.getTotalSessions()!!,
                                            meditationStateModelTemp.getDailyMinutes()!!
                                        )
                                    } else {
                                        db.meditationStateDao().insertAll(meditationStateModel!!)
                                    }
                                } else {
                                    db.meditationStateDao().insertAll(meditationStateModel!!)
                                }

                                checkDateOfMeditation()

                                val totalWeeklyMin = homeResponse.getProfile()?.getWeeklyMinuteMeditate()
                                val totalDailyMin = homeResponse.getProfile()?.getWeeklyMinuteMeditate()
                                val totalMeditateMinute = homeResponse.getProfile()?.getMinuteMeditate()
                                MyApplication.prefs!!.weeklyMinute  = totalWeeklyMin!!.toFloat()
                                MyApplication.prefs!!.dailyMinute  = totalDailyMin!!.toFloat()
                                MyApplication.prefs!!.totalMeditateMinute  = totalMeditateMinute!!.toFloat()

                                val count:Double = String.format("%.2f", totalWeeklyMin).toDouble()
                                if (totalWeeklyMin > 100) {
                                    circularProgressBar.progress = (100f)
                                    tvProgress.text = "100"
                                } else {
                                    circularProgressBar.progress = (count.toFloat())
                                    tvProgress.text = count.toString()
                                }
                            }

                    }
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

//    private fun getProfileDetail() {
//        swipeToRefresh.isRefreshing = true
//        val retrofit = Retrofit.Builder()
//            .baseUrl(getString(R.string.base_url))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)
//        val call = mindFulNessAPI.getProfileDetail(authToken!!, userId)
//
//        call.enqueue(object : Callback<Profile> {
//            override fun onFailure(call: Call<Profile>, t: Throwable) {
//                if (swipeToRefresh != null)
//                    swipeToRefresh.isRefreshing = false
//                Toast.makeText(
//                    requireActivity(),
//                    getString(R.string.str_something_went_wrong),
//                    Toast.LENGTH_SHORT
//                )
//                    .show()
//            }
//
//            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
//                if (response.code() == 200) {
//                    if (swipeToRefresh != null) {
//                        swipeToRefresh.isRefreshing = false
//                        profile = response.body()!!
//                        if (profile.getStatus().equals(getString(R.string.str_success))) {
//
//                            meditationStateModel!!.setUserId(userId.toInt())
//                            meditationStateModel!!.setCurrentStreak(profile.getCurrentStreak())
//                            meditationStateModel!!.setLongestStreak(profile.getLongestStreak())
//                            meditationStateModel!!.setMinuteMeditated(profile.getMinuteMeditate())
//                            meditationStateModel!!.setTotalSessions(profile.getTotalSessions())
//
//                            MyApplication.prefs!!.profilePic = profile.getProfilePic()!!
//                            MyApplication.prefs!!.first_name = profile.getFirstName()!!
//                            MyApplication.prefs!!.last_name = profile.getLastName()!!
//                            val meditationStateModelArrayList = db.meditationStateDao().getAll()
//                            var meditationStateModelTemp = MeditationStateModel()
//                            if (meditationStateModelArrayList.size > 0)
//                                meditationStateModelTemp = meditationStateModelArrayList.get(0)
//
//                            if (meditationStateModel!!.getMinuteMeditated() != meditationStateModelTemp.getMinuteMeditated()) {
//                                if (meditationStateModelTemp.getMinuteMeditated() != null) {
//                                    setMeditationState(
//                                        meditationStateModelTemp.getMinuteMeditated()!!,
//                                        meditationStateModelTemp.getCurrentStreak()!!,
//                                        meditationStateModelTemp.getLongestStreak()!!,
//                                        meditationStateModelTemp.getTotalSessions()!!,
//                                        meditationStateModelTemp.getDailyMinutes()!!
//                                    )
//                                } else {
//                                    db.meditationStateDao().insertAll(meditationStateModel!!)
//                                }
//                            } else {
//                                db.meditationStateDao().insertAll(meditationStateModel!!)
//                            }
//                            checkDateOfMeditation()
//
//                            loadData()
//
//                        } else if (profile.getStatus().equals(
//                                getString(R.string.str_invalid_token),
//                                ignoreCase = true
//                            )
//                        ) {
//                            signOut()
//                        } else {
//                            Toast.makeText(
//                                requireActivity(),
//                                getString(R.string.str_something_went_wrong),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                }
//            }
//        })
//    }

    private fun setMeditationState(
        min: Float,
        currentStreak: Int,
        longestStreak: Int,
        totalSession: Int, dailyMin: Float
    ) {
        val userId = MyApplication.prefs!!.userId

        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userMap: java.util.HashMap<String, String> = java.util.HashMap<String, String>()
        userMap.put("userId", userId)
        userMap.put("currentStreak", currentStreak.toString())
        userMap.put("longestStreak", longestStreak.toString())
//        userMap.put("minuteMeditate", min.toString())
//        userMap.put("dailyMinutes", dailyMin.toString())
        userMap.put("totalSessions", totalSession.toString())

        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)
        val call = mindFulNessAPI.updateProfile(userMap)
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

    private fun checkDateOfMeditation() {

        try {
            if (meditationStateModel != null) {
                currentStreak = meditationStateModel?.getCurrentStreak() as Int
                longestStreak = meditationStateModel?.getLongestStreak() as Int
                totalSession = meditationStateModel?.getTotalSessions() as Int
//                totalMin = meditationStateModel?.getMinuteMeditated() as Float
//                totalDailyMin = meditationStateModel?.getDailyMinutes() as Float
//                totalWeeklyMin = meditationStateModel?.getWeeklyMinutes()as Float
//            val m = homeResponse.getProfile()?.getMinuteMeditate()!!.toFloat()

//                if (totalWeeklyMin > 100) {
//
//                    circularProgressBar.progress = (100f)
//                    tvProgress.text = "100"
//                } else {
//                    circularProgressBar.progress = (totalWeeklyMin.toFloat())
//                    tvProgress.text = totalWeeklyMin.toString()
//                }


            }
        } catch (e: TypeCastException) {
            e.printStackTrace()
        }


        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val prevDate = calendar.time
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val todayAsString = dateFormat.format(today)
        val prevDateAsString = dateFormat.format(prevDate)

        val previousDate = MyApplication.prefs!!.prevDate
        if (!previousDate.equals(prevDateAsString)) {
            if (!previousDate.equals(todayAsString, ignoreCase = true)) {
                setMeditationState(0f, 0, longestStreak, totalSession, 0f)
            }
        }
    }

    override fun onCourseHomeAdapterInteractionListener(courseListModel: CourseListModel) {

        courseModel = courseListModel
        loadCourseScreen()

//        if (courseListModel.freePaid.equals("Paid")) {
//            if (courseListModel.purchased!!){
//                courseModel = courseListModel
//                loadCourseScreen()
//            }else{
//                purchaseDialogCourse(courseListModel.getCourseId())
//            }
//
//        } else {
//            courseModel = courseListModel
//            loadCourseScreen()
//        }
    }

    private fun loadData() {
        coursArrayList = ArrayList()
        momentArrayList = ArrayList()
        soundArrayList = ArrayList()
        if (coursArrayList.size > 0) {
            coursArrayList.clear()
        }
        if (courseSortArrayList.size > 0) {
            courseSortArrayList.clear()
        }
        if (momentArrayList.size > 0) {
            momentArrayList.clear()
        }
        if (soundArrayList.size > 0) {
            soundArrayList.clear()
        }

        momentArrayList.addAll(db.momentDao().getAll())
        coursArrayList.addAll(db.courseDao().getAll().reversed())
        soundArrayList.addAll(db.soundDao().getAll())

//        getBudhaPrice()
//        getProfileDetail()
        try {

            var b = MyApplication.prefs!!.myBalance
            if (b.isEmpty()){
                b= "0"
            }

            val p = 0.0000000000869
            val bal = p.toBigDecimal() * b.toBigDecimal()

            val usdBal = DecimalFormat("0.00000").format(bal)
            tvBalance.text = "$" + usdBal + ""

        }catch (e :Exception){
            e.printStackTrace()
        }

        for (i in 0 until coursArrayList.size) {
//            for (j in 0..7) {
            if (coursArrayList[i].getAnxiety() == 1) {
                if (1 == MyApplication.prefs!!.anxiety) {
                    var isAvaulable = false
                    for (j in 0 until courseSortArrayList.size) {
                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }
                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }

//                        break
                }
            }

            if (coursArrayList.get(i).getFocus() == 1) {
                if (1 == MyApplication.prefs!!.focus) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
//                        break
                }
            }
            if (coursArrayList.get(i).getGratitute() == 1) {
                if (1 == MyApplication.prefs!!.gratitute) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
//                        break
                }
            }
            if (coursArrayList.get(i).getHappiness() == 1) {
                if (1 == MyApplication.prefs!!.happiness) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
//                        break
                }
            }
            if (coursArrayList.get(i).getMeditate() == 1) {
                if (1 == MyApplication.prefs!!.meditate) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
//                        break
                }
            }
            if (coursArrayList.get(i).getSelfEsteem() == 1) {
                if (1 == MyApplication.prefs!!.selfEsteem) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
                }
            }
            if (coursArrayList.get(i).getSleep() == 1) {
                if (1 == MyApplication.prefs!!.sleep) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
                }
            }
            if (coursArrayList.get(i).getStress() == 1) {
                if (1 == MyApplication.getPref().stress) {
                    var isAvaulable = false
                    for (j in 0..courseSortArrayList.size - 1) {

                        if (courseSortArrayList.get(j).getCourseId() == coursArrayList.get(i)
                                .getCourseId()
                        ) {
                            isAvaulable = true
                        }

                    }
                    if (!isAvaulable) {
                        courseSortArrayList.add(coursArrayList.get(i))
                    }
                }
            }
//            }
        }


        if (courseSortArrayList.size < 1) {
            courseSortArrayList = coursArrayList
        }

        //        if (courseSortArrayList.size > 0)
//            MyApplication.prefs!!.firstMeditationId = coursArrayList.get(0).getCourseId()!!

        rvLessions.adapter =
            CourseHomeAdapter(courseSortArrayList, this@HomeFragment, requireContext())
//        rvMoment.adapter =
//            MomentHomeAdapter(momentArrayList, this@HomeFragment, requireContext())

//        if (coursArrayList.size > 0) {
//            if (currentCourseId == 0) {
//                loadHomeCourse(coursArrayList.get(0))
//            } else {
//                for (i in 0 until coursArrayList.size) {
//                    val courseModel = coursArrayList.get(i)
//                    if (currentCourseId == courseModel.getCourseId()) {
//                        loadHomeCourse(courseModel)
//                        break
//                    }
//                }
//            }
//        }

    }

    private fun getBudhaPrice() {
//        swipeToRefresh.isRefreshing = true

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nomics.com/v1/currencies/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mindFulNessAPI = retrofit.create(ClarityAPI::class.java)
        val call = mindFulNessAPI.getTicker(
            "f74b0f300342169ca51b24e4b0b5f23bec262dc4",
            "CHI",
            "1d",
            "USD",
            "100",
            "1"
        )

        call.enqueue(object : Callback<ArrayList<responceBudaPriceModel>> {
            override fun onFailure(call: Call<ArrayList<responceBudaPriceModel>>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(
                call: Call<ArrayList<responceBudaPriceModel>>,
                response: Response<ArrayList<responceBudaPriceModel>>
            ) {
                if (response.code() == 200) {
                    if (swipeToRefresh != null) {
                        swipeToRefresh.isRefreshing = false

                        var data = response


                        val todayPrice = response.body()!!.get(0).price
                        Log.d("okhttp", "Coin Price :" + todayPrice)

                        val bal =
                            todayPrice!!.toBigDecimal() * MyApplication.prefs!!.myBalance.toBigDecimal()

                        val usdBal = DecimalFormat("0.00000").format(bal)

                        try {
                            val bal = MyApplication.prefs!!.myBalance.toBigDecimal()
                            val p = Utils.formatBal(bal!!.toBigInteger())
                            tvBalance.text = p.replace("$", "$" + "CHI")

                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    if (swipeToRefresh != null) {
                        swipeToRefresh.isRefreshing = false
                    }
                    try {
//                        tvBalance.text = "$"+"CHI 00.00"

                        val bal = MyApplication.prefs!!.myBalance.toBigDecimal()
                        val p = Utils.formatBal(bal!!.toBigInteger())

                        tvBalance.text = p.replace("$", "$" + "CHI")

                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    }

                }
            }
        })
    }

    public fun loadFirstCourse() {
        courseModel = courseSortArrayList.get(0)
        loadCourseScreen()
    }

    private fun loadHomeCourse(course: CourseListModel) {
        courseModel = course
        val totalSong = db.chapterDao().loadAllByIds(course.getCourseId()!!)
        val totalPlayedSong = db.chapterPlayedDao().loadAllByIds(course.getCourseId()!!)
        if (totalSong.size > 0 && totalPlayedSong.size > 0) {
//            circleProgressCourseComplation.visibility = View.VISIBLE
//            val perItem = 100 / totalSong.size
//            val progress = totalPlayedSong.size * perItem
//            circleProgressCourseComplation.setProgress(progress)

//            wheelprogress.setPercentage(50)
        }
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onRefresh() {
        if (isAdded) {
            if (checkInternetConnection()) {
                getSleepDetail()
                getTasks()
//                getProfileDetail()
//                getBudhaPrice()
            } else {
                swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireContext(),
                    "Please Chek Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

}
