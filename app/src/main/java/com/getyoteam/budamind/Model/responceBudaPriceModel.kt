package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class responceBudaPriceModel {

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("price_date")
    @Expose
    var date: String? = null


}