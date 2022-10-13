package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


class WallateHistoryModel {
    @SerializedName("itemId")
    @Expose
     var itemId: String? = null

    @SerializedName("coins")
    @Expose
     var coins: String? = null

    @SerializedName("description")
    @Expose
     var description: String? = null


    @SerializedName("type")
    @Expose
     var type: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null


}