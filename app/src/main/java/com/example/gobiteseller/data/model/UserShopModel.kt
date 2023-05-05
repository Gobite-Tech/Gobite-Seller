package com.example.gobiteseller.data.model
import com.google.gson.annotations.SerializedName


data class UserShopModel(
    @SerializedName("shopModel")
    val shopModel: ShopModel,
    @SerializedName("userModel")
    val userModel: UserModel
)

