package com.example.gobiteseller.data.model

data class OrderX(
    val created_at: String,
    val customer_id: Int,
    val customer_name:String,
    val deleted: Boolean,
    val id: String,
    val items: List<ItemXX>,
    val meta: MetaXX,
    val order_placed_time: String,
    val order_status: String,
    val payment_status: String,
    val price: Double,
    val rating: Double,
    val shop_id: Int,
    val tax: Double,
    val transactions: List<Any>,
    val updated_at: String
)