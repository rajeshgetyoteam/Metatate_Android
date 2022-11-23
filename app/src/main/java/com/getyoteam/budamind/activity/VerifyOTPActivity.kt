package com.getyoteam.budamind.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getyoteam.budamind.Model.StatusModel
import com.getyoteam.budamind.Model.responceUserModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_verified_otp.*
import kotlinx.android.synthetic.main.activity_verified_otp.ivBack
import kotlinx.android.synthetic.main.activity_verified_otp.v1
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTPActivity : AppCompatActivity(), View.OnClickListener {

    private var otp: String = ""
    private var strOtp: String = ""
    private var strEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verified_otp)
        strEmail = intent.getStringExtra("email").toString()
        strOtp = intent.getStringExtra("otp").toString()


        scrollOtp.scrollTo(0, scrollOtp.bottom)
        v1.requestFocus()

        etOne.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().trim().length == 1) {
                    otp += s.toString().trim()
                    etTwo.requestFocus()
                } else if (s.toString().isEmpty()) {
                    otp = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        etTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    otp += s.toString().trim()
                    etThree.requestFocus()
                } else if (s.toString().isEmpty()) {
                    otp = removeLastChar(otp)
                    etOne.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        etThree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    otp += s.toString().trim()
                    etFour.requestFocus()
                } else if (s.toString().isEmpty()) {
                    otp = removeLastChar(otp)
                    etTwo.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        etFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 1) {
                    otp += s.toString().trim()
                    if (otp.length > 3) {
                        if (checkInternetConnection()) {

                            if (otp == strOtp) {
                                hideKeyBoard()
                                verifyOTP(strEmail, otp)
                            } else {
                                Toast.makeText(
                                    this@VerifyOTPActivity, "Please Enter Correct OTP",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(
                                this@VerifyOTPActivity,
                                getString(R.string.str_check_internet_connection),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (s.toString().isEmpty()) {
                    otp = removeLastChar(otp)
                    etThree.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        tvSubmit.setOnClickListener {
            if (otp.length < 4) {
                Toast.makeText(this, "OTP not less than 4 digit", Toast.LENGTH_SHORT).show()
            } else {
                verifyOTP(strEmail, otp)
            }
        }
        tvResend.setOnClickListener(this)
        ivBack.setOnClickListener(this)
    }

    private fun removeLastChar(str: String): String {
        return str.substring(0, str.length - 1)
    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.tvResend -> {
                sendOtPEmail(strEmail)
            }

                  R.id.ivBack -> {
                finish()
            }
        }
    }

    private fun hideKeyBoard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun verifyOTP(pCustomerEmail: String, otp: String) {
   
        progressBarSubmit.visibility = View.VISIBLE

        val call = ApiUtils.getAPIService()
            .loginViaOtp(pCustomerEmail, otp, MyApplication.prefs!!.firebaseToken)

        call.enqueue(object : Callback<responceUserModel> {
            override fun onFailure(call: Call<responceUserModel>, t: Throwable) {
                progressBarSubmit.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<responceUserModel>,
                response: Response<responceUserModel>
            ) {
                if (response.code() == 200) {
                    progressBarSubmit.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.status.equals(getString(R.string.str_success))) {
                        val userId = commonModel.result!!.userId
                        MyApplication.prefs!!.userId = userId.toString()
                        MyApplication.prefs!!.email = commonModel.result!!.email.toString()
                        MyApplication.prefs!!.first_name = commonModel.result!!.firstName.toString()
                        MyApplication.prefs!!.last_name = commonModel.result!!.lastName.toString()
                        MyApplication.prefs!!.loginType = "Email"
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

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_login_successfully),
                            Toast.LENGTH_SHORT
                        ).show()

                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_COURSE")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_SOUND")
                        FirebaseMessaging.getInstance().subscribeToTopic("NEW_MOMENT")
                        FirebaseMessaging.getInstance().subscribeToTopic("PUSH_NOTIFICATION")
                        if(commonModel.result!!.anxiety == 1 ||commonModel.result!!.focus == 1 ||commonModel.result!!.gratitute == 1 ||
                            commonModel.result!!.happiness == 1 ||
                            commonModel.result!!.meditate == 1 || commonModel.result!!.selfEsteem == 1 ||
                            commonModel.result!!.stress == 1 || commonModel.result!!.sleep ==1 ){
                            MyApplication.prefs!!.isFirstApp = false
                        }

                        if (commonModel.walletStatus.equals("created")) {

                            if (commonModel.result!!.firstName.isNullOrBlank()) {
                                val intent =
                                    Intent(applicationContext, UpdateProfileActivity::class.java)
                                intent.putExtra("isFirstTime", true)
                                intent.putExtra("wallateStatus", commonModel.walletStatus)
                                intent.putExtra("credited", commonModel.credited)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                } else {
                                    val intent =
                                        Intent(applicationContext, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
                        ).show()
                    }
                } else {
                    progressBarSubmit.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


    private fun sendOtPEmail(pCustomerEmail: String) {

        progressBar.visibility = View.VISIBLE

        val call = ApiUtils.getAPIService().sendVerificationMail(pCustomerEmail)

        call.enqueue(object : Callback<StatusModel> {
            override fun onFailure(call: Call<StatusModel>, t: Throwable) {
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    getString(R.string.str_something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onResponse(call: Call<StatusModel>, response: Response<StatusModel>) {
                if (response.code() == 200) {
                    progressBar.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.status.equals(getString(R.string.str_success))) {

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_otp_sent),
                            Toast.LENGTH_SHORT
                        ).show()

                        strOtp = commonModel.otp.toString()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            commonModel.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.str_something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }


}
