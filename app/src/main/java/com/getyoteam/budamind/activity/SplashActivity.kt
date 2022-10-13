package com.getyoteam.budamind.activity

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.Prefs
import com.getyoteam.budamind.R
import com.getyoteam.budamind.utils.AlarmReceiver
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class SplashActivity : Activity() {
    private lateinit var calendar: Calendar
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 7000 //5 seconds
    private var customerId: String = ""
    private var refralcustomerId: String = ""
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            if (customerId.equals("")) {
                intent = Intent(applicationContext, WelcomeActivity::class.java)
            } else {
                intent = Intent(applicationContext, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        customerId = MyApplication.prefs!!.userId.toString()
        val prfc = Prefs(this)
        prfc.clickaction = "HOME"
        prfc.wallateResponce = ""
        prfc.taskResponce = ""

        runOnUiThread {
            val path = "android.resource://" + packageName + "/" + R.raw.new_splash
            viewVideo.setVideoURI(Uri.parse(path))
            viewVideo.start()
        }


        printHashKey(this)

        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

        setTime(8, 0)

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }

        if (MyApplication.prefs!!.isFirstTime!!) {
            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
        }

        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    Log.d("mytag", "deepLink - ${pendingDynamicLinkData.link.toString()}")
                    deepLink = pendingDynamicLinkData.link
                    refralcustomerId =
                        deepLink.toString().substringAfterLast(getString(R.string.separator))
                    Log.d("mytag", "session id - $refralcustomerId")

                    if (!refralcustomerId.isNullOrBlank()) {
                        prfc.referalId = refralcustomerId
                    }

                }

            }
            .addOnFailureListener(this) {

            }

    }

    private fun setTime(selectedHour: Int, selectedMinute: Int) {
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
    private fun printHashKey(pContext: Context) {
        try {
            val info = pContext.packageManager.getPackageInfo(
                pContext.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("HashKey", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("HashKey", "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e("HashKey", "printHashKey()", e)
        }
    }

}