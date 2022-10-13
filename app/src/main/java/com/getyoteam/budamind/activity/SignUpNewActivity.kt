package com.getyoteam.budamind.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.Model.LoginResponseModel
import com.getyoteam.budamind.Model.StatusModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_sign_up_new.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class SignUpNewActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_new)

        tvSignIn.setOnClickListener(this)
        ivBack.setOnClickListener(this)
        tvSignIn.setOnClickListener {
            finish()
        }

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

                    finish()
//                    sendOtPEmail(strEmail.toString())

                }
            }


            R.id.tvTermsCondi -> {

            }
            R.id.ivBack -> {

                finish()

            }

        }
    }

    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
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
                )
                    .show()
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


                        val intent = Intent(applicationContext, VerifyedOTPActivity::class.java)
                        intent.putExtra("email", pCustomerEmail)
                        intent.putExtra("otp", commonModel.otp)
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.str_something_went_wrong),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    progressBarSignIn.visibility = View.INVISIBLE
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
        val clarityAPI = retrofit.create(ClarityAPI::class.java)
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
                            val intent = Intent(applicationContext, SubscribeActivity::class.java)
                            intent.putExtra("fromScreen", "Login")
                            startActivity(intent)
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
