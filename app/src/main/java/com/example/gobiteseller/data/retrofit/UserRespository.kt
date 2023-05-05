package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.UserModel
import retrofit2.Retrofit

class UserRespository(private val retofit: Retrofit) {
    val service = retofit.create(CustomApi::class.java)

    suspend fun login(userModel: UserModel) = service.login(userModel)

    suspend fun updateProfile(userModel: UserModel) = service.updateProfile(userModel)

    suspend fun updateFcmToken(userModel: UserModel) = service.updateFcmToken(userModel)
}