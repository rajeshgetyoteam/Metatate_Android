package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class responceUserModel {

    @SerializedName("result")
    @Expose
    var result: UserDataModel? = null

    @SerializedName("walletStatus")
    @Expose
    var walletStatus: String? = null

    @SerializedName("credited")
    @Expose
    var credited: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null


}