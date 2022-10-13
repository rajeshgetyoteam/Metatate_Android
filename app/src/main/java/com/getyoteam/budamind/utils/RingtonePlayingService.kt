package com.getyoteam.budamind.utils

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.IBinder
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi


class RingtonePlayingService : Service() {


    private var ringtone: Ringtone? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val ringtoneUri = Uri.parse(intent?.getExtras()?.getString("ringtone-uri"))
        val alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        var NOTIFICATION_SOUND_URI =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/raw/sample")
        ringtone = RingtoneManager.getRingtone(this, NOTIFICATION_SOUND_URI)
        ringtone!!.play()
        ringtone!!.isLooping=true
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        ringtone!!.stop()
    }

}