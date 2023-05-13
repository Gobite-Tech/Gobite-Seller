package com.example.gobiteseller.data.model

data class Payment(
    var account_holder: String,
    var account_ifsc: String,
    var account_number: String,
    val gst: String,
    val pan: String
)