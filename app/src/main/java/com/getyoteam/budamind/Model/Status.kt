package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


open class Status {
    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("status")
    @Expose
    var status: String? = null


}