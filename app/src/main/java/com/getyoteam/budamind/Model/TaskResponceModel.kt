package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.mindfulness.greece.model.MeditationStateModel
import java.util.*
import kotlin.collections.ArrayList


class TaskResponceModel {

    @SerializedName("meditationState")
    @Expose
     var meditationState: MeditationStateModel? = null
    @SerializedName("tasks")
    @Expose
     var tasks: ArrayList<TaskDataModel>? = null

    @SerializedName("status")
    @Expose
     var status: String? = null





}