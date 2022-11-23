package com.getyoteam.budamind.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.getyoteam.budamind.Model.Status
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.interfaces.ApiUtils
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExitService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        println("onTaskRemoved called")
        super.onTaskRemoved(rootIntent)
        val jsonObj = JSONObject()
        jsonObj.put("userId", MyApplication.prefs!!.userId)
        jsonObj.put("totalMinutes",MyApplication.prefs!!.totTimeSpent)
        val jsonParser = JsonParser()
        var gsonObject = JsonObject()
        gsonObject = jsonParser.parse(jsonObj.toString()) as JsonObject
        //print parameter
        Log.e("MY gson.JSON:  ", "AS PARAMETER  $gsonObject")
        try {
            ApiUtils.getAPIService().submitTimeSpent(gsonObject)
                .enqueue(object : Callback<Status> {
                    override fun onResponse(
                        call: Call<Status>,
                        response: Response<Status>
                    ) {
                        MyApplication.prefs!!!!.totTimeSpent = 0
                        Log.d("Response", response.toString())
                    }

                    override fun onFailure(
                        call: Call<Status>,
                        t: Throwable
                    ) {
                        Log.d("Response", t.toString())

                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.stopSelf()
    }
}