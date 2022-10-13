package com.getyoteam.budamind

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import io.branch.referral.Branch
import java.util.*


class MyApplication : Application() {
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
        Branch.getAutoInstance(this);

    }
}