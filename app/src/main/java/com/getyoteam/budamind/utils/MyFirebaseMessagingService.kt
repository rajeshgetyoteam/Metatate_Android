package com.getyoteam.budamind.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Intent
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.getyoteam.budamind.MyApplication
import com.getyoteam.budamind.R
import android.net.Uri
import android.app.Notification
import android.media.AudioAttributes
import android.content.ContentResolver
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.getyoteam.budamind.BuildConfig
import com.getyoteam.budamind.Prefs
import com.getyoteam.budamind.activity.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: ${remoteMessage?.from}")
        Log.e(TAG, "From: ${remoteMessage}")
//
//        // Check if message contains a data payload.
        remoteMessage?.data?.isNotEmpty()?.let {
            Log.e(TAG, "Message data payload: " + remoteMessage.data)
//            // Handle message within 10 seconds
//            handleNow()
        }
//
//        // Check if message contains a notification payload.
        remoteMessage?.notification?.let {
            Log.e(TAG, "Message Notification Body: ${it.body}")

            var prf = Prefs(applicationContext)
            prf.clickaction = remoteMessage!!.data.get("click_action")!!
        }

        sendNotification(remoteMessage!!.data.get("message")!!)

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.e(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        MyApplication.prefs!!.firebaseToken = token!!

    }
    // [END on_new_token]

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.e(TAG, "Short lived task is done.")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT  or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = getString(R.string.app_name)
        val NOTIFICATION_SOUND_URI =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + BuildConfig.APPLICATION_ID + "/" + R.raw.notif)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        val notification: Notification
        notification = notificationBuilder.setSmallIcon(R.drawable.noti_icon).setTicker(getString(R.string.app_name)).setWhen(0)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.noti_icon))
            .setContentTitle(getString(R.string.app_name))
            .setSound(NOTIFICATION_SOUND_URI)
            .setSmallIcon(R.drawable.noti_icon)
            .setColor(ContextCompat.getColor(applicationContext,R.color.color_yellow))
            .setContentText(messageBody)
            .build()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(NOTIFICATION_SOUND_URI, attributes)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* ID of notification */, notification)
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}