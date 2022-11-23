package com.getyoteam.budamind.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.Model.LoginResponseModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.actionbar_layout.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.defaultLoginButtonFB
import kotlinx.android.synthetic.main.activity_sign_up.progressBarGoogle
import kotlinx.android.synthetic.main.activity_sign_up.tvSignIn
import kotlinx.android.synthetic.main.activity_sign_up.tvSignUpFacebook
import kotlinx.android.synthetic.main.activity_sign_up.tvSignUpGoogle
import kotlinx.android.synthetic.main.activity_sign_up.tvTermsCondi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    private var callbackManager: CallbackManager? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN: Int = 2001
    private var acceptTerms: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        callbackManager = CallbackManager.Factory.create();
        facebookLoginManagerCallBack()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tvSignUpFacebook.setOnClickListener(this)
        tvSignUp.setOnClickListener(this)
        tvSignUpGoogle.setOnClickListener(this)
        tvTermsCondi.setOnClickListener(this)
        tvSignIn.setOnClickListener(this)
        ivHeaderLeft.setOnClickListener(this)

        cbPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            //            etReEnterPassword.inputType=InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            if (isChecked) {
                buttonView.setButtonDrawable(ContextCompat.getDrawable(this@SignUpActivity, R.drawable.ic_eye_open))
                etReEnterPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                buttonView.setButtonDrawable(ContextCompat.getDrawable(this@SignUpActivity, R.drawable.ic_eye_close))
                etReEnterPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }


        cbTerms.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked)
                acceptTerms = true
            else
                acceptTerms = false
        }
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onClick(v: View?) {
        when (v?.id) {
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
            R.id.tvSignUp -> {
                val strEmail = etEmail.text
                val strPassword = etPassword.text.toString()
                val strConfirmPassword = etReEnterPassword.text.toString()
                val strFirstName = etFirstName.text
                val strLastName = etLastName.text

                if (strFirstName.length == 0) {
                    Toast.makeText(this,getString(R.string.str_enter_first_name),Toast.LENGTH_SHORT).show()
//                    etFirstName.error = getString(R.string.str_enter_first_name)
                } else if (strLastName.length == 0) {
                    Toast.makeText(this,getString(R.string.str_enter_last_name),Toast.LENGTH_SHORT).show()
//                    etLastName.error = getString(R.string.str_enter_last_name)
                } else if (!isValidEmailAddress(strEmail.toString())) {
                    Toast.makeText(this,getString(R.string.str_enter_valid_email),Toast.LENGTH_SHORT).show()
//                    etEmail.error = getString(R.string.str_enter_valid_email)
                } else if (strPassword.length == 0) {
                    Toast.makeText(this,getString(R.string.str_enter_password),Toast.LENGTH_SHORT).show()
//                    etPassword.error = getString(R.string.str_enter_password)
                } else if (!strPassword.equals(strConfirmPassword)) {
                    Toast.makeText(this,getString(R.string.str_possword_does_not_match),Toast.LENGTH_SHORT).show()
//                    etReEnterPassword.error = getString(R.string.str_possword_does_not_match)
                } else if (!acceptTerms) {
                    Toast.makeText(this@SignUpActivity,
                        getString(R.string.str_please_accept_tems), Toast.LENGTH_SHORT).show()
                } else {
                    signUp(
                        strFirstName.toString(),
                        strLastName.toString(),
                        strEmail.toString(),
                        strPassword.toString(),
                        getString(R.string.str_email)
                    )
                }
            }
            R.id.tvSignIn -> {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.tvTermsCondi -> {

            }
            R.id.ivHeaderLeft -> {
                finish()
            }
        }
    }

    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
    private fun facebookLoginManagerCallBack() {
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
//                            signUp(
//                                name!!.split(" ")[0],
//                                name.split(" ")[1], "", email,
//                                profile_pic, id, getString(R.string.str_facebook), gender
//                            )
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
            progressBarGoogle.visibility = View.INVISIBLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("Google**", "**" + account!!.displayName + "**" + account.email + "**" + account.photoUrl)
            val name = account.displayName
            val email = account.email.toString()
            val profile_pic = account.photoUrl.toString()
            if (!name!!.isEmpty() && name.contains(" ")) {
//                signUp(
//                    name!!.split(" ")[0],
//                    name.split(" ")[1], "", email,
//                    profile_pic, "", getString(R.string.str_google), ""
//                )
            } else {
//                signUp(
//                    "", "", "", email,
//                    profile_pic, "", getString(R.string.str_google), ""
//                )
            }
        } catch (e: ApiException) {
            Log.e("Google**", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(applicationContext, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUp(
        pFirstName: String, pLastName: String,
        pCustomerEmail: String, pPassword: String,
        pLoginThrough: String
    ) {
        val tz = TimeZone.getDefault();
        progressBarSignUp.visibility = View.VISIBLE
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(API::class.java)
        val loginResponseModel = LoginResponseModel(
            pFirstName, pLastName, pPassword, pCustomerEmail,
            "", pLoginThrough, MyApplication.prefs!!.firebaseToken, tz.id.toString()
        )
        val call = ApiUtils.getAPIService().signUp(loginResponseModel)

        call.enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                progressBarSignUp.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<CommonModel>,
                response: Response<CommonModel>
            ) {
                progressBarSignUp.visibility = View.INVISIBLE
                if (response.code() == 200) {
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

                        if(MyApplication.prefs!!.subPurchase) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent)
                        }else{
//                            val intent = Intent(applicationContext, SubscribeActivity::class.java)
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.putExtra("fromScreen","Login")
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
