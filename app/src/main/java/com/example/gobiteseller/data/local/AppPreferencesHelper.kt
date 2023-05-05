package com.example.gobiteseller.data.local

import com.example.gobiteseller.data.model.ShopConfigurationModel
import com.example.gobiteseller.data.model.UserModel

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val shop: String?
    val currentShop: Int?
    val id: Int?
    val fcmToken: String?
    val tempMobile: String?
    val tempOauthId: String?


    var isFCMTokenUpdated: Boolean?
    var isFCMTopicSubScribed: Boolean?
    var orderStatusChanged: Boolean


    var updateItemRequest: String
    var deleteItemRequest: Int


    fun saveUser(id: Int?,name: String?,email: String?, mobile: String?, role: String?, oauthId: String?, shop: String?)

    fun clearPreferences()

    fun getShop():List<ShopConfigurationModel>?

    fun getUser(): UserModel?
}