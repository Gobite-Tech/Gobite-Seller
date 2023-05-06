package com.example.gobiteseller.data.model

data class Shop(
    val address: Address,
    val avg_serve_time: Int,
    val category: String,
    val closing_time: String,
    val cover_photos: List<Any>,
    val description: String,
    val email: String,
    val icon: String,
    val id: Int,
    val mobile: String,
    val name: String,
    val opening_time: String,
    val payment: Payment,
    val rejected_conversations: List<Any>,
    val status: String,
    val tags: List<String>,
    val telephone: String,
    val updated_at: String
)