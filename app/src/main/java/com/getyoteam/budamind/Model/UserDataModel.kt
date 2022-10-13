package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class   UserDataModel {

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("profilePic")
    @Expose
    var profilePic: String? = null

    @SerializedName("authToken")
    @Expose
    var authToken: String? = null

    @SerializedName("userId")
    @Expose
    var userId: Int? = null

    @SerializedName("email")
    @Expose
    var email: String? = null


    @SerializedName("anxiety")
    @Expose
    var anxiety: Int? = null

    @SerializedName("focus")
    @Expose
    var focus: Int? = null

    @SerializedName("gratitute")
    @Expose
    var gratitute: Int? = null

    @SerializedName("happiness")
    @Expose
    var happiness: Int? = null

    @SerializedName("meditate")
    @Expose
    var meditate: Int? = null

    @SerializedName("selfEsteem")
    @Expose
    var selfEsteem: Int? = null

    @SerializedName("sleep")
    @Expose
    var sleep: Int? = null

    @SerializedName("stress")
    @Expose
    var stress: Int? = null

}