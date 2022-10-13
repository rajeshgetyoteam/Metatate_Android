package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class responceRewarModel {

    @SerializedName("reward")
    @Expose
    var reward: String? = null

    @SerializedName("creditedCoins")
    @Expose
    var creditedCoins: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null


}