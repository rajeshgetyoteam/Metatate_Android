package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ProductListModel {
    @SerializedName("productId")
    @Expose
     var productId: String? = null

    @SerializedName("productName")
    @Expose
     var productName: String? = null

    @SerializedName("productImage")
    @Expose
    var productImage: Int? = null


}