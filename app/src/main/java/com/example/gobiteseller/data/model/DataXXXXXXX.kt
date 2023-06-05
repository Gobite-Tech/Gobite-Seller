package com.example.gobiteseller.data.model

data class DataXXXXXXX(
    val orders: List<OrderX>,
    val page_size: Int,
    val total: Int,
    var next_page_token:String?
)