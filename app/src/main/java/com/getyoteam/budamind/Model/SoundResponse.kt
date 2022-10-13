package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SoundResponse {

    @SerializedName("soundList")
    @Expose
    var soundList: List<SoundListModel>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

}