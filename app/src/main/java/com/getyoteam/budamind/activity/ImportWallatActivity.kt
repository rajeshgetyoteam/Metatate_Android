package com.getyoteam.budamind.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.activity_import_wallate.*
import kotlinx.android.synthetic.main.activity_receive.ivHeaderLeft


class ImportWallatActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_wallate)


        ivHeaderLeft.setOnClickListener { finish() }


        tvConfirm.setOnClickListener { finish() }


    }


}
