package com.getyoteam.budamind.utils

import android.content.Context
import android.content.SharedPreferences
import com.getyoteam.budamind.Model.MomentListModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.getyoteam.budamind.Model.SoundListModel
import java.util.ArrayList

class StorageUtil(requireContext: Context) {
    private val STORAGE = " com.valdioveliu.valdio.audioplayer.STORAGE"
    private var preferences: SharedPreferences? = null
    private var context = requireContext


    fun storeAudio(arrayList: ArrayList<SoundListModel>, arrayMomentList: ArrayList<MomentListModel>) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)

        val editor = preferences!!.edit()
        if (arrayList.size > 0) {
            val gson = Gson()
            val json = gson.toJson(arrayList)
            editor.putString("audioArrayList", json)
        }
        if (arrayMomentList.size > 0) {
            val gsonMoment = Gson()
            val jsonMoment = gsonMoment.toJson(arrayMomentList)
            editor.putString("audioMomentArrayList", jsonMoment)
        }
        editor.apply()
    }

    fun loadAudio(): ArrayList<SoundListModel>? {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences!!.getString("audioArrayList", null)
        val type = object : TypeToken<ArrayList<SoundListModel>>() {

        }.type
        return gson.fromJson<ArrayList<SoundListModel>>(json, type)
    }

    fun loadMomentAudio(): ArrayList<MomentListModel>? {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences!!.getString("audioMomentArrayList", null)
        val type = object : TypeToken<ArrayList<MomentListModel>>() {

        }.type
        return gson.fromJson<ArrayList<MomentListModel>>(json, type)
    }

    fun storeAudioMomentIndex(index: Int) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt("audioMomentIndex", index)
        editor.apply()
    }

    fun loadAudioMomentIndex(): Int {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences!!.getInt("audioMomentIndex", -1)//return -1 if no data found
    }

    fun storeAudioIndex(index: Int) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.putInt("audioIndex", index)
        editor.apply()
    }

    fun loadAudioIndex(): Int {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        return preferences!!.getInt("audioIndex", -1)//return -1 if no data found
    }

    fun clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE)
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }
}