package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HomeResponse {

    @SerializedName("profile")
    @Expose
    private var profile: Profile? = null

    @SerializedName("courseList")
    @Expose
    private var courseList: List<CourseListModel>? = null

    @SerializedName("momentList")
    @Expose
    private var momentList: List<MomentListModel>? = null

    @SerializedName("soundList")
    @Expose
    private var soundList: List<SoundListModel>? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    @SerializedName("balance")
    @Expose
     var balance: String? = null

    @SerializedName("walletId")
    @Expose
     var walletId: String? = null


    @SerializedName("home")
    @Expose
    private var home: HomeModel? = null

    fun getProfile(): Profile? {
        return profile
    }

    fun setProfile(profile: Profile) {
        this.profile = profile
    }

    fun getCourseList(): List<CourseListModel>? {
        return courseList
    }

    fun setCourseList(courseList: List<CourseListModel>) {
        this.courseList = courseList
    }

    fun getMomentList(): List<MomentListModel>? {
        return momentList
    }

    fun setMomentList(momentList: List<MomentListModel>) {
        this.momentList = momentList
    }

    fun getSoundList(): List<SoundListModel>? {
        return soundList
    }

    fun setSoundList(soundList: List<SoundListModel>) {
        this.soundList = soundList
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }

    fun getHome(): HomeModel? {
        return home
    }

    fun setHome(home: HomeModel) {
        this.home = home
    }
}