package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList


class WallateResponceModel {
    @SerializedName("walletId")
    @Expose
     var walletId: String? = null

    @SerializedName("balance")
    @Expose
     var balance: String? = null

    @SerializedName("withdrawEnable")
    @Expose
    var isWithdrawEnable: Boolean? = null

    @SerializedName("today_coins")
    @Expose
     var today_coins: String? = null

    @SerializedName("totalEarn")
    @Expose
    var totalEarn: String? = null

    @SerializedName("history")
    @Expose
     var history: ArrayList<WallateHistoryModel>? = null

    @SerializedName("status")
    @Expose
     var status: String? = null





}