package com.getyoteam.budamind

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.getyoteam.budamind.services.ExitService
import io.branch.referral.Branch
import java.util.*


class MyApplication : Application(), LifecycleObserver,Application.ActivityLifecycleCallbacks {
    private val mMediaItems = ArrayList<MediaBrowserCompat.MediaItem>()
    private val mTreeMap = TreeMap<String?, MediaMetadataCompat>()
    companion object {
        @JvmStatic
        fun getInstance(): MyApplication {
           return MyApplication()
        }
        @JvmStatic
        fun getPref(): Prefs {
            return prefs!!
        }

        var prefs: Prefs? = null
        var isHomeAPI=true
        var isLibraryAPI=true
        var isSoundAPI=true
        var isProfileAPI=true
    }

    fun getMediaItems(): List<MediaBrowserCompat.MediaItem> {
        return mMediaItems
    }

    fun getTreeMap(): TreeMap<String?, MediaMetadataCompat> {
        return mTreeMap
    }



    fun setMediaItems(mediaItems: List<MediaMetadataCompat>) {
        mMediaItems.clear()
        for (item in mediaItems) {
            Log.d("setMediaItems", "setMediaItems: called: adding media item: " + item.description)
            mMediaItems.add(
                MediaBrowserCompat.MediaItem(
                    item.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
            mTreeMap[item.description.mediaId] = item
        }
    }

    fun getMediaItem(mediaId: String): MediaMetadataCompat {
        return mTreeMap[mediaId]!!
    }

    override fun onCreate() {
        prefs = Prefs(applicationContext)
        super.onCreate()
        val config = PRDownloaderConfig.newBuilder()
            .setDatabaseEnabled(true)
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()
        PRDownloader.initialize(applicationContext, config)
        Branch.getAutoInstance(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(activity: Activity) {
//                val jsonObj = JSONObject()
//        jsonObj.put("userId", prefs!!.userId)
//        jsonObj.put("totalMinutes",prefs!!.totTimeSpent)
//        Utils.callUpdateTimeSpentSeconds(jsonObj)
//        val intent = Intent(this, ExitService::class.java)
//        startService(intent)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

//    override fun onActivityStopped(activity: Activity) {
//        val jsonObj = JSONObject()
//        jsonObj.put("userId", prefs!!.userId)
//        jsonObj.put("totalMinutes",prefs!!.totTimeSpent)
//        Utils.callUpdateTimeSpentSeconds(jsonObj)
//    }

    override fun onActivityDestroyed(activity: Activity) {
//        val jsonObj = JSONObject()
//        jsonObj.put("userId", prefs!!.userId)
//        jsonObj.put("totalMinutes",prefs!!.totTimeSpent)
//        Utils.callUpdateTimeSpentSeconds(jsonObj)
//        val intent = Intent(this, ExitService::class.java)
//        startService(intent)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        prefs!!.appInBacground = false
        Log.d("TimeSpentService", "App in background")
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        prefs!!.appInBacground = true
        Log.d("TimeSpentService", "App in foreground")
    }
}