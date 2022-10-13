package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class CommonModel {
    @SerializedName("userId")
    @Expose
    private var userId: String? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("authToken")
    @Expose
    private var authToken: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getAuthToken(): String? {
        return authToken
    }

    fun setAuthToken(message: String) {
        this.authToken = authToken
    }


    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }


}