package com.getyoteam.budamind.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.AppDatabase
import kotlinx.android.synthetic.main.activity_terms_of_use.*


class PrivacyPolicyActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_of_use)

        tvHeader.text = getText(R.string.str_privacy_of_use)
//        ivHeaderLeft.setImageResource(R.drawable.ic_back_white)
//        webview.loadUrl("https://claritymeditations.wordpress.com/terms-of-use/");

        webview.loadDataWithBaseURL("", getString(R.string.str_privacy_policy), "text/html", "UTF-8", "");

        ivHeaderLeft.setOnClickListener {
            finish()
        }


    }

}