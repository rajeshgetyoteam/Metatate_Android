package com.getyoteam.budamind.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.API
import kotlinx.android.synthetic.main.activity_forgot_password.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {


    private var isFromSignIn: Boolean?=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        tvSubmit.setOnClickListener(this)

        val bundle = intent.extras
        if(bundle!=null){
            isFromSignIn = bundle.getBoolean("isFromSignIn")
        }

        if(isFromSignIn!!){
            tvHeader.setText("Forgot Password")
        }else{
            tvHeader.setText(getString(R.string.str_reset_password))
        }
        ivClose.setOnClickListener { finish() }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSubmit -> {
                val strEmail = etEmail.text

                if (!isValidEmailAddress(strEmail.toString())) {
                    Toast.makeText(this,getString(R.string.str_enter_valid_email),Toast.LENGTH_SHORT).show()
//                    etEmail.error = getString(R.string.str_enter_valid_email)
                }else {
                    forgotPassword(strEmail.toString())
                }
            }
        }
    }


    private fun forgotPassword(
        pCustomerEmail: String
    ) {
        progressBarSubmit.visibility = View.VISIBLE
        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(API::class.java)
        val call = ApiUtils.getAPIService().forgotPassword(pCustomerEmail)

        call.enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                progressBarSubmit.visibility = View.INVISIBLE
                Toast.makeText(this@ForgotPasswordActivity, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(
                call: Call<CommonModel>,
                response: Response<CommonModel>
            ) {
                if (response.code() == 200) {
                    progressBarSubmit.visibility = View.INVISIBLE
                    val commonModel = response.body()!!
                    if (commonModel.getStatus().equals(getString(R.string.str_success))) {
                        MyApplication.prefs!!.emailAddress = pCustomerEmail
                        val intent = Intent(applicationContext, VerifyOTPActivity::class.java)
                        startActivity(intent)
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


    fun isValidEmailAddress(email: String): Boolean {
        val emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
