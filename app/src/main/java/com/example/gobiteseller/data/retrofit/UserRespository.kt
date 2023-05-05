package com.example.gobiteseller.data.retrofit

import com.example.gobiteseller.data.model.LoginRequest
import com.example.gobiteseller.data.model.SignUpRequest
import com.example.gobiteseller.data.model.UserModel
import com.example.gobiteseller.data.model.otpRequest
import retrofit2.Retrofit

class UserRespository(retofit: Retrofit) {
    val service = retofit.create(CustomApi::class.java)

    suspend fun loginNew(loginReq:LoginRequest) = service.loginNew(loginReq)

    suspend fun signUpNew(signupReq:SignUpRequest)=service.signUpNew(signupReq)

    suspend fun otpNew(otpReq:otpRequest)=service.otpNew(otpReq)

    suspend fun login(userModel: UserModel) = service.login(userModel)

    suspend fun updateProfile(userModel: UserModel) = service.updateProfile(userModel)

    suspend fun updateFcmToken(userModel: UserModel) = service.updateFcmToken(userModel)
}