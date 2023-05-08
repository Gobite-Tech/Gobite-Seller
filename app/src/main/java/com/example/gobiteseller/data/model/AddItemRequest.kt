package com.example.gobiteseller.data.model

data class AddItemRequest(
    val category: String,
    val status:String,
    val description: String,
    val filterable_fields: List<Any>,
    val item_type: String,
    val name: String
)