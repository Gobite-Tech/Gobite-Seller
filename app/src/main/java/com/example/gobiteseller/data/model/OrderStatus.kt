package com.example.gobiteseller.data.model


data class OrderStatus(
    var isDone: Boolean = false,
    var isCurrent: Boolean = false,
    var name: String,
    var orderStatusList: List<OrderStatusItemModel> = listOf()
)
