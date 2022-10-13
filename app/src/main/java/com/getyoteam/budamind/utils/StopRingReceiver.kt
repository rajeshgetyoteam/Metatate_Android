package com.getyoteam.budamind.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Created by Govind on 2/27/2018.
 */
class StopRingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(1000)

        val stopIntent = Intent(context, RingtonePlayingService::class.java)
        context.stopService(stopIntent)
    }
}