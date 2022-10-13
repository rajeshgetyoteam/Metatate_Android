package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindfulness.greece.model.MeditationStateModel


class LibraryModel {
    @SerializedName("meditationState")
    @Expose
    private var meditationState: MeditationStateModel? = null

    @SerializedName("courseList")
    @Expose
    private var courseList: List<CourseListModel>? = null

    @SerializedName("momentList")
    @Expose
    private var momentList: List<MomentListModel>? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getMeditationState(): MeditationStateModel? {
        return meditationState
    }

    fun setMeditationState(meditationState: MeditationStateModel) {
        this.meditationState = meditationState
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

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }
}