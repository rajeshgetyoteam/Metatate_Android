package com.getyoteam.budamind.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.getyoteam.budamind.MyApplication
import java.util.*

class SampleBootReceiver : BroadcastReceiver() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    override fun onReceive(context: Context, intent: Intent) {
        val hour = MyApplication.prefs!!.hour
        val min = MyApplication.prefs!!.minute

        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(context, 0, intent, 0)
        }
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
// Set the alarm to start at 8:30 a.m.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
            }

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
            alarmMgr?.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    alarmIntent
            )
        }
    }

}