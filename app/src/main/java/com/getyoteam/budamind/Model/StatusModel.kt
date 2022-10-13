package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class StatusModel {

    @SerializedName("message")
    @Expose
     var message: String? = null

    @SerializedName("status")
    @Expose
     var status: String? = null

    @SerializedName("otp")
    @Expose
     var otp: String? = null



}