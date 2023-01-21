package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class SizeListModel {
    @SerializedName("productId")
    @Expose
     var productId: String? = null

    @SerializedName("productName")
    @Expose
     var productName: String? = null


}