package com.getyoteam.budamind.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.getyoteam.budamind.Model.*
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_sign_in.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class SignInActivity : AppCompatActivity(), View.OnClickListener {
    private var alertDialog: AlertDialog? = null
    private var callbackManager: CallbackManager? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN: Int = 2001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        showDialog()
        tvSignIn.setOnClickListener(this)
        ivBack.setOnClickListener(this)

        callbackManager = CallbackManager.Factory.create();
        facebookLoginManagerCallBack()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LoginManager.getInstance().logOut()

        tvSignUpFacebook.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
        tvSignUpGoogle.setOnClickListener(this)


        scrollLogin.scrollTo(0, scrollLogin.bottom)
        v1.requestFocus();
//        ObjectAnimator.ofInt(scrollLogin, "scrollY", v1.getBottom()).setDuration(2000)
//            .start();
//        scrollLogin.scrollTo(0, resources.getDimension(R.dimen._100sdp).toInt());

        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpNewActivity::class.java)
            startActivity(intent)
        }

        tvTurms.setOnClickListener {
            val intent = Intent(this, TermsOfUseActivity::class.java)
            startActivity(intent)
        }

        tvPrivacy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
//
//        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//        etEmail.requestFocus();
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSignIn -> {
                val strEmail = etEmail.text

                if (!isValidEmailAddress(strEmail.toString())) {
                    Toast.makeText(
                        this,
                        getString(R.string.str_enter_valid_email),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
//                    alertDialog!!.show()
                    sendOtPEmail(strEmail.toString())

                }
            }
            R.id.tvSignUpFacebook -> {
                if (checkInternetConnection()) {
                    defaultLoginButtonFB.performClick()
                } else {
                    Toast.makeText(this, getString(R.string.str_check_internet_connection), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.tvSignUpGoogle -> {
                if (checkInternetConnection()) {
                    progressBarGoogle.visibility = View.VISIBLE
                    googleSignIn()
                } else {
                    Toast.makeText(this, getString(R.string.str_check_internet_connection), Toast.LENGTH_SHORT).show()
                }
            }

            R.id.tvTermsCondi -> {

            }
            R.id.ivBack -> {

                if (isTaskRoot) {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    finish()
                }

            }

        }
    }

    private fun facebookLoginManagerCallBack() {
        defaultLoginButtonFB.setPermissions("email")
        defaultLoginButtonFB.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {

                    val request = GraphRequest.newMeRequest(result!!.accessToken) { `object`, response ->
                        try {
                            val id = `object`.getString("id")
                            val profile_pic =
                                "http://graph.facebook.com/" + id + "/picture?type=large"
                            val name = `object`.optString("name");
                            val email = `object`.optString("email");
                            val gender = `object`.optString("gender");

                            Log.e("Facebook**", "**" + name + "**" + email + "**" + profile_pic)
                            signUp(
                                name.split(" ")[0],
                                name.split(" ")[1],  email,
                                profile_pic, "FACEBOOK",id
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
//                            dismissDialogLogin()
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "name,email,id,picture.type(large),gender")
                    request.parameters = parameters
                    request.executeAsync()

                }

                override fun onCancel() {
                    Log.e("FBLOGIN_FAILD", "Cancel")
                }

                override fun onError(error: FacebookException) {
                    Log.e("FBLOGIN_FAILD", "ERROR", error)
                }
            })
    }
    private fun googleSignIn() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                progressBarGoogle.visibility = View.INVISIBLE
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            }catch (e:Exception){
                e.printStackTrace()
            }

        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("Google**", "**" + account!!.displayName + "**" + account.email + "**" + account.photoUrl)
            val id = account.id.toString()
            val name = account.displayName
            val email = account.email.toString()
            var profile_pic = ""
            if (account.photoUrl != null){
                 profile_pic = account.photoUrl.toString()
            }

            if (!name!!.isEmpty() && name.contains(" ")) {
                signUp(
                    name!!.split(" ")[0],
                    name.split(" ")[1],  email,
                    profile_pic, "GOOGLE",id
                )
            } else {
                signUp(
                    "", "", email,
                    profile_pic, "GOOGLE",id
                )
            }
        } catch (e: ApiException) {
            Log.e("Google**", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(applicationContext, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUp(
        pFirstName: String, pLastName: String,
        pCustomerEmail: String,profilePic: String,pLoginThrough: String,
        pSocialid: String
    ) {
        val tz = TimeZone.getDefault();

        if (pLoginThrough.equals("FACEBOOK")){
            progressBarFacebook.visibility = View.VISIBLE
        }else{
            progressBarGoogle.visibility = View.VISIBLE
        }

        val loginResponseModel = SocialLoginModel(
            pFirstName, pLastName, pCustomerEmail,
            profilePic, pLoginThrough,pSocialid
        )
        val call = ApiUtils.getAPIService().socialLogin(loginResponseModel)

        call.enqueue(object : Callback<responceUserModel> {
            override fun onFailure(call: Call<responceUserModel>, t: Throwable) {
                if (pLoginThrough.equals("FACEBOOK")){
                    progressBarFacebook.visibility = View.INVISIBLE
                }else{
                    progressBarGoogle.visibility = View.INVISIBLE
                }
                Toast.makeText(applicationContext, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<responceUserModel>,
                response: Response<responceUserModel>
            ) {

                if (response.code() == 200) {

                    if (pLoginThrough.equals("FACEBOOK")){
                        progressBarFacebook.visibility = View.INVISIBLE
                    }else{
                        progressBarGoogle.visibility = View.INVISIBLE
                    }

                    val commonModel = response.body()!!
                    if (commonModel.status.equals(getString(R.string.str_success))) {
                        val userId = commonModel.result!!.userId
                        MyApplication.prefs!!.userId = userId.toString()
                        MyApplication.prefs!!.email = commonModel.result!!.email.toString()
                        MyApplication.prefs!!.first_name = commonModel.result!!.firstName.toString()
                        MyApplication.prefs!!.last_name = commonModel.result!!.lastName.toString()
                        MyApplication.prefs!!.loginType = pLoginThrough
                        MyApplication.prefs!!.authToken = commonModel.result!!.authToken.toString()
                        if (commonModel.result!!.anxiety == null){
                            commonModel.result!!.anxiety = 0
                        }
                        if (commonModel.result!!.focus == null){
                            commonModel.result!!.focus = 0
                        }
                        if (commonModel.result!!.gratitute == null){
                            commonModel.result!!.gratitute = 0
                        }
                        if (commonModel.result!!.happiness == null){
                            commonModel.result!!.happiness = 0
                        }
                        if (commonModel.result!!.meditate == null){
                            commonModel.result!!.meditate = 0
                        }
                        if (commonModel.result!!.selfEsteem == null){
                            commonModel.result!!.selfEsteem = 0
                        }
                        if (commonModel.result!!.sleep == null){
                            commonModel.result!!.sleep = 0
                        }
                        if (commonModel.result!!.stress == null){
                            commonModel.result!!.stress = 0
                        }
                        MyApplication.prefs!!.anxiety = commonModel.result!!.anxiety
                        MyApplication.prefs!!.focus = commonModel.result!!.focus
                        MyApplication.prefs!!.gratitute = commonModel.result!!.gratitute
                        MyApplication.prefs!!.happiness = commonModel.result!!.happiness
                        MyApplication.prefs!!.meditate = commonModel.result!!.meditate
                        MyApplication.prefs!!.selfEsteem = commonModel.result!!.selfEsteem
                        MyApplication.prefs!!.sleep = commonModel.result!!.sleep
                        MyApplication.prefs!!.stress = commonModel.result!!.stress

                        if(commonModel.result!!.anxiety == 1 ||
                            commonModel.result!!.focus == 1 ||
                            commonModel.result!!.gratitute == 1 ||
                            commonModel.result!!.happiness == 1 ||
                            commonModel.result!!.meditate == 1 ||
                            commonModel.result!!.selfEsteem == 1 ||
                            commonModel.result!!.stress == 1 ||
                            commonModel.result!!.sleep ==1 ){
                            MyApplication.prefs!!.isFirstApp = false
                        }


                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_login_successfully),
                            Toast.LENGTH_SHORT
                        ).show()

                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_COURSE")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_SOUND")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOMENT")
                        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATION")


                        if (commonModel.walletStatus.equals("created")) {


                            if (commonModel.result!!.firstName.isNullOrBlank()) {
                                val intent =
                                    Intent(applicationContext, UpdateProfileActivity::class.java)
                                intent.putExtra("isFirstTime", true)
                                intent.putExtra("wallateStatus", commonModel.walletStatus)
                                intent.putExtra("credited", commonModel.credited)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent)

                            } else {
                                if (MyApplication.prefs!!.isFirstApp!!) {
                                    val intent = Intent(
                                        applicationContext,
                                        ChooseYourGoalActivity::class.java
                                    )
                                    intent.putExtra("isFirstTime", true)
                                    intent.putExtra("wallateStatus", commonModel.walletStatus)
                                    intent.putExtra("credited", commonModel.credited)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent)
                                }
                            }


                        } else {

                            if (commonModel.result!!.firstName.isNullOrBlank()) {
                                val intent =
                                    Intent(applicationContext, UpdateProfileActivity::class.java)
                                intent.putExtra("isFirstTime", true)
                                intent.putExtra("wallateStatus", "")
                                intent.putExtra("credited", "")
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent)

                            } else {
                                if (MyApplication.prefs!!.isFirstApp!!) {
                                    val intent =
                                        Intent(
                                            applicationContext,
                                            ChooseYourGoalActivity::class.java
                                        )
                                    intent.putExtra("isFirstTime", true)
                                    intent.putExtra("wallateStatus", "")
                                    intent.putExtra("credited", "")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent)
                                }
                            }


                        }

                        finish()


                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (pLoginThrough.equals("FACEBOOK")){
                        progressBarFacebook.visibility = View.INVISIBLE
                    }else{
                        progressBarGoogle.visibility = View.INVISIBLE
                    }
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
    }
    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun showDialog() {
        // Late initialize an alert dialog object

        // Initialize a new instance of alert dialog builder object
        val builder = AlertDialog.Builder(this)

        // Set a title for alert dialog
        builder.setTitle("Wallet")

        // Set a message for alert dialog
        builder.setMessage("Create/Import Wallet?")


        // On click listener for dialog buttons
        val dialogClickListener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val intent = Intent(this, ImportWallatActivity::class.java)
                    startActivity(intent)
                    alertDialog!!.dismiss()
                }
                DialogInterface.BUTTON_NEGATIVE -> {

                    Toast.makeText(
                        this,
                        "Wallet Created Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    alertDialog!!.dismiss()
                }
            }
        }


        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Import Wallet", dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNegativeButton("Create Wallet", dialogClickListener)

        // Set the alert dialog neutral/cancel button

        // Initialize the AlertDialog using builder object
        alertDialog = builder.create()

    }

    private fun sendOtPEmail(pCustomerEmail: String) {

        progressBarSignIn.visibility = View.VISIBLE

        val call = ApiUtils.getAPIService().sendVerificationMail(pCustomerEmail)

        call.enqueue(object : Callback<StatusModel> {
            override fun onFailure(call: Call<StatusModel>, t: Throwable) {
                progressBarSignIn.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<StatusModel>, response: Response<StatusModel>) {
                if (response.code() == 200) {
                    progressBarSignIn.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.status.equals(getString(R.string.str_success))) {

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_otp_sent),
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(applicationContext, VerifyOTPActivity::class.java)
                        intent.putExtra("email", pCustomerEmail)
                        intent.putExtra("otp", commonModel.otp)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressBarSignIn.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


    private fun loginWithEmail(
        pPassword: String,
        pCustomerEmail: String,
        pLoginThrough: String
    ) {
        val tz = TimeZone.getDefault();
        progressBarSignIn.visibility = View.VISIBLE
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(API::class.java)
        val loginResponseModel = LoginResponseModel(
            "", "", pPassword, pCustomerEmail,
            "", pLoginThrough, MyApplication.prefs!!.firebaseToken,
            tz.id.toString()
        )
        val call = ApiUtils.getAPIService().login(loginResponseModel)

        call.enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                progressBarSignIn.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<CommonModel>, response: Response<CommonModel>) {
                if (response.code() == 200) {
                    progressBarSignIn.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.getStatus().equals(getString(R.string.str_success))) {
                        val userId = commonModel.getUserId()
                        MyApplication.prefs!!.userId = userId.toString()
                        MyApplication.prefs!!.loginType = pLoginThrough
                        MyApplication.prefs!!.authToken = commonModel.getAuthToken()!!
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_login_successfully),
                            Toast.LENGTH_SHORT
                        ).show()

                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_COURSE")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_SOUND")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOMENT")
                        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATION")

                        if (MyApplication.prefs!!.subPurchase) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent)
                        } else {
//                            val intent = Intent(applicationContext, SubscribeActivity::class.java)
//                            intent.putExtra("fromScreen", "Login")
//                            startActivity(intent)
                        }
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            commonModel.getMessage(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }
}
