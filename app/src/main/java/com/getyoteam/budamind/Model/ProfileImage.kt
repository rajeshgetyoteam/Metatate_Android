package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProfileImage {
    @SerializedName("fileUrl")
    @Expose
    private var fileUrl: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getFileUrl(): String? {
        return fileUrl
    }

    fun setFileUrl(fileUrl: String) {
        this.fileUrl = fileUrl
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }
}