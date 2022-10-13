package com.getyoteam.budamind.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.activity_export_private_key.*
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.android.synthetic.main.activity_receive.ivHeaderLeft


class ExportPrivateActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_export_private_key)

        tvInfo.text = "Type your budamind password"

        ivHeaderLeft.setOnClickListener { finish() }


        tvDone.setOnClickListener { finish() }

        tvConfirm.setOnClickListener {
            tvInfo.text = "This is your Private Key"
            etPassword.visibility = View.GONE
            layDone.visibility = View.VISIBLE
            layConfirm.visibility = View.GONE
            layPrivateKey.visibility = View.VISIBLE

        }

    }


}
