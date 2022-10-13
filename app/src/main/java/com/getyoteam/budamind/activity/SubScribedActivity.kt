package com.getyoteam.budamind.activity

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import android.content.Intent
import android.net.Uri
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import kotlinx.android.synthetic.main.activity_sub_scribed.*
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException


class SubScribedActivity : AppCompatActivity() {

    private var strSubscriptionTeamStartDate: String? = ""
    private var strSubscriptionTeamEndDate: String? = ""
    private var strSubscriptionDate: Long? = 0
    private var strSubscriptionType: String = ""
    var sku = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_scribed)

        val builder = SpannableStringBuilder()

        val red = getString(R.string.str_manage_or_delete_your_subscription_by_clinking_here)
        val redSpannable = SpannableString(red)
        redSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_text_color_light)),
            0,
            red.length,
            0
        )
        builder.append(redSpannable)

        val blue = " here"
        val whiteSpannable = SpannableString(blue)
        whiteSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_view_all)),
            0,
            blue.length,
            0
        )
        builder.append(whiteSpannable)

        val black = "."
        val blackSpannable = SpannableString(black)
        blackSpannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.color_text_color_light)),
            0,
            black.length,
            0
        )
        builder.append(blackSpannable)

        tvManageSubscription.setText(builder, TextView.BufferType.SPANNABLE);


        strSubscriptionType = MyApplication.prefs!!.subscriptionType
        strSubscriptionDate = MyApplication.prefs!!.purchaseDate
        if (strSubscriptionType.equals(getString(R.string.str_monthly))) {
            sku = "gold_monthly1"
        } else {
            sku = "gold_yearly"
        }
        tvSubscriptionPlan.setText(strSubscriptionType)

            getDateFromMilliSec(strSubscriptionDate!!)

        tvManageSubscription.setOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions?sku=$sku&package=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {
                //showToast("Cant open the browser")
                e.printStackTrace()
            }
        }
    }

    fun parseDateToddMMyyyy(time: String): String? {
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd MMMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }


    private fun getDateFromMilliSec(milliSec: Long) {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd MMMM yyyy")

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSec)
        val purchaseDate = formatter.format(calendar.getTime())
        tvSubscriptionPlanStartDate.setText(purchaseDate)

        //get date of renewal
        if (strSubscriptionType.equals(getString(R.string.str_monthly))) {
            calendar.add(Calendar.DATE, 30)
        } else {
            calendar.add(Calendar.DATE, 365)
        }
        val renewalDate = formatter.format(calendar.getTime())

        tvSubscriptionPlanEndDate.setText(renewalDate)
    }
}
