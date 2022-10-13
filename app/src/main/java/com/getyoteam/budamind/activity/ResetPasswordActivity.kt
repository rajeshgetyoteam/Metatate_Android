package com.getyoteam.budamind.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import com.getyoteam.budamind.Model.CommonModel
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import com.getyoteam.budamind.interfaces.ApiUtils
import com.getyoteam.budamind.interfaces.ClarityAPI
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_reset_password.cbPassword
import kotlinx.android.synthetic.main.activity_reset_password.etPassword
import kotlinx.android.synthetic.main.activity_reset_password.etReEnterPassword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ResetPasswordActivity : AppCompatActivity(), View.OnClickListener {

    private var isFromSignIn: Boolean? = false
    private var otp: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        cbPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            //            etReEnterPassword.inputType=InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            if (isChecked) {
                buttonView.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_open))
                etReEnterPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                buttonView.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.ic_eye_close))
                etReEnterPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        tvSubmit.setOnClickListener(this)
        ivClose.setOnClickListener { finish() }

    }

    private fun checkInternetConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSubmit -> {
                val userId = MyApplication.prefs!!.userId
                val firebaseToken = MyApplication.prefs!!.firebaseToken
                val strPassword = etPassword.text.toString()
                val strConfirmPassword = etReEnterPassword.text.toString()

                if (strPassword.length == 0) {
                    Toast.makeText(this,getString(R.string.str_enter_password),Toast.LENGTH_SHORT).show()
//                    etPassword.error = getString(R.string.str_enter_password)
                } else if (!strPassword.equals(strConfirmPassword)) {
                    Toast.makeText(this,getString(R.string.str_possword_does_not_match),Toast.LENGTH_SHORT).show()
//                    etReEnterPassword.error = getString(R.string.str_possword_does_not_match)
                } else {
                    resetPassword(
                        userId,
                        strPassword,
                        firebaseToken
                    )
                }
            }
        }
    }

    private fun resetPassword(
        userId: String, pPassword: String,
        firebaseToken: String
    ) {
        progressBarSubmit.visibility = View.VISIBLE
        val retrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val clarityAPI = retrofit.create(ClarityAPI::class.java)
        val call = ApiUtils.getAPIService().setPassword(userId, firebaseToken, pPassword)

        call.enqueue(object : Callback<CommonModel> {
            override fun onFailure(call: Call<CommonModel>, t: Throwable) {
                progressBarSubmit.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, getString(R.string.str_something_went_wrong), Toast.LENGTH_SHORT)
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
                        MyApplication.prefs!!.authToken = commonModel.getAuthToken().toString()
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.str_login_successfully),
                                Toast.LENGTH_SHORT
                            ).show()
                        MyApplication.prefs!!.loginType=getString(R.string.str_email)
                        if(MyApplication.prefs!!.subPurchase) {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                        }else{
                            val intent = Intent(applicationContext, SubscribeActivity::class.java)
                            intent.putExtra("fromScreen","Login")
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
