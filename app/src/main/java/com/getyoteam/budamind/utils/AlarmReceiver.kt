package com.getyoteam.budamind.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri


/**
 * Created by Govind on 2/27/2018.
 */
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        val service = Intent(context, AlarmNotificationService::class.java)
        service.data = Uri.parse("custom://" + System.currentTimeMillis())
        context!!.startService(service)
    }
}