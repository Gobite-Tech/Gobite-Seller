package com.example.gobiteseller.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class UserInviteModel (
    @SerializedName("userModel")
    val userModel: UserModel,
    @SerializedName("shopModel")
    val shopModel: ShopModel,
    @SerializedName("date")
    val date: Date
)
