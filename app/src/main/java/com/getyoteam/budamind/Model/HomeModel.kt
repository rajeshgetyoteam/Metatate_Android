package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class HomeModel {
    @SerializedName("courseList")
    @Expose
    private var courseList: List<CourseListModel>? = null

    @SerializedName("momentList")
    @Expose
    private var momentList: List<MomentListModel>? = null

    @SerializedName("soundList")
    @Expose
    private var soundList: List<SoundListModel>? = null

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

}