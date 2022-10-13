package com.getyoteam.budamind.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class QuoteModel {
    @SerializedName("quote")
    @Expose
    private var quote: String? = null

    @SerializedName("status")
    @Expose
    private var status: String? = null

    fun getQuote(): String? {
        return quote
    }

    fun setQuote(quote: String) {
        this.quote = quote
    }

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String) {
        this.status = status
    }
}