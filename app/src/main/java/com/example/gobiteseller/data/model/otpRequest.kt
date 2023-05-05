package com.example.gobiteseller.data.model

data class otpRequest(
    val auth_token: String,
    val name: String,
    val otp: String,
    val password: String,
    val password_confirmation: String
)