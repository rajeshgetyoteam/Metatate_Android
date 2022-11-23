package com.getyoteam.budamind.services

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.getyoteam.budamind.MyApplication
import java.util.*


class TimeSpentService : JobIntentService() {
    var time = 0

    override fun onCreate() {
        super.onCreate()

        object : CountDownTimer(3600000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG,"seconds remaining: " + millisUntilFinished / 1000)

                if (MyApplication.prefs!!.appInBacground){
                    time += 1
                    MyApplication.prefs!!.totTimeSpent = time
                    Log.d("okh", "tot time : $time")
                }

//                val jsonObj = JSONObject()
//                jsonObj.put("userId", MyApplication.prefs!!!!.userId)
//                jsonObj.put("totalMinutes",1)
//                Utils.callUpdateTimeSpentSeconds(jsonObj)

            }

            override fun onFinish() {

                Log.d(TAG, "done")
            }
        }.start()

    }



    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")

        isRunning = false
    }

    override fun onStopCurrentWork(): Boolean {
        return super.onStopCurrentWork()
        Log.d(TAG, "onStopCurrentWork")
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")


    }


    private fun onErrors(throwable: Throwable) {
        sendBroadcastMeaasge("Error in file upload " + throwable.message)
        Log.d(TAG, "onErrors: ", throwable)
    }

    private fun onProgress(progress: Double) {
        sendBroadcastMeaasge("Uploading in progress... " + (100 * progress).toInt())
        Log.i(TAG, "onProgress: $progress")
    }

    private fun onSuccess(text: String) {
        sendBroadcastMeaasge("File uploading successful ")
        Log.i(TAG, "onSuccess: File Uploaded : $text")
    }

    private fun sendBroadcastMeaasge(message: String?) {
        val localIntent = Intent("my.own.broadcast")
        localIntent.putExtra("result", message)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }
    
    companion object {
        var isRunning = false
        private const val TAG = "TimeSpentService"
        val t = Date().time.toInt()

        private val JOB_ID = t

        fun enqueueWork(context: Context?, intent: Intent?) {
            Log.d(TAG, "enqueueWork : $t")
            isRunning = true
            enqueueWork(context!!, TimeSpentService::class.java, t, intent!!)

        }

    }


}