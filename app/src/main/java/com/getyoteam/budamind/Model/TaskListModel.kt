package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindfulness.greece.model.MeditationStateModel


class TaskListModel {

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("moment")
    @Expose
    var moment: MomentListModel? = null

    @SerializedName("chapter")
    @Expose
    var chapter: ChapterListModel? = null

    @SerializedName("courseData")
    @Expose
    var courseData: TaskCourseListModel? = null

    @SerializedName("sounds")
    @Expose
    var sounds: SoundListModel? = null



}