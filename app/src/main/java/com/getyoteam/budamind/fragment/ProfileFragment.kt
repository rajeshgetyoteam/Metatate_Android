package com.getyoteam.budamind.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.*
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import com.getyoteam.budamind.utils.AppDatabase
import com.getyoteam.budamind.utils.ManagePermissions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.mindfulness.greece.model.MeditationStateModel
import io.branch.referral.Branch
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.cvInternetToast
import kotlinx.android.synthetic.main.fragment_profile.swipeToRefresh
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ProfileFragment : Fragment(), View.OnClickListener {

    private var alertDialog: AlertDialog? = null
    private var alertDeleteDialog: AlertDialog? = null
    private lateinit var last_name: String
    private lateinit var first_name: String
//    private var totalMin: Int = 0
    private var meditationStateModel: MeditationStateModel? = null
    private lateinit var list: List<String>
    private var profile: Profile? = null
    private lateinit var userId: String
    private var listener: OnFragmentInteractionListener? = null
    lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
    var profileImageModel: ProfileImage? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private lateinit var db: AppDatabase
    private var totalSession: Int = 0
    private var longestStreak: Int = 0
    private var currentStreak: Int = 0
    private var authToken: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginThrough = MyApplication.prefs!!.loginType.toString()
        if (loginThrough.equals("Facebook", ignoreCase = true) || loginThrough.equals(
                "gmail",
                ignoreCase = true
            )
        ) {
            tvChangePassword.visibility = View.GONE
            tvChangePasswordView.visibility = View.GONE
        }

        authToken = MyApplication.prefs!!.authToken
        userId = MyApplication.prefs!!.userId
        list = listOf<String>(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )

        val c1 = ContextCompat.getColor(requireContext(), R.color.color_blue)
        swipeToRefresh.setColorSchemeColors(c1)
        swipeToRefresh.setEnabled(false);


        db = AppDatabase.getDatabase(requireContext())
        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()
        mGoogleApiClient.connect()

        ivEditProfile.setOnClickListener(this)
        tvShareYourStatus.setOnClickListener(this)
        ivEditCamera.setOnClickListener(this)
        ivSaveProfile.setOnClickListener(this)
        tvLogOut.setOnClickListener(this)
        tvDeleteAccount.setOnClickListener(this)
        tvDownloads.setOnClickListener(this)
        tvUnlockClarityPremium.setOnClickListener(this)
        tvMindfulnessReminder.setOnClickListener(this)
        tvUnlockClarityPremium.setOnClickListener(this)
        tvPushNotification.setOnClickListener(this)
        tvChangePassword.setOnClickListener(this)
        tvTermsOfUse.setOnClickListener(this)
        tvGoal.setOnClickListener(this)
        tvWallet.setOnClickListener(this)
        cvInternetToast.setOnClickListener(this)

        val profilePic = MyApplication.prefs!!.profilePic
        first_name = MyApplication.prefs!!.first_name
        last_name = MyApplication.prefs!!.last_name

        if (first_name.isNullOrBlank() || last_name.isNullOrBlank()){
            tvName.text = "First name" + " " + "Last name"
        }
        tvName.text = first_name + " " + last_name
        tvEmail.text = MyApplication.prefs!!.email
        if (!TextUtils.isEmpty(profilePic)) {
            Glide.with(requireActivity())
                .load(profilePic)
                .placeholder(R.drawable.user_image)
                .into(ivUserPic)
        }
//        if (checkInternetConnection()) {
//            if (MyApplication.isProfileAPI)
//                getProfileDetail()
//        } else {
//            cvInternetToast.visibility = View.VISIBLE
//        }
//
//        swipeToRefresh.setOnRefreshListener {
//            if (isAdded()) {
//                if (checkInternetConnection())
//                    getProfileDetail()
//                else
//                    cvInternetToast.visibility = View.VISIBLE
//            }
//        }

        showDialog()
    }

    private fun deleteProfile() {
        swipeToRefresh.isRefreshing = true

        val call = ApiUtils.getAPIService().deleteUser(authToken!!,userId)

        call.enqueue(object : Callback<com.getyoteam.budamind.Model.Status> {
            override fun onFailure(call: Call<com.getyoteam.budamind.Model.Status>, t: Throwable) {
                if (isAdded) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false

                    Toast.makeText(activity, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(call: Call<com.getyoteam.budamind.Model.Status>, response: Response<com.getyoteam.budamind.Model.Status>) {
                if (response.code() == 200) {
                    if (isAdded) {
                        if (swipeToRefresh != null) {
                            swipeToRefresh.isRefreshing = false

                            val libraryModel = response.body()!!
                            if (libraryModel.status.equals(getString(R.string.str_success))) {
                                Branch.getInstance(getApplicationContext()).logout()
                                MyApplication.prefs!!.isFirstApp = true
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
                }else{
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false
                        Toast.makeText(requireContext(), getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun getProfileDetail() {
        swipeToRefresh.isRefreshing = true
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mindFulNessAPI = retrofit.create(API::class.java)
        val call = mindFulNessAPI.getProfileDetail(authToken!!, userId)

        call.enqueue(object : Callback<Profile> {
            override fun onFailure(call: Call<Profile>, t: Throwable) {
                if (swipeToRefresh != null)
                    swipeToRefresh.isRefreshing = false
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.code() == 200) {
                    if (swipeToRefresh != null) {
                        MyApplication.isProfileAPI = false
                        swipeToRefresh.isRefreshing = false
                        profile = response.body()!!
                        if (profile!!.getStatus().equals(getString(R.string.str_success))) {
                            val strName = profile!!.getFirstName() + " " + profile!!.getLastName()
                            tvName.text = strName

                            if (profile!!.getProfilePic() != null) {
                                Glide.with(requireActivity())
                                    .load(profile!!.getProfilePic())
                                    .placeholder(R.drawable.user_image)
                                    .into(ivUserPic)
                            }
                            meditationStateModel = MeditationStateModel()
                            meditationStateModel!!.setUserId(userId.toInt())
                            meditationStateModel!!.setCurrentStreak(profile!!.getCurrentStreak())
                            meditationStateModel!!.setLongestStreak(profile!!.getLongestStreak())
                            meditationStateModel!!.setMinuteMeditated(profile!!.getMinuteMeditate()!!.toFloat())
                            meditationStateModel!!.setTotalSessions(profile!!.getTotalSessions())


                            val meditationStateModelArrayList = db.meditationStateDao().getAll()
                            var meditationStateModelTemp = MeditationStateModel()
                            if (meditationStateModelArrayList.size > 0)
                                meditationStateModelTemp = meditationStateModelArrayList.get(0)

                            if (meditationStateModel!!.getMinuteMeditated() != meditationStateModelTemp.getMinuteMeditated()) {
                                if (meditationStateModelTemp.getMinuteMeditated() != null) {
                                    setMeditationState(
                                        meditationStateModelTemp.getMinuteMeditated()!!.toInt(),
                                        meditationStateModelTemp.getCurrentStreak()!!,
                                        meditationStateModelTemp.getLongestStreak()!!,
                                        meditationStateModelTemp.getTotalSessions()!!
                                    )
                                } else {
                                    db.meditationStateDao().insertAll(meditationStateModel!!)
                                }
                            } else {
                                db.meditationStateDao().insertAll(meditationStateModel!!)
                            }
                            checkDateOfMeditation()

                            loadData()

                        } else if (profile!!.getStatus().equals(
                                getString(R.string.str_invalid_token),
                                ignoreCase = true
                            )
                        ) {
                            logOutAfterDialog()
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.str_something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

//    private fun signOut() {
//        MyApplication.prefs?.userId = ""
//        MyApplication.isSoundAPI = true
//        MyApplication.isHomeAPI = true
//        MyApplication.isLibraryAPI = true
//        MyApplication.isProfileAPI = true
//        MyApplication.prefs!!.profilePic = ""
//        MyApplication.prefs!!.first_name = ""
//        MyApplication.prefs!!.last_name = ""
//        db.chapterPlayedDao().deleteAllData()
//        db.meditationStateDao().deleteAll()
//        db.goalDao().deleteAll()
//        val intent = Intent(requireActivity(), LoginWithSocialActivity::class.java)
//        startActivity(intent)
//        requireActivity().finish()
//    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        val meditationStateModelArrayList = db.meditationStateDao().getAll()
        if (meditationStateModelArrayList.size > 0)
            meditationStateModel = meditationStateModelArrayList.get(0)

        if (meditationStateModel != null) {

            if (meditationStateModel?.getLongestStreak().toString().equals("null")) {
                tvLongestStreak.text = "0"
            } else {
                val longStreak = meditationStateModel?.getLongestStreak()
                tvLongestStreak.text = longStreak.toString()
            }

//            if (meditationStateModel?.getMinuteMeditated().toString().equals("null")) {
//                tvMindfulnessMin.text = "0"
//            } else {
//                tvMindfulnessMin.text = meditationStateModel?.getMinuteMeditated().toString()
//            }

            if (meditationStateModel?.getTotalSessions().toString().equals("null")) {
                tvTotalSession.text = "0"
            } else {
                tvTotalSession.text = meditationStateModel?.getTotalSessions().toString()
            }


            try {
//            val count =
                val count:Double = String.format("%.2f", MyApplication.prefs!!.totalMeditateMinute).toDouble()

                if (count < 0) {
                    tvMindfulnessMin.text = "0"
                } else {
                    tvMindfulnessMin.text =count.toString()
                }

            }catch (e :Exception){
                e.printStackTrace()
            }
        }
    }

    private fun setMeditationState(
        min: Int,
        currentStreak: Int,
        longestStreak: Int,
        totalSession: Int
    ) {
        val userId = MyApplication.prefs!!.userId

        val userMap: java.util.HashMap<String, String> = java.util.HashMap<String, String>()
        userMap.put("userId", userId)
        userMap.put("currentStreak", currentStreak.toString())
        userMap.put("longestStreak", longestStreak.toString())
//        userMap.put("minuteMeditate", min.toString())
        userMap.put("totalSessions", totalSession.toString())

        val call = ApiUtils.getAPIService().updateProfile(userMap)
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
        if (meditationStateModel != null) {
            currentStreak = meditationStateModel?.getCurrentStreak() as Int
            longestStreak = meditationStateModel?.getLongestStreak() as Int
            totalSession = meditationStateModel?.getTotalSessions() as Int
//            totalMin = meditationStateModel?.getMinuteMeditated() as Int
        }

        val calendar = Calendar.getInstance()
        val today = calendar.getTime()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val prevDate = calendar.getTime()
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy")
        val todayAsString = dateFormat.format(today)
        val prevDateAsString = dateFormat.format(prevDate)

        val previousDate = MyApplication.prefs!!.prevDate
        if (!previousDate.equals(prevDateAsString)) {
            if (!previousDate.equals(todayAsString, ignoreCase = true)) {
                setMeditationState(0, 0, longestStreak, totalSession)
            }
        }
    }

    private fun updateUserDetail(
    ) {
        swipeToRefresh.isRefreshing = true
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val mindFulNessAPI = retrofit.create(API::class.java)
        val userMap: HashMap<String, String> = HashMap<String, String>()
        userMap.put("userId", userId)
        userMap.put("firstName", etFirstName.text.toString())
        userMap.put("lastName", etLastName.text.toString())
        if (profileImageModel != null)
            userMap.put("profilePic", profileImageModel!!.getFileUrl().toString())

        val call = mindFulNessAPI.updateProfile(userMap)

        call.enqueue(object : Callback<CommanResponseModel> {
            override fun onFailure(call: Call<CommanResponseModel>, t: Throwable) {
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
                call: Call<CommanResponseModel>,
                response: Response<CommanResponseModel>
            ) {
                if (response.code() == 200) {
                    if (swipeToRefresh != null)
                        swipeToRefresh.isRefreshing = false
                    val commanResponseModel = response.body()!!
                    if (commanResponseModel.getStatus().equals(getString(R.string.str_success))) {
                        val strName = etFirstName.text.toString() + " " + etLastName.text.toString()
                        tvName.text = strName
                        if (profileImageModel != null)
                            Glide.with(requireActivity())
                                .load(profileImageModel!!.getFileUrl())
                                .placeholder(R.drawable.progress)
                                .into(ivUserPic)

                        getProfileDetail()
                        hideKeyBoard()
                        tvName.visibility = View.VISIBLE
                        ivEditCamera.visibility = View.GONE
                        etFirstName.visibility = View.GONE
                        etLastName.visibility = View.GONE
                        ivSaveProfile.visibility = View.GONE
                        ivEditProfile.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun hideKeyBoard() {
        val imm =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = requireActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvShareYourStatus -> {
                ivEditProfile.visibility = View.INVISIBLE
                val bitmap = screenShot(rvStatus)
                val imgUri = saveImageShareStatus(bitmap)
                shareImageUri(imgUri!!)
            }
            R.id.ivEditCamera -> {
                openCameraWithPermission()
            }
            R.id.cvInternetToast -> {
                cvInternetToast.visibility = View.GONE
            }
            R.id.tvGoal -> {
                val intent = Intent(requireActivity(), ChooseYourGoalActivity::class.java)
                intent.putExtra("isFirstTime", false)
                intent.putExtra("wallateStatus", "")
                intent.putExtra("credited", "")
                startActivity(intent)
            }
            R.id.tvTermsOfUse -> {
                val intent = Intent(requireActivity(), TermsOfUseActivity::class.java)
                startActivity(intent)
            }
            R.id.ivEditProfile -> {
                tvName.visibility = View.GONE
                ivEditCamera.visibility = View.VISIBLE
                etFirstName.visibility = View.VISIBLE
                etLastName.visibility = View.VISIBLE
                ivSaveProfile.visibility = View.VISIBLE
                ivEditProfile.visibility = View.GONE

                if (profile != null) {
                    etFirstName.setText(profile!!.getFirstName())
                    etLastName.setText(profile!!.getLastName())
                } else {
                    etFirstName.setText(first_name)
                    etLastName.setText(last_name)
                }
            }
            R.id.ivSaveProfile -> {
                val lastName = etLastName.text.toString()
                val firstName = etFirstName.text.toString()
                if (TextUtils.isEmpty(firstName)) {
                    etFirstName.setError(getString(R.string.str_please_enter_first_name))
                } else if (TextUtils.isEmpty(lastName)) {
                    etLastName.setError(getString(R.string.str_please_enter_last_name))
                } else {
                    updateUserDetail()
                }
            }
            R.id.tvUnlockClarityPremium -> {
                if (MyApplication.prefs!!.subPurchase) {
                    val intent = Intent(requireContext(), SubScribedActivity::class.java)
                    startActivity(intent)
                } else {
//                    val intent = Intent(requireContext(), SubscribeActivity::class.java)
//                    intent.putExtra("isFirstTime", false)
//                    startActivity(intent)
                }
            }
            R.id.tvDownloads -> {
                val intent = Intent(requireContext(), DownloadActivity::class.java)
                startActivity(intent)
            }
            R.id.tvLogOut -> {
                alertDialog!!.show()

            }
            R.id.tvDeleteAccount -> {
                val builder = AlertDialog.Builder(requireContext())

                // Set a title for alert dialog
                builder.setTitle("Delete Account")

                // Set a message for alert dialog
                builder.setMessage("Are you sure, you would like to delete account?")


                // On click listener for dialog buttons
                val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {

                            deleteProfile()

                        }
                        DialogInterface.BUTTON_NEGATIVE -> alertDialog!!.dismiss()
                    }
                }


                // Set the alert dialog positive/yes button
                builder.setPositiveButton("Yes", dialogClickListener)

                // Set the alert dialog negative/no button
                builder.setNegativeButton("NO", dialogClickListener)

                // Set the alert dialog neutral/cancel button

                // Initialize the AlertDialog using builder object
                alertDeleteDialog = builder.create()
                alertDeleteDialog!!.show()

            }

            R.id.tvChangePassword -> {
                MyApplication.prefs!!.forgotPassword = false
                val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
                startActivity(intent)
            }
            R.id.tvMindfulnessReminder -> {
                val intent = Intent(requireActivity(), ReminderActivity::class.java)
                startActivity(intent)
            }
            R.id.tvPushNotification -> {
                val intent = Intent(requireActivity(), NotificationActivity::class.java)
                startActivity(intent)
            }
            R.id.tvWallet -> {
                val intent = Intent(requireActivity(), WalletActivity::class.java)
                startActivity(intent)
            }


        }
    }

    private fun showDialog() {
        // Late initialize an alert dialog object

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(requireContext())

        // Set a title for alert dialog
        builder.setTitle("Logout?")

        // Set a message for alert dialog
        builder.setMessage("Are you sure, you would like to Log Out?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    Branch.getInstance(getApplicationContext()).logout();
                    MyApplication.prefs!!.isFirstApp = true
                    logOutAfterDialog()
                }
                DialogInterface.BUTTON_NEGATIVE -> alertDialog!!.dismiss()
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Yes", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("NO", dialogClickListener)

        // Set the alert dialog neutral/cancel button

        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

    }

    private fun logOutAfterDialog() {
        MyApplication.prefs?.userId = ""
        MyApplication.isSoundAPI = true
        MyApplication.isHomeAPI = true
        MyApplication.isLibraryAPI = true
        MyApplication.isProfileAPI = true
        MyApplication.prefs!!.isFirstApp = true
        MyApplication.prefs!!.profilePic = ""
        MyApplication.prefs!!.first_name = ""
        MyApplication.prefs!!.last_name = ""
        MyApplication.prefs!!.taskResponce = ""
        MyApplication.prefs!!.wallateResponce = ""
        MyApplication.prefs!!.weeklyMinute = 0f
        MyApplication.prefs!!.dailyMinute = 0f
        MyApplication.prefs!!.totalMeditateMinute = 0f
        MyApplication.prefs!!.todayEarn = 0f
        MyApplication.prefs!!.dailyEarnLimit = 0f

        db.meditationStateDao().deleteAll()
        db.chapterPlayedDao().deleteAllData()
        db.goalDao().deleteAll()
        db.courseDao().deleteAll()
        db.momentDao().deleteAll()
        db.soundDao().deleteAll()

        val loginThrough = MyApplication.prefs!!.loginType.toString()

        if (loginThrough.equals("FACEBOOK", ignoreCase = true)) {
            LoginManager.getInstance().logOut()
            socialSignOut()
        } else if (loginThrough.equals("GOOGLE", ignoreCase = true)) {
            googleSignOut()
        } else {
            socialSignOut()
        }
        requireActivity().finish()
    }

    fun screenShot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.getWidth(),
            view.getHeight(), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveImageShareStatus(image: Bitmap): Uri? {
        //TODO - Should be processed in another thread
        val imagesFolder = File(requireContext().cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(
                imagesFolder,
                getString(R.string.app_name) + "_" + System.currentTimeMillis() + ".png"
            )

            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(requireContext(), "com.getyoteam.budamind.fileprovider", file)

        } catch (e: IOException) {
            Log.d(
                ShareQuoteActivity::class.java.simpleName,
                "IOException while trying to write file for sharing: " + e.message
            )
        }
        return uri
    }

    private fun shareImageUri(uri: Uri) {
        ivEditProfile.visibility = View.VISIBLE
        val intent = Intent(android.content.Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_msg));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "*/*"
        startActivity(intent)
    }


    private fun openCameraWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    list.toTypedArray(),
                    PermissionsRequestCode
                )
            } else {
                showPictureDialog()
            }

        } else {
            showPictureDialog()
        }
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
                    showPictureDialog()
                } else {

                }
                return
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(requireActivity())
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            activity!!.contentResolver,
                            contentURI
                        )
                        saveImage(bitmap)
                        Glide.with(requireContext())
                            .load(bitmap)
                            .into(ivUserPic)
//                    ivProfilePic!!.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

            } else if (requestCode == CAMERA) {

                if (data != null) {
                    try {

                        val thumbnail = data!!.extras!!.get("data") as Bitmap
                        Glide.with(requireContext())
                            .load(thumbnail)
                            .into(ivUserPic)
//            ivProfilePic!!.setImageBitmap(thumbnail)

                        saveImage(thumbnail)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
        }

    }


    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


        val wallpaperDirectory =
            File((Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY))

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

//        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        var file: File? = null
//        try {
//            file = File.createTempFile(timeStamp, ".jpg", storageDir)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//        var mCurrentPhotoPath = java.lang.String.valueOf(Uri.fromFile(file))
//        uploadProfilePic(file!!)
//
//        return file.toString()

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val imgFile = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg")
            )
            imgFile.createNewFile()
            val fo = FileOutputStream(imgFile)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(imgFile.getPath()),
                arrayOf("image/jpeg"),
                null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + imgFile.getAbsolutePath())
            uploadProfilePic(imgFile)

            return imgFile.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
    }

    private fun uploadProfilePic(imgFile: File) {
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val multipartBody = MultipartBody.Part.createFormData(
            "file",
            imgFile.getName(),
            RequestBody.create(MediaType.parse("image/*"), imgFile)
        );
        val imageRequestBody = RequestBody.create(MediaType.parse("text/plain"), "customer_image");
        val mindFulNessAPI = retrofit.create(API::class.java)

        val call = mindFulNessAPI.updateUserProfilePhoto(
            multipartBody,
            imageRequestBody
        )

        call.enqueue(object : Callback<ProfileImage> {
            override fun onFailure(call: Call<ProfileImage>, t: Throwable) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<ProfileImage>, response: Response<ProfileImage>) {
                if (response.code() == 200) {
                    profileImageModel = response.body()!!
                    Toast.makeText(
                        requireActivity(),
                        "Profile picture uplode succesfully.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    MyApplication.prefs!!.profilePic = profileImageModel!!.getFileUrl()!!
//                    if (profileImageModel != null)
//                        Glide.with(requireActivity())
//                            .load(profileImageModel!!.getFileUrl())
//                            .into(ivUserPic)
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        })
    }


    private fun socialSignOut() {
        val intent = Intent(requireActivity(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun googleSignOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            object : ResultCallback<Status> {
                override fun onResult(p0: Status) {
                    socialSignOut()
                }
            })
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

}
