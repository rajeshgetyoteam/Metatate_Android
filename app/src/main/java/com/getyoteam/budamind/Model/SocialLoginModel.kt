package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SocialLoginModel(
    pFirstName: String, pLastName: String, pCustomerEmail: String,
    pProfilePic: String, pLoginThrough: String,pSocialId: String
) {

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null

    @SerializedName("lastName")
    @Expose
    var lastName: String? = null

    @SerializedName("email")
    @Expose
    var customerEmail: String? = null

    @SerializedName("profilePic")
    @Expose
    var profilePic: String? = null

    @SerializedName("loginFrom")
    @Expose
    var loginThrough: String? = null

    @SerializedName("socialId")
    @Expose
    var socialId: String? = null


    init {
        firstName = pFirstName
        lastName = pLastName
        customerEmail = pCustomerEmail
        profilePic = pProfilePic
        loginThrough = pLoginThrough
        socialId = pSocialId
    }

}