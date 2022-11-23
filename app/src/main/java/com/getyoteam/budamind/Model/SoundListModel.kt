package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity
class SoundListModel {
    @SerializedName("soundId")
    @Expose
    @PrimaryKey
    private var soundId: Int? = null

    @SerializedName("title")
    @Expose
    @ColumnInfo(name = "title")
    private var title: String? = null

    @SerializedName("subtitle")
    @Expose
    @ColumnInfo(name = "subtitle")
    private var subtitle: String? = null

    @SerializedName("minutes")
    @Expose
    @ColumnInfo(name = "minutes")
    private var minutes: String? = null

    @SerializedName("seconds")
    @Expose
    @ColumnInfo(name = "seconds")
    private var seconds: String? = null

    @SerializedName("coinForContent")
    @Expose
    @ColumnInfo(name = "coinForContent")
     var coinForContent: String? = null

    @SerializedName("coins")
    @Expose
    @ColumnInfo(name = "coins")
    var coins: String? = null

    @SerializedName("freePaid")
    @Expose
    @ColumnInfo(name = "freePaid")
    private var freePaid: String? = null

    @SerializedName("purchased")
    @Expose
    @ColumnInfo(name = "purchased")
     var purchased: Boolean? = null

    @SerializedName("rewarded")
    @Expose
    @ColumnInfo(name = "rewarded")
    var rewarded: Boolean? = null



    @SerializedName("audio")
    @Expose
    @ColumnInfo(name = "audio")
    private var audio: String? = null

    @SerializedName("isPlaying")
    @Expose
    @ColumnInfo(name = "isPlaying")
    private var isPlaying: Boolean? = null

    @Expose
    @ColumnInfo(name = "isFirstTime")
    private var isFirstTime: Boolean? = null


    @SerializedName("banner")
    @Expose
    @ColumnInfo(name = "banner")
     var image: String? = null

    @SerializedName("ads")
    @Expose
    @ColumnInfo(name = "ads")
    var isAds: Boolean? = false

    @SerializedName("adLink")
    @Expose
    @ColumnInfo(name = "adLink")
    var adLink: String? = ""

    @SerializedName("adUrl")
    @Expose
    @ColumnInfo(name = "adUrl")
    var adUrl: String? = null


    @SerializedName("adHeight")
    @Expose
    @ColumnInfo(name = "adHeight")
    var adHeight: String? = null

    @SerializedName("adWidth")
    @Expose
    @ColumnInfo(name = "adWidth")
    var adWidth: String? = null

    fun isFirstTime(): Boolean? {
        return isFirstTime
    }

    fun setFirstTime(isFirstTime: Boolean?) {
        this.isFirstTime = isFirstTime
    }

    fun isPlaying(): Boolean? {
        return isPlaying
    }

    fun setPlaying(isPlaying: Boolean?) {
        this.isPlaying = isPlaying
    }

    fun getSoundId(): Int? {
        return soundId
    }

    fun setSoundId(soundId: Int?) {
        this.soundId = soundId
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getSubtitle(): String? {
        return subtitle
    }

    fun setSubtitle(subtitle: String) {
        this.subtitle = subtitle
    }

    fun getMinutes(): String? {
        return minutes
    }

    fun setMinutes(minutes: String) {
        this.minutes = minutes
    }

    fun getSeconds(): String? {
        return seconds
    }

    fun setSeconds(seconds: String) {
        this.seconds = seconds
    }

    fun getFreePaid(): String? {
        return freePaid
    }


    fun setFreePaid(freePaid: String) {
        this.freePaid = freePaid
    }

    fun getAudio(): String? {
        return audio
    }

    fun setAudio(audio: String) {
        this.audio = audio
    }

}