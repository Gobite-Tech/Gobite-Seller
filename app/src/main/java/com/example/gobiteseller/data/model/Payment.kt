package com.example.gobiteseller.data.model

data class Payment(
    val account_holder: String,
    val account_ifsc: String,
    val account_number: String,
    val gst: String,
    val pan: String
)