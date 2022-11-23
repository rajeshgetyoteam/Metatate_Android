package com.getyoteam.budamind.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.app.NotificationChannel
import android.content.ContentResolver
import android.net.Uri
import com.getyoteam.budamind.R
import com.getyoteam.budamind.activity.MainActivity

class AlarmNotificationService : IntentService("AlarmNotificationService") {
    private val ACTION_SNOOZE: String? = "action_stop"
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {

        const val CHANNEL_ID = "samples.notification.devdeeds.com.CHANNEL_ID"
        const val CHANNEL_NAME = "Alarm Notification"
    }

    override fun onHandleIntent(intent: Intent?) {
        //Create Channel
        createChannel()

        val context = this.applicationContext
        var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyIntent = Intent(this, MainActivity::class.java)
        val title = getString(R.string.app_name)
        val message = "Make time for what matters. A relaxed body, a peaceful mind and a nourushed heart"
        notifyIntent.putExtra("message", message)
        notifyIntent.putExtra("notification", true)

        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val snoozeIntent = Intent(this, StopRingReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(ACTION_SNOOZE, mNotificationId)
        }
        val snoozePendingIntent: PendingIntent =
            PendingIntent.getBroadcast(this, 0, snoozeIntent, 0 or PendingIntent.FLAG_IMMUTABLE)
        val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val res = this.resources
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val NOTIFICATION_SOUND_URI =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/raw/sample")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotification = Notification.Builder(this, CHANNEL_ID)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.noti_icon)
                .setColor(resources.getColor(R.color.color_yellow))
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.noti_icon))
                .setAutoCancel(false)
                .setOngoing(true)
                .setStyle(Notification.BigTextStyle().bigText(message))
                .setContentText(message)
                .setSound(NOTIFICATION_SOUND_URI)
                .addAction(R.drawable.noti_icon, getString(R.string.str_stop), snoozePendingIntent)
                .build()
        } else {
            mNotification = Notification.Builder(this)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.noti_icon)
                .setColor(resources.getColor(R.color.color_yellow))
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.noti_icon))
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(Notification.BigTextStyle().bigText(message))
                .setSound(NOTIFICATION_SOUND_URI)
                .setContentText(message)
                .addAction(R.drawable.noti_icon, getString(R.string.str_stop), snoozePendingIntent)
                .build()
        }
        val startIntent = Intent(context, RingtonePlayingService::class.java)
        context.startService(startIntent)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mNotificationId is a unique int for each notification that you must define
        notificationManager.notify(mNotificationId, mNotification)

    }
}