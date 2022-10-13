package com.getyoteam.budamind.Model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.jetbrains.annotations.NotNull

@Entity
class PurchaseModel {
    @SerializedName("orderId")
    @Expose
    @NotNull
    @PrimaryKey
    private var orderId: String? = null

    @SerializedName("packageName")
    @Expose
    @ColumnInfo(name = "packageName")
    private var packageName: String? = null

    @SerializedName("productId")
    @Expose
    @ColumnInfo(name = "productId")
    private var productId: String? = null

    @SerializedName("purchaseTime")
    @Expose
    @ColumnInfo(name = "purchaseTime")
    private var purchaseTime: Long? = null

    @SerializedName("purchaseState")
    @Expose
    @ColumnInfo(name = "purchaseState")
    private var purchaseState: Int? = null

    @SerializedName("purchaseToken")
    @Expose
    @ColumnInfo(name = "purchaseToken")
    private var purchaseToken: String? = null

    @SerializedName("acknowledged")
    @Expose
    @ColumnInfo(name = "acknowledged")
    private var acknowledged: Boolean? = null

    fun getOrderId(): String? {
        return orderId
    }

    fun setOrderId(orderId: String) {
        this.orderId = orderId
    }

    fun getPackageName(): String? {
        return packageName
    }

    fun setPackageName(packageName: String) {
        this.packageName = packageName
    }

    fun getProductId(): String? {
        return productId
    }

    fun setProductId(productId: String) {
        this.productId = productId
    }

    fun getPurchaseTime(): Long? {
        return purchaseTime
    }

    fun setPurchaseTime(purchaseTime: Long?) {
        this.purchaseTime = purchaseTime
    }

    fun getPurchaseState(): Int? {
        return purchaseState
    }

    fun setPurchaseState(purchaseState: Int?) {
        this.purchaseState = purchaseState
    }

    fun getPurchaseToken(): String? {
        return purchaseToken
    }

    fun setPurchaseToken(purchaseToken: String) {
        this.purchaseToken = purchaseToken
    }

    fun getAcknowledged(): Boolean? {
        return acknowledged
    }

    fun setAcknowledged(acknowledged: Boolean?) {
        this.acknowledged = acknowledged
    }
}