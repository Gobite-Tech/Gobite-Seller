package com.example.gobiteseller.data.model

data class ShopUpdateRequestTemp(
    val account_holder: String?="account_holder",
    val account_ifsc: String?="account_ifsc",
    val account_number: String?="account_number",
    val area: String?="IITK",
    val avg_serve_time: Int?=0,
    val category: String?="food",
    val city: String?="KANPUR",
    val closing_time: String?="",
    val cover_photos: List<CoverPhoto>?,
    val description: String?="description",
    val email: String?="email",
    val gst: String?="gst",
    val icon: String?="",
    val lat: Double?,
    val lng: Double?,
    val mobile: String?="mobile",
    val name: String?="name",
    val opening_time: String?="",
    val pan: String?="pan",
    val pincode: String?="pincode",
    val state: String?="UP",
    val street: String?="IITK",
    val tags: List<String>?,
    val telephone: String?="telephone"
)