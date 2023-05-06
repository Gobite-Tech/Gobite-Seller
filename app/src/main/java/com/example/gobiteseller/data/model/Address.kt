package com.example.gobiteseller.data.model

data class Address(
    val area: String,
    val city: String,
    val lat: Double,
    val lng: Double,
    val pincode: String,
    val state: String,
    val street: String
)