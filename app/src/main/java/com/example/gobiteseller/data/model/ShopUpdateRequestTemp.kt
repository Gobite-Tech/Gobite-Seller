package com.example.gobiteseller.data.model

data class ShopUpdateRequestTemp(
    val account_holder: String?,
    val account_ifsc: String?,
    val account_number: String?,
    val area: String?,
    val avg_serve_time: Int?,
    val category: String?,
    val city: String?,
    val closing_time: String?,
    val cover_photos: List<CoverPhoto>?,
    val description: String?,
    val email: String?,
    val gst: String?,
    val icon: String?,
    val lat: Double?,
    val lng: Double?,
    val mobile: String?,
    val name: String?,
    val opening_time: String?,
    val pan: String?,
    val pincode: String?,
    val state: String?,
    val street: String?,
    val tags: List<String>?,
    val telephone: String?
)