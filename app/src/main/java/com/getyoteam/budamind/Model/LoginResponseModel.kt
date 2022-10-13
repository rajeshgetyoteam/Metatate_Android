package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginResponseModel(
    pFirstName: String, pLastName: String, pPassword: String, pCustomerEmail: String,
    pProfilePic: String, pLoginThrough: String, pDeviceToken: String, pZone: String
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

    @SerializedName("password")
    @Expose
    var password: String? = null

    @SerializedName("loginFrom")
    @Expose
    var loginThrough: String? = null

    @SerializedName("deviceToken")
    @Expose
    var deviceToken: String? = null

    @SerializedName("zoneId")
    @Expose
    var zoneId: String? = null

    init {
        firstName = pFirstName
        lastName = pLastName
        password = pPassword
        customerEmail = pCustomerEmail
        profilePic = pProfilePic
        loginThrough = pLoginThrough
        deviceToken = pDeviceToken
        zoneId = pZone
    }

}