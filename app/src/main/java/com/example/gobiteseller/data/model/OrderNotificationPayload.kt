package com.example.gobiteseller.data.model
import com.google.gson.annotations.SerializedName


data class OrderNotificationPayload(
    @SerializedName("userName")
    val userName: String,
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("itemList")
    val itemList: List<String>,
    @SerializedName("orderType")
    val orderType: String,
    @SerializedName("orderStatus")
    val orderStatus: String
)