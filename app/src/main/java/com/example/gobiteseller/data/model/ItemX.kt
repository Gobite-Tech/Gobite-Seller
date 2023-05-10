package com.example.gobiteseller.data.model

data class ItemX(
    val actual_price: Double,
    val id: String,
    val item_id: String,
    val item_name: String,
    val item_variant_id: String,
    val item_variant_name: String,
    val item_variant_value: String,
    val meta: MetaX,
    val quantity: Int,
    val total_price: Double
)