package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindfulness.greece.model.MeditationStateModel


class TaskDataModel {

    @SerializedName("itemId")
    @Expose
    var itemId: String? = null


    @SerializedName("chapterId")
    @Expose
    var chapterId: String? = null

    @SerializedName("titleText")
    @Expose
    var titleText: String? = null

    @SerializedName("claim")
    @Expose
    var claim: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("coinForContent")
    @Expose
    var coinForContent: String? = null

    @SerializedName("coins")
    @Expose
    var coins: String? = null


    @SerializedName("moment")
    @Expose
    var moment: MomentListModel? = null

    @SerializedName("courses")
    @Expose
    var courses: ChapterListModel? = null

    @SerializedName("courseData")
    @Expose
    var courseData: CourseListModel? = null


    @SerializedName("sounds")
    @Expose
    var sounds: SoundListModel? = null




}