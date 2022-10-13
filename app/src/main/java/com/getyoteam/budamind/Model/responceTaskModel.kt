package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class responceTaskModel {

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("reward")
    @Expose
    var creditedCoins: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null


}